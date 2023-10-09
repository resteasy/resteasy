package org.jboss.resteasy.client.jaxrs.engines;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.client.ClientResponseFilter;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.concurrent.DefaultEventExecutor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.HttpProtocol;
import reactor.netty.http.HttpResources;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.server.HttpServer;
import reactor.netty.resources.ConnectionProvider;

public class ReactorNettyClientHttpEngineByteBufReleaseTest {

    private static final String HELLO_WORLD = "Hello World!";
    private static final int CONNECTION_POOL_SIZE = 10;
    private static final Duration SERVER_DELAY_BETWEEN_ELEMENTS_WHILE_STREAMING = Duration.ofMillis(10);
    private static int SERVER_ELEMENT_COUNT = 10;
    private static final int CALL_COUNT = 4000;
    private static final AtomicInteger numOfTimeStreamingEndpointCalled = new AtomicInteger(0);

    private static final DisposableServer mockServer = setupMockServer();

    //CHECKSTYLE.OFF: RegexpSinglelineJava
    private static final PrintStream systemErr = System.err;
    //CHECKSTYLE.ON: RegexpSinglelineJava
    private static final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    @BeforeAll
    public static void setup() {
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID);
        System.setErr(new PrintStream(errContent));
    }

    @AfterAll
    public static void cleanup() {
        System.setErr(systemErr);
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.DISABLED);
        mockServer.dispose();
    }

    @AfterEach
    public void afterEach() {
        //CHECKSTYLE.OFF: RegexpSinglelineJava
        System.out.println(errContent);
        //CHECKSTYLE.ON: RegexpSinglelineJava
        errContent.reset();
        numOfTimeStreamingEndpointCalled.set(0);
    }

    @Test
    public void testExceptionInClientResponseFilterDoesNotLeakMemory() throws Exception {

        final AtomicInteger numOfCalls = new AtomicInteger(0);
        final Client client = setupClient(Duration.ofSeconds(2));

        final ClientResponseFilter exceptionThrowerFilter = new ClientResponseFilter() {
            @Override
            public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) {
                assertEquals("yes", responseContext.getHeaderString("did-it-hit-the-hello-endpoint"));
                numOfCalls.incrementAndGet();
                throw new RuntimeException("Exception from exceptionThrowerFilter!");
            }
        };

        final WebTarget webTarget = client.target("/hello")
                .register(exceptionThrowerFilter, Integer.MAX_VALUE);

        for (int i = 0; i < CALL_COUNT; i++) {
            try {
                webTarget.request()
                        .rx()
                        .get()
                        .toCompletableFuture()
                        .get();

                fail("An exception from filter chain was expected!");
            } catch (final Exception e) {
                // Swallow the exception..
                assertEquals("Exception from exceptionThrowerFilter!", e.getCause().getCause().getMessage());
            }
        }

        Thread.sleep(1000);
        assertFalse(errContent.toString().contains("LEAK"));
        // Connection pool size is 10.  So, being able to make all these calls also verifies that connections are
        // are not leaked, because idle-timeout is set to a high number.
        assertTrue(numOfCalls.get() >= CALL_COUNT - 50); // Some calls may have timed out.

        client.close();
    }

    @Test
    @Disabled // Flaky test on slow hosts (Travis)
    public void testTimeoutWhileReadingBytesFromWireDoesNotLeakMemory() throws ExecutionException, InterruptedException {
        final Client client = setupClient(Duration.ofMillis(50));
        final WebTarget webTarget = client.target("/slowstream");
        final Response FALL_BACK_RESPONSE = Response.status(500).entity("TimeoutException").build();

        Flux.range(1, CALL_COUNT)
                .flatMap(i -> Mono.fromCompletionStage(webTarget.request().rx().get())
                        .onErrorReturn(t -> t instanceof TimeoutException, FALL_BACK_RESPONSE), CONNECTION_POOL_SIZE)
                .map(r -> {
                    r.close();
                    return r;
                })
                .collectList()
                .block()
                .forEach(response -> assertEquals(FALL_BACK_RESPONSE, response, () -> "A TimeoutException was expected!"));

        Thread.sleep(2000);

        assertFalse(errContent.toString().contains("LEAK"));
        // Some calls may have timed before making the call.
        assertTrue(numOfTimeStreamingEndpointCalled.get() >= CALL_COUNT - 50);

        client.close();
    }

    @Test
    public void testLeakDetectionOnMissingClientResponseClose() throws Exception {
        final Client client = setupClient(Duration.ofSeconds(10), false);
        for (int i = 0; i < CALL_COUNT; i++) {
            final Response response = client
                    .target("/hello")
                    .request()
                    .rx()
                    .get()
                    .toCompletableFuture()
                    .get(10, TimeUnit.SECONDS);
        }
        // It's a ByteBuf leak that is actually asserted here on missing close on response.
        assertTrue(errContent.toString().contains("LEAK"));
        client.close();
    }

    @Test
    public void testRestEasyClientResponseWithFinalize() throws Exception {
        final Client client = setupClient(Duration.ofSeconds(10), true);
        final Response response = client
                .target("/hello")
                .request()
                .rx()
                .get()
                .toCompletableFuture()
                .get(10, TimeUnit.SECONDS);

        assertNotNull(response.getClass().getDeclaredMethod("finalize"));
        assertTrue(response.getClass().getSimpleName().contains("FinalizedRestEasyClientResponse"));
    }

    @Test
    public void testDefaultRestEasyClientResponseWithoutFinalize()
            throws ExecutionException, InterruptedException, TimeoutException {
        final Client client = setupClient(Duration.ofSeconds(10));
        final Response response = client
                .target("/hello")
                .request()
                .rx()
                .get()
                .toCompletableFuture()
                .get(10, TimeUnit.SECONDS);

        assertThrows(NoSuchMethodException.class, () -> response.getClass().getDeclaredMethod("finalize"),
                () -> "Expected a NoSuchMethodException");
    }

    private static ByteBuf toByteBuf(final int i) {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            out.write(("-> " + i).getBytes(Charset.defaultCharset()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ByteBufAllocator.DEFAULT
                .buffer()
                .writeBytes(out.toByteArray());
    }

    private static DisposableServer setupMockServer() {

        return HttpServer.create()
                .host("localhost")
                .route(routes -> routes
                        .get("/hello", (request, response) -> response.addHeader(HttpHeaderNames.CONTENT_TYPE, "text/plain")
                                .addHeader("did-it-hit-the-hello-endpoint", "yes")
                                .sendString(Mono.just(HELLO_WORLD)))
                        .get("/slowstream", (request, response) -> {
                            numOfTimeStreamingEndpointCalled.incrementAndGet();
                            return response.addHeader("did-it-hit-the-streaming-endpoint", "yes")
                                    .sse()
                                    .send(Flux.range(1, SERVER_ELEMENT_COUNT)
                                            .delayElements(SERVER_DELAY_BETWEEN_ELEMENTS_WHILE_STREAMING)
                                            .map(i -> toByteBuf(i))
                                            .doOnDiscard(ByteBuf.class, b -> ReferenceCountUtil.safeRelease(b)));
                        }))
                .bindNow();
    }

    private static Client setupClient(final Duration timeout) {
        return setupClient(timeout, false);
    }

    private static Client setupClient(final Duration timeout, final Boolean finalizedResponse) {

        final String connectionPoolName = "ReactorNettyClientHttpEngineByteBufReleaseTest-Connection-Pool";

        final HttpClient httpClient = HttpClient.create(ConnectionProvider.create(connectionPoolName, CONNECTION_POOL_SIZE))
                .protocol(HttpProtocol.HTTP11)
                .keepAlive(true)
                .baseUrl("http://localhost:" + mockServer.port())
                .doOnConnected(con -> con.addHandlerLast(new ReadTimeoutHandler(1, TimeUnit.MINUTES)))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 60000);

        final ReactorNettyClientHttpEngine engine = new ReactorNettyClientHttpEngine(
                httpClient,
                new DefaultChannelGroup(new DefaultEventExecutor()),
                HttpResources.get(),
                timeout,
                finalizedResponse);

        final ClientBuilder builder = ClientBuilder.newBuilder();
        final ResteasyClientBuilder clientBuilder = (ResteasyClientBuilder) builder;
        clientBuilder.httpEngine(engine);
        return builder.build();
    }
}
