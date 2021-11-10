package org.jboss.resteasy.client.jaxrs.engines;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.BiFunction;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.InvocationCallback;
import jakarta.ws.rs.client.ResponseProcessingException;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Providers;

import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import static java.util.Objects.requireNonNull;

import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.internal.ClientConfiguration;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
import org.jboss.resteasy.client.jaxrs.internal.ClientRequestHeaders;
import org.jboss.resteasy.client.jaxrs.internal.ClientResponse;
import org.jboss.resteasy.client.jaxrs.internal.TrackingClientRequestHeaders;
import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.tracing.RESTEasyTracingLogger;
import static org.jboss.resteasy.util.HttpHeaderNames.CONTENT_LENGTH;

import org.jboss.resteasy.util.CaseInsensitiveMap;
import org.jboss.resteasy.client.jaxrs.internal.TrackingMap;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.client.HttpClientResponse;
import reactor.netty.resources.ConnectionProvider;

public class ReactorNettyClientHttpEngine implements ReactiveClientHttpEngine {
    private static final Logger log = Logger.getLogger(ReactorNettyClientHttpEngine.class);

    private final HttpClient httpClient;
    private final ChannelGroup channelGroup;
    private final ConnectionProvider connectionProvider;
    private final Optional<Duration> requestTimeout;
    private final BiFunction<ClientConfiguration, InputStream, ClientResponse> fnClientResponse;

    /**
     * Constructor for ReactorNettyClientHttpEngine
     *
     * @param httpClient         The {@link HttpClient} instance to be used by this {@link AsyncClientHttpEngine}
     * @param channelGroup       The {@link ChannelGroup} instance used by the provided {@link HttpClient}
     * @param connectionProvider The {@link ConnectionProvider} instance used to create the provided {@link HttpClient}
     * @param requestTimeout     The {@link Optional<Duration>} instance used to configure requestTimeout on response
     * @param useFinalizedResponse Used to configure for using {@link RestEasyClientResponse} with `finalize` method
     */
    private ReactorNettyClientHttpEngine(final HttpClient httpClient,
                                         final ChannelGroup channelGroup,
                                         final ConnectionProvider connectionProvider,
                                         final Optional<Duration> requestTimeout,
                                         final Boolean useFinalizedResponse) {
        this.httpClient = requireNonNull(httpClient);
        this.channelGroup = requireNonNull(channelGroup);
        this.connectionProvider = requireNonNull(connectionProvider);
        this.requestTimeout = requireNonNull(requestTimeout);

        requestTimeout
                .ifPresent( duration -> {
                    if(duration.isNegative())
                        throw new IllegalArgumentException("Required positive value for requestTimeout");
                    if(duration.isZero())
                        throw new IllegalArgumentException("Required non zero value for requestTimeout");
                });

        this.fnClientResponse = useFinalizedResponse ?
                FinalizedRestEasyClientResponse::new
                : RestEasyClientResponse::new;
    }

    public ReactorNettyClientHttpEngine(final HttpClient httpClient,
                                        final ChannelGroup channelGroup,
                                        final ConnectionProvider connectionProvider) {
        this(httpClient, channelGroup, connectionProvider, Optional.empty(), false);
    }

    public ReactorNettyClientHttpEngine(final HttpClient httpClient,
                                        final ChannelGroup channelGroup,
                                        final ConnectionProvider connectionProvider,
                                        final Duration requestTimeout) {
        this(httpClient, channelGroup, connectionProvider, Optional.of(requestTimeout), false);
    }

    public ReactorNettyClientHttpEngine(final HttpClient httpClient,
                                        final ChannelGroup channelGroup,
                                        final ConnectionProvider connectionProvider,
                                        final Boolean useResponseFinalize) {
        this(httpClient, channelGroup, connectionProvider, Optional.empty(), useResponseFinalize);
    }

    public ReactorNettyClientHttpEngine(final HttpClient httpClient,
                                        final ChannelGroup channelGroup,
                                        final ConnectionProvider connectionProvider,
                                        final Duration requestTimeout,
                                        final Boolean useResponseFinalize) {
        this(httpClient, channelGroup, connectionProvider, Optional.of(requestTimeout), useResponseFinalize);
    }

