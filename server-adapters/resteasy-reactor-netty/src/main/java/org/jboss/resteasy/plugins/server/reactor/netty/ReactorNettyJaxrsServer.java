package org.jboss.resteasy.plugins.server.reactor.netty;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import javax.net.ssl.SSLContext;

import org.jboss.logging.Logger;
import org.jboss.resteasy.core.ResteasyDeploymentImpl;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory;
import org.jboss.resteasy.plugins.server.embedded.EmbeddedJaxrsServer;
import org.jboss.resteasy.plugins.server.embedded.SecurityDomain;
import org.jboss.resteasy.specimpl.ResteasyUriInfo;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.EmbeddedServerHelper;
import org.jboss.resteasy.util.PortProvider;
import org.reactivestreams.Publisher;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.IdentityCipherSuiteFilter;
import io.netty.handler.ssl.JdkSslContext;
import io.netty.handler.ssl.SslContext;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpRequestDecoderSpec;
import reactor.netty.http.server.HttpServer;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

/**
 * A server adapter built on top of <a
 * href='https://github.com/reactor/reactor-netty'>reactor-netty</a>. Similar
 * to the adapter built on top of netty4, this adapter will ultimately run on
 * Netty rails. Leveraging reactor-netty brings 3 main benefits, which are:
 *
 * 1. Reactor-netty's HttpServer + handle(req, resp) API is a little closer
 * match to how a normal HTTP server works. Basically, it should be easier for
 * an HTTP web server person to maintain compared to a raw Netty
 * implementation. However, this assumes you don't have to delve into
 * reactor-netty!
 * 2. Reactor Netty puts a <a href='https://projectreactor.io/'>reactor</a>
 * facade on top of Netty. The Observable+Iterable programming paradigm is
 * more general purpose than Netty's IO-centric Channel concept. In theory, it
 * should be more beneficial to learn:)
 * 3. When paired with a Netty-based client (e.g. the JAX-RS client powered by
 * reactor-netty), the threadpool can be efficiently shared between the client
 * and the server.
 */
public class ReactorNettyJaxrsServer implements EmbeddedJaxrsServer<ReactorNettyJaxrsServer> {
    private static final Logger log = Logger.getLogger(ReactorNettyJaxrsServer.class);

    private final EmbeddedServerHelper serverHelper = new EmbeddedServerHelper();

    protected String hostname = null;
    protected int configuredPort = PortProvider.getPort();
    protected int runtimePort = -1;
    protected String root = "";
    protected ResteasyDeployment deployment;
    protected SecurityDomain domain;

    private Duration idleTimeout;
    private SSLContext sslContext;
    private ClientAuth clientAuth = ClientAuth.REQUIRE;
    private List<Runnable> cleanUpTasks;
    private UnaryOperator<HttpRequestDecoderSpec> mkDecoderSpec = spec -> spec;

    private DisposableServer server;

    private UriExtractor uriExtractor = new UriExtractor();

    @Override
    public ReactorNettyJaxrsServer deploy() {
        return this;
    }

    @Override
    public ReactorNettyJaxrsServer start() {
        log.info("Starting RestEasy Reactor-based server!");
        serverHelper.checkDeployment(deployment);

        final String appPath = serverHelper.checkAppDeployment(deployment);
        if (appPath != null && (root == null || "".equals(root))) {
            setRootResourcePath(appPath);
        }

        final Handler handler = new Handler();

        HttpServer svrBuilder = HttpServer.create()
                .port(configuredPort)
                .httpRequestDecoder(mkDecoderSpec)
                .handle(handler::handle);

        if (idleTimeout != null) {
            svrBuilder = svrBuilder.idleTimeout(idleTimeout);
        }

        if (sslContext != null) {
            svrBuilder = svrBuilder.secure(sslContextSpec -> sslContextSpec.sslContext(toNettySSLContext(sslContext)));
        }
        if (hostname != null && !hostname.trim().isEmpty()) {
            svrBuilder = svrBuilder.host(hostname);
        }

        if (Boolean.parseBoolean(System.getProperty("resteasy.server.reactor-netty.warmup", "true"))) {
            log.info("Warming up the reactor-netty server");
            svrBuilder.warmup().block();
        }

        server = svrBuilder.bindNow();
        runtimePort = server.port();
        return this;
    }

