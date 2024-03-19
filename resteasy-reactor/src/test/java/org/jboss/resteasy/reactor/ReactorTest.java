package org.jboss.resteasy.reactor;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.InvocationCallback;
import jakarta.ws.rs.core.Response;

import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.engines.ReactiveClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.engines.ReactorNettyClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
import org.jboss.resteasy.plugins.server.netty.NettyJaxrsServer;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.DefaultEventExecutor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.ConnectionObserver;
import reactor.netty.DisposableServer;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.server.HttpServer;
import reactor.netty.resources.ConnectionProvider;
import reactor.util.context.Context;

public class ReactorTest {
    private static NettyJaxrsServer server;

    private static CountDownLatch latch;
    private static AtomicReference<Object> value = new AtomicReference<Object>();
    private static final Logger LOG = Logger.getLogger(NettyJaxrsServer.class);

    @BeforeAll
    public static void beforeClass() throws Exception {
        server = new NettyJaxrsServer();
        server.setPort(TestPortProvider.getPort());
        server.setRootResourcePath("/");
        server.getDeployment().getActualResourceClasses().add(ReactorResource.class);
        server.getDeployment().getActualProviderClasses().add(ReactorInjector.class);
        server.getDeployment().start();
        server.getDeployment().registration();
        server.start();
    }

    @AfterAll
    public static void afterClass() throws Exception {
        server.stop();
        server = null;
    }

    private ResteasyClient client;

    @BeforeEach
    public void before() {
        final ReactorNettyClientHttpEngine reactorEngine = new ReactorNettyClientHttpEngine(
                HttpClient.create(),
                new DefaultChannelGroup(new DefaultEventExecutor()),
                ConnectionProvider.newConnection());
        client = ((ResteasyClientBuilder) ClientBuilder.newBuilder())
                .httpEngine(reactorEngine)
                .readTimeout(5, TimeUnit.SECONDS)
                .connectionCheckoutTimeout(5, TimeUnit.SECONDS)
                .connectTimeout(5, TimeUnit.SECONDS)
                .build();
        value.set(null);
        latch = new CountDownLatch(1);
    }

    @AfterEach
    public void after() {
        client.close();
    }

    @Test
    public void testMono() throws Exception {
        Assertions.assertEquals(0, ReactorResource.monoEndpointCounter.get());
        Mono<Response> mono = client.target(generateURL("/mono")).request().rx(MonoRxInvoker.class).get();
        Thread.sleep(1_000);
        // Make HTTP call does not happen until a subscription happens.
        Assertions.assertEquals(0, ReactorResource.monoEndpointCounter.get());
        mono.subscribe((Response r) -> {
            value.set(r.readEntity(String.class));
            latch.countDown();
        });
        latch.await();
        Assertions.assertEquals(1, ReactorResource.monoEndpointCounter.get());
        Assertions.assertEquals("got it", value.get());
    }

    @Test
    public void testFlux() throws Exception {
        FluxRxInvoker invoker = client.target(generateURL("/flux")).request().rx(FluxRxInvoker.class);
        @SuppressWarnings("unchecked")
        Flux<String> flux = (Flux<String>) invoker.get();
        Set<String> data = new TreeSet<>(); //FIXME [RESTEASY-2778] Intermittent flow / flux test failure
        flux.subscribe(
                (String s) -> data.add(s),
                (Throwable t) -> LOG.error(t.getMessage(), t),
                () -> latch.countDown());
        latch.await();
        Assertions.assertArrayEquals(new String[] { "one", "two" }, data.toArray());
    }

    @Test
    public void testSubscriberContext() {
        final String ctxKey = "secret";
        final List<Integer> secrets = new ArrayList<>();

        // With the `Publisher` bridge, the end user's subscriber context is available when the
        // reactor-netty client is instantiated.  This can be useful for things like trace logging.
        final HttpClient reactorClient = HttpClient.create()
                .doOnRequest((req, conn) -> req.currentContext().<Integer> getOrEmpty(ctxKey).ifPresent(secrets::add));

        final ReactorNettyClientHttpEngine reactorEngine = new ReactorNettyClientHttpEngine(
                reactorClient,
                new DefaultChannelGroup(new DefaultEventExecutor()),
                ConnectionProvider.newConnection());

        final ResteasyClient client = ((ResteasyClientBuilder) ClientBuilder.newBuilder())
                .httpEngine(reactorEngine)
                .readTimeout(5, TimeUnit.SECONDS)
                .connectionCheckoutTimeout(5, TimeUnit.SECONDS)
                .connectTimeout(5, TimeUnit.SECONDS)
                .build();

        final Supplier<Mono<String>> getFn = () -> client.target(generateURL("/mono")).request().rx(MonoRxInvoker.class)
                .get(String.class);

        Mono<String> mono = getFn.get()
                .flatMap(
                        resp1 -> getFn.get()
                                .flatMap(resp2 -> getFn.get().map(resp3 -> String.join("-", Arrays.asList(resp1, resp2, resp3)))
                                        .contextWrite(Context.of(ctxKey, 24))))
                .contextWrite(ctx -> ctx.put(ctxKey, 42));

        Assertions.assertEquals(mono.block(), "got it-got it-got it");
        Assertions.assertTrue(equalTo(secrets, Arrays.asList(42, 42, 24)));
    }