    @Override
    public <T> Mono<T> submitRx(final ClientInvocation request,
                                 final boolean buffered,
                                 final ResultExtractor<T> extractor) {
        final Mono<ClientResponse> responseMono =
            send(request)
                .responseSingle((response, bytes) -> bytes
                        .asInputStream()
                        .map(is -> toRestEasyResponse(request.getClientConfiguration(), response, is))
                        .switchIfEmpty(
                                Mono.defer(
                                        () -> Mono.just(
                                                toRestEasyResponse(
                                                        request.getClientConfiguration(),
                                                        response,
                                                        null)))
                        )
                        // If RestEasyClientResponse or InputStream is in buffer of an operator when
                        // the stream is closed with an error or cancellation, make sure
                        // to close it so that underlying resources (InputStream, Netty ByteBuf, etc)
                        // are released.
                        .doOnDiscard(InputStream.class, is -> {
                            try {
                                is.close();
                            } catch (IOException e) {
                                log.warn("Not able to close InputStream.  This may lead to direct memory leaks", e);
                            }
                        })
                        .doOnDiscard(RestEasyClientResponse.class, RestEasyClientResponse::close)
                );

        return requestTimeout
                .map(responseMono::timeout)
                .orElse(responseMono)
                .handle((response, sink) -> {
                    try {
                        sink.next(extractor.extractResult(response));
                    } catch (final Exception e) {
                        try {
                            // We release the connection instead of closing it because the WebApplicationException
                            // *may* make use of the response.  However, since we are releasing the
                            // connection here, handlers of the rethrown exception will not be able
                            // to use the stream (we ASSume buffering happened).  An alternative is
                            // to force handlers to be responsible for closing the response; however,
                            // that does open the possibility of leaks..  But then again, so do
                            // several paths you can take with the Client API.
                            response.releaseConnection();
                        } catch (final IOException ie) {
                            log.warn("There was a problem releasing the connection in an error scenario.", ie);
                        }
                        sink.error(e);
                    }
                });
    }

    /**
     * The main business logic mapping RestEasy's {@link ClientInvocation request} to Reactor Netty's concept
     * of it.
     */
    private HttpClient.ResponseReceiver<?> send(final ClientInvocation resteasyReq) {
        final Optional<Object> reqPayload = Optional.ofNullable(resteasyReq.getEntity());

        final HttpClient.RequestSender requestSender = httpClient.headers(headers -> addHeaders(resteasyReq, headers))
                    .request(HttpMethod.valueOf(resteasyReq.getMethod()))
                    .uri(resteasyReq.getUri().toString());

        return reqPayload.<HttpClient.ResponseReceiver<?>>map(ignore -> {
            return requestSender.send((reactorReq, outbound) -> {
                final ByteBufOutputStream byteBufOutputStream =
                    new ByteBufOutputStream(outbound.alloc().buffer());

                /* Replacing the ClientRequestHeaders with TrackingClientRequestHeaders
                to track the changes to headers by the sendRequestBody method. */
                resteasyReq.setHeaders(
                    new TrackingClientRequestHeaders(
                        resteasyReq.getClientConfiguration(),
                        resteasyReq.getHeaders().getHeaders()
                    )
                );

                try {
                    sendRequestBody(resteasyReq, byteBufOutputStream);
                } catch (final IOException ioe) {
                    return Mono.error(ioe);
                }

                // Updating the HttpClientRequest with the headers modified by the sendRequestBody method.
                final TrackingMap<?> trackingMap = (TrackingMap<?>) resteasyReq.getHeaders().getHeaders();
                trackingMap.getAddedOrUpdatedKeys()
                    .forEach(key -> updateHeader(
                            key,
                            resteasyReq.getHeaders().getHeaders(),
                            reactorReq.requestHeaders()
                        )
                    );
                trackingMap.getRemovedKeys()
                    .forEach(reactorReq.requestHeaders()::remove);

                final int length = byteBufOutputStream.writtenBytes();
                reactorReq.header(CONTENT_LENGTH, Integer.toString(length));

                if (log.isDebugEnabled() &&
                    isContentLengthInvalid(
                        resteasyReq.getHeaders().getHeader(CONTENT_LENGTH), length)) {

                    log.debug("The request's Content-Length header is replaced " +
                        " by the size of the byte array computed from the request entity.");
                }

                return outbound.send(Mono.defer(() -> Mono.just(byteBufOutputStream.buffer())));
            });
        }).orElse(requestSender);
    }

    private static void addHeaders(final ClientInvocation resteasyReq, final HttpHeaders reactorHeaders) {
        final ClientRequestHeaders resteasyHeaders = resteasyReq.getHeaders();
        resteasyHeaders.getHeaders().entrySet().forEach(entry -> {
            final String key = entry.getKey();
            final List<Object> valueList = entry.getValue();
            valueList.forEach(value -> reactorHeaders.add(key, value != null ? value : ""));
        });
    }

    private static void updateHeader(final String key,
                                     final CaseInsensitiveMap<Object> headers,
                                     final HttpHeaders reactorHeaders) {
        List<Object> valueList  = headers.get(key);
        reactorHeaders.set(key, valueList);
    }

    @Override
    public <T> Mono<T> fromCompletionStage(final CompletionStage<T> cs) {
        return Mono.fromCompletionStage(() -> cs);
    }

    @Override
    public <T> Mono<T> just(final T t) {
        return Mono.just(t);
    }

    @Override
    public Mono error(final Exception e) {
        return Mono.error(e);
    }

    @Override
    public <T> Future<T> submit(final ClientInvocation request,
        final boolean buffered,
        final InvocationCallback<T> callback,
        final ResultExtractor<T> extractor) {

        return submit(request, buffered, extractor, null)
            .whenComplete((response, throwable) -> {
                if(callback != null) {
                    if (throwable != null) callback.failed(throwable);
                    else callback.completed(response);
                }
            });
    }