    /**
     * Calling this method will block the current thread.
     */
    public void startAndBlock() {
        start();
        server.onDispose().block();
    }

    class Handler {

        private final Mono<InputStream> empty = Mono.just(new InputStream() {
            @Override
            public int read() {
                return -1; // end of stream
            }
        });

        Publisher<Void> handle(final HttpServerRequest req, final HttpServerResponse resp) {

            final ResteasyUriInfo info = extractUriInfo(req, root);

            // aggregate (and maybe? asInputStream) reads the entire request body into memory (direct?)
            // Can we stream it in some way?
            // https://stackoverflow.com/a/51801335/2071683 but requires a thread.  Isn't using a thread
            // per request even if from the elastic pool a big problem???  I mean we are trying to reduce
            // threads!
            // I honestly don't know what the Netty4 adapter is doing here.  When
            // I try to send a large body it says "request payload too large".  I
            // don't know if that's configurable or not..

            // This is a subscription tied to the completion writing the response.
            final Sinks.Empty<Void> completionSink = Sinks.empty();

            final AtomicBoolean isTimeoutSet = new AtomicBoolean(false);

            final ReactorNettyHttpResponse resteasyResp = new ReactorNettyHttpResponse(req.method(), resp, completionSink);

            return req.receive()
                    .aggregate()
                    .asInputStream()
                    .doOnDiscard(InputStream.class, is -> {
                        try {
                            is.close();
                        } catch (final IOException ie) {
                            log.error("Problem closing discarded input stream", ie);
                        }
                    }).switchIfEmpty(empty)
                    .flatMap(body -> {

                        // These next 2 classes, along with ReactorNettyHttpResponse provide the main '1-way bridges'
                        // between reactor-netty and RestEasy.
                        final SynchronousDispatcher dispatcher = (SynchronousDispatcher) deployment.getDispatcher();

                        final ReactorNettyHttpRequest resteasyReq = new ReactorNettyHttpRequest(info, req, body, resteasyResp,
                                dispatcher);

                        ResteasyProviderFactory defaultInstance = ResteasyProviderFactory.getInstance();
                        if (defaultInstance instanceof ThreadLocalResteasyProviderFactory) {
                            ThreadLocalResteasyProviderFactory.push(deployment.getProviderFactory());
                        }

                        try {
                            // This is what actually kicks RestEasy into action.
                            deployment.getDispatcher().invoke(resteasyReq, resteasyResp);
                        } finally {
                            //Runs clean up tasks after request is processed
                            if (cleanUpTasks != null) {
                                cleanUpTasks.forEach(Runnable::run);
                            }
                        }

                        if (defaultInstance instanceof ThreadLocalResteasyProviderFactory) {
                            ThreadLocalResteasyProviderFactory.pop();
                        }

                        if (!resteasyReq.getAsyncContext().isSuspended()) {
                            try {
                                resteasyResp.close();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        final Mono<Void> actualMono = Optional.ofNullable(resteasyReq.timeout())
                                .map(timeout -> {
                                    isTimeoutSet.set(true);
                                    return completionSink.asMono().timeout(resteasyReq.timeout());
                                })
                                .orElse(completionSink.asMono());

                        return actualMono
                                .doFinally(s -> {
                                    try {
                                        body.close();
                                    } catch (final IOException ioe) {
                                        log.error("Failure to close the request's input stream.", ioe);
                                    }
                                });
                    }).onErrorResume(t -> {
                        if (!resteasyResp.isCommitted()) {
                            final Mono<Void> sendMono;

                            if (isTimeoutSet.get() && Exceptions.unwrap(t) instanceof TimeoutException) {
                                sendMono = resp.status(503).send();
                            } else {
                                log.error("Unhandled server error.", t);
                                sendMono = resp.status(500).send();
                            }
                            SinkSubscriber.subscribe(completionSink, sendMono);
                        } else {
                            log.error("Unhandled server error, JAXRS response committed.", t);
                        }

                        return completionSink.asMono();
                    });
        }

    }

    @Override
    public void stop() {
        runtimePort = -1;
        server.disposeNow();
        if (deployment != null) {
            deployment.stop();
        }
    }

    @Override
    public ResteasyDeployment getDeployment() {
        if (deployment == null) {
            deployment = new ResteasyDeploymentImpl();
        }
        return deployment;
    }

    @Override
    public ReactorNettyJaxrsServer setDeployment(ResteasyDeployment deployment) {
        this.deployment = deployment;
        return this;
    }

    @Override
    public ReactorNettyJaxrsServer setPort(int port) {
        this.configuredPort = port;
        return this;
    }

    public int getPort() {
        return runtimePort > 0 ? runtimePort : configuredPort;
    }

    @Override
    public ReactorNettyJaxrsServer setHostname(String hostname) {
        this.hostname = hostname;
        return this;
    }

    public String getHostname() {
        return hostname;
    }

    @Override
    public ReactorNettyJaxrsServer setRootResourcePath(String rootResourcePath) {
        root = Objects.requireNonNull(rootResourcePath);
        if (root != null && root.equals("/")) {
            root = "";
        } else if (!root.startsWith("/")) {
            root = "/" + root;
        }
        return this;
    }

    @Override
    public ReactorNettyJaxrsServer setSecurityDomain(SecurityDomain sc) {
        this.domain = sc;
        return this;
    }

    public ReactorNettyJaxrsServer setIdleTimeout(final Duration idleTimeout) {
        this.idleTimeout = idleTimeout;
        return this;
    }

    public ReactorNettyJaxrsServer setSSLContext(final SSLContext sslContext) {
        Objects.requireNonNull(sslContext);
        this.sslContext = sslContext;
        return this;
    }

    public ReactorNettyJaxrsServer setClientAuth(final ClientAuth clientAuth) {
        Objects.requireNonNull(clientAuth);
        this.clientAuth = clientAuth;
        return this;
    }

    /**
     * Sets clean up tasks that are needed immediately after {@link org.jboss.resteasy.spi.Dispatcher#invoke} yet before
     * any asynchronous asynchronous work is continued by the reactor-netty server. Since these run on the Netty event
     * loop threads, it is important that they run fast (not block). It is expected that you take special care with
     * exceptions. This is useful in certain cases where servlet Filters have options that are hard to achieve with the
     * pure JAX-RS API, such as:
     * <code>
     *  doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) {
     *     establishThreadLocals();
     *     chain.doFilter(req, resp, chain);
     *     clearThreadLocals();
     *  }
     * </code>
     *
     * @param cleanUpTasks List of clean up tasks
     * @return ReactorNettyJaxrsServer
     */
    public ReactorNettyJaxrsServer setCleanUpTasks(final List<Runnable> cleanUpTasks) {
        this.cleanUpTasks = cleanUpTasks;
        return this;
    }

    /**
     * @see HttpServer#httpRequestDecoder(Function).
     *
     * @param decoderSpecFn
     */
    public void setDecoderSpecFn(UnaryOperator<HttpRequestDecoderSpec> decoderSpecFn) {
        this.mkDecoderSpec = decoderSpecFn;
    }

    private SslContext toNettySSLContext(final SSLContext sslContext) {
        Objects.requireNonNull(sslContext);
        return new JdkSslContext(
                sslContext,
                false,
                null,
                IdentityCipherSuiteFilter.INSTANCE,
                null,
                clientAuth,
                null,
                false);
    }

    static class UriExtractor {
        ResteasyUriInfo extract(final HttpServerRequest req, final String contextPath) {
            final String uri = req.uri();

            final String uriString;

            // If we have an absolute URL, don't try to recreate it from the host and request line.
            if (uri.startsWith(req.scheme() + "://")) {
                uriString = uri;
            } else {
                String host = req.requestHeaders().get(HttpHeaderNames.HOST);
                if (host == null || "".equals(host.trim())) {
                    final InetSocketAddress hostAddress = req.hostAddress();
                    if (hostAddress == null) {
                        throw new IllegalArgumentException("Could not determine host address from request.  " +
                                "This should never happen.");
                    }
                    host = hostAddress.getHostString() + ":" + hostAddress.getPort();
                }
                uriString = new StringBuilder(100)
                        .append(req.scheme())
                        .append("://")
                        .append(host)
                        .append(req.uri())
                        .toString();
            }

            return new ResteasyUriInfo(uriString, contextPath);
        }
    }

    private ResteasyUriInfo extractUriInfo(final HttpServerRequest req, final String contextPath) {
        return uriExtractor.extract(req, contextPath);
    }

}