    @Test
    public void testTimeoutOverridePerRequest() throws Exception {
        // This also tests that the client will eagerly close the connection
        // in the case of a business logic timeout.
        final Duration serverResponseDelay = Duration.ofSeconds(60);
        final CountDownLatch serverConnDisconnectingEvent = new CountDownLatch(1);
        final DisposableServer server = HttpServer.create()
                .childObserve((conn, state) -> {
                    if (state == ConnectionObserver.State.DISCONNECTING) {
                        serverConnDisconnectingEvent.countDown();
                    }
                })
                .handle((req, resp) -> resp.sendString(Mono.just("I'm delayed!").delayElement(serverResponseDelay)))
                .bindNow();

        try {
            final CountDownLatch latch = new CountDownLatch(1);

            final HttpClient reactorClient = HttpClient.create();

            final ReactorNettyClientHttpEngine reactorEngine = new ReactorNettyClientHttpEngine(
                    reactorClient,
                    new DefaultChannelGroup(new DefaultEventExecutor()),
                    ConnectionProvider.builder("clientconns").maxConnections(1).build());

            final AtomicReference<Exception> innerTimeoutException = new AtomicReference<>();

            final ReactiveClientHttpEngine wrappedEngine = new ReactiveClientHttpEngine() {
                private <T> Mono<T> recordTimeout(final Mono<T> m) {
                    return m.doOnError(TimeoutException.class, innerTimeoutException::set);
                }

                public <T> Mono<T> submitRx(ClientInvocation request, boolean buffered, ResultExtractor<T> extractor) {
                    return recordTimeout(reactorEngine.submitRx(request, buffered, extractor));
                }

                public <T> Mono<T> fromCompletionStage(CompletionStage<T> cs) {
                    return recordTimeout(reactorEngine.fromCompletionStage(cs));
                }

                public <T> Mono<T> just(T t) {
                    return recordTimeout(reactorEngine.just(t));
                }

                public Mono error(Exception e) {
                    return recordTimeout(reactorEngine.error(e));
                }

                public <T> Future<T> submit(ClientInvocation request, boolean buffered, InvocationCallback<T> callback,
                        ResultExtractor<T> extractor) {
                    return reactorEngine.submit(request, buffered, callback, extractor);
                }

                public <K> CompletableFuture<K> submit(ClientInvocation request, boolean buffered, ResultExtractor<K> extractor,
                        ExecutorService executorService) {
                    return reactorEngine.submit(request, buffered, extractor, executorService);
                }

                public SSLContext getSslContext() {
                    return reactorEngine.getSslContext();
                }

                public HostnameVerifier getHostnameVerifier() {
                    return reactorEngine.getHostnameVerifier();
                }

                public Response invoke(Invocation request) {
                    return reactorEngine.invoke(request);
                }

                public void close() {
                    reactorEngine.close();
                }
            };

            final Duration innerTimeout = Duration.ofSeconds(5);
            final ResteasyClient client = ((ResteasyClientBuilder) ClientBuilder.newBuilder())
                    .httpEngine(wrappedEngine)
                    .readTimeout(innerTimeout.toMillis(), TimeUnit.MILLISECONDS)
                    .build();

            client.target("http://localhost:" + server.port() + "/")
                    .request()
                    .rx(MonoRxInvoker.class)
                    .get(String.class)
                    .timeout(Duration.ofMillis(500))
                    .subscribe(
                            ignore -> {
                                Assertions.fail("Should have got timeout exception");
                            },
                            t -> {
                                if (!(t instanceof TimeoutException)) {
                                    Assertions.assertTrue(t.getMessage().contains("signal within 500ms")); // crappy assertion:(
                                }
                                latch.countDown();
                            },
                            latch::countDown);

            Assertions.assertNull(innerTimeoutException.get(), "Inner timeout should not have occurred!");
            Assertions.assertTrue(latch.await(innerTimeout.multipliedBy(2).toMillis(), TimeUnit.MILLISECONDS),
                    "Test timed out");
            Assertions.assertTrue(serverConnDisconnectingEvent.await(
                    serverResponseDelay.dividedBy(2).toMillis(), TimeUnit.MILLISECONDS), "Server disconnect didn't happen.");
        } finally {
            server.disposeNow();
        }
    }

    @Test
    public void testInjection() {
        Integer data = client.target(generateURL("/injection")).request().get(Integer.class);
        Assertions.assertEquals((Integer) 24, data);

        data = client.target(generateURL("/injection-async")).request().get(Integer.class);
        Assertions.assertEquals((Integer) 42, data);
    }

    // the order of the values in the 2 lists must be the same
    private boolean equalTo(List<Integer> src, List<Integer> control) {
        if (src.size() == control.size()) {
            for (int i = 0; i < src.size(); i++) {
                if (src.get(i).intValue() != control.get(i).intValue()) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