    @Override
    public <K> CompletableFuture<K> submit(final ClientInvocation request,
        final boolean buffered,
        final ResultExtractor<K> extractor,
        final ExecutorService executorService
    ) {
        return submitRx(request, buffered, extractor).toFuture();
    }

    private static boolean isContentLengthInvalid(final String headerValue, final int length) {

        try {
            return headerValue != null && Long.parseLong(headerValue) != length;
        } catch (Exception e) {
            log.warn("Problem parsing the Content-Length header value.", e);
        }
        return true;
    }

    @Override
    public SSLContext getSslContext() {
        // reactor-netty does not allow to access the ssl-context from HttpClient API.
        throw new UnsupportedOperationException();
    }

    @Override
    public HostnameVerifier getHostnameVerifier() {
        // reactor-netty does not support HostnameVerifier API.
        throw new UnsupportedOperationException();
    }

    @Override
    public Response invoke(Invocation request) {
        final Future<ClientResponse> future = submit((ClientInvocation) request, false, null, response -> response);

        try {
            return future.get();
        } catch (InterruptedException e) {
            future.cancel(true);
            throw clientException(e, null);
        } catch (ExecutionException e) {
            throw clientException(e.getCause(), null);
        }
    }

    @Override
    public void close() {
        try {
            channelGroup.close().await();
        } catch (InterruptedException e) {
            log.warn("Exception while closing Netty ChannelGroup", e);
            // What can we do other than swallowing?
        } finally {
            connectionProvider.disposeLater().block();
        }
    }

    static RuntimeException clientException(Throwable ex, Response clientResponse) {
        RuntimeException ret;
        if (ex == null) {
            ret = new ProcessingException(new NullPointerException());
        } else if (ex instanceof WebApplicationException) {
            ret = (WebApplicationException) ex;
        } else if (ex instanceof ProcessingException) {
            ret = (ProcessingException) ex;
        } else if (clientResponse != null) {
            ret = new ResponseProcessingException(clientResponse, ex);
        } else {
            ret = new ProcessingException(ex);
        }
        return ret;
    }

    private static void sendRequestBody(final ClientInvocation req, final ByteBufOutputStream out) throws IOException {
        req.getDelegatingOutputStream().setDelegate(out);

        if (ResteasyContext.getContextData(Providers.class) == null) {
            try (ResteasyContext.CloseableContext cc = pushProvidersContext(req)) {
                req.writeRequestBody(req.getEntityStream());
            }
        } else {
            req.writeRequestBody(req.getEntityStream());
        }
    }

    private static ResteasyContext.CloseableContext pushProvidersContext(final ClientInvocation req) {
        ResteasyContext.CloseableContext ret = ResteasyContext.addCloseableContextDataLevel();
        ResteasyContext.pushContext(Providers.class, req.getClientConfiguration());
        return ret;
    }

    private ClientResponse toRestEasyResponse(final ClientConfiguration clientConfiguration,
        final HttpClientResponse reactorNettyResponse,
        final InputStream inputStream) {

        final ClientResponse restEasyClientResponse = fnClientResponse.apply(clientConfiguration, inputStream);
        restEasyClientResponse.setStatus(reactorNettyResponse.status().code());

        final MultivaluedMap<String, Object> resteasyHeaders =  restEasyClientResponse.getHeaders();
        reactorNettyResponse.responseHeaders()
            .forEach(header -> resteasyHeaders.add(header.getKey(), header.getValue()));

        return restEasyClientResponse;
    }

    private static class RestEasyClientResponse extends ClientResponse {

        private InputStream is;

        RestEasyClientResponse(final ClientConfiguration configuration, final InputStream is) {
            super(configuration, RESTEasyTracingLogger.empty());
            this.is = is;
        }

        @Override
        protected InputStream getInputStream() {
            return this.is;
        }

        @Override
        protected void setInputStream(InputStream inputStream) {
            this.is = inputStream;
        }

        @Override
        public void releaseConnection() throws IOException {
            this.releaseConnection(false);
        }

        @Override
        public void releaseConnection(boolean consumeInputStream) throws IOException {
            try {
                if (is != null) {
                    if (consumeInputStream) {
                        while (is.available() > 0) {
                            is.read();
                        }
                    }
                    is.close();
                }
            }
            catch (IOException e) {
                // Swallowing because other ClientHttpEngine implementations are swallowing as well.
                // What is better?  causing a potential leak with inputstream slowly or cause an unexpected
                // and unhandled io error and potentially cause the service go down?
                log.warn("Exception while releasing the connection!", e);
            }
        }
    }

    // RestEasyClientResponse that adds a finalize method as safety net
    private class FinalizedRestEasyClientResponse extends RestEasyClientResponse {

        FinalizedRestEasyClientResponse(final ClientConfiguration configuration, final InputStream is) {
            super(configuration, is);
        }

        @Override
        protected void finalize() throws Throwable {

            if (isClosed()) return;
            try {
                log.warn("RestEasyClientResponse was leaked. Ensure all resources are freed via calling close()");
                close();
            } catch (Exception e) {
                log.warn("Exception while close() during finalize()", e);
            }
        }

    }
}
