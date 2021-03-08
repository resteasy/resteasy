package org.jboss.resteasy.client.jaxrs.engines;

import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.concurrent.DefaultEventExecutor;
import static org.hamcrest.CoreMatchers.containsString;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.internal.ClientResponse;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.HttpResources;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.server.HttpServer;
import reactor.netty.resources.ConnectionProvider;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.client.ResponseProcessingException;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class ReactorNettyClientHttpEngineTest {

    private static Client client;
    private static DisposableServer mockServer;
    private static final String HELLO_WORLD = "Hello World!";
    private static final String RESOURCE_COULD_NOT_BE_FOUND = "Resource could not be found!";
    private static final String SERVER_IS_NOT_ABLE_TO_RESPONSE = "Server is not able to response!";
    private static final String LIST_OF_STRINGS_IN_JSON = "[\"somestring1\", \"somestring2\"]";
    private static final String DELAYED_HELLO_WORLD = "Delayed Hello World!";

    @BeforeClass
    public static void setup() {
        setupMockServer();
        client = setupClient(HttpClient.create());
    }

    private static Client setupClient(HttpClient httpClient, Duration timeout) {

        final ReactorNettyClientHttpEngine engine =
                new ReactorNettyClientHttpEngine(
                        httpClient,
                        new DefaultChannelGroup(new DefaultEventExecutor()),
                        HttpResources.get(),
                        timeout);

        final ClientBuilder builder = ClientBuilder.newBuilder();
        final ResteasyClientBuilder clientBuilder = (ResteasyClientBuilder)builder;
        clientBuilder.httpEngine(engine);
        return builder.build();
    }

    private static Client setupClient(HttpClient httpClient) {
        final ReactorNettyClientHttpEngine engine =
                new ReactorNettyClientHttpEngine(
                        httpClient,
                        new DefaultChannelGroup(new DefaultEventExecutor()),
                        HttpResources.get());

        final ClientBuilder builder = ClientBuilder.newBuilder();
        final ResteasyClientBuilder clientBuilder = (ResteasyClientBuilder)builder;
        clientBuilder.httpEngine(engine);
        return builder.build();
    }

    private static void setupMockServer() {
        mockServer = HttpServer.create()
                .host("localhost")
                .route(routes -> routes
                        .get("/hello", (request, response) ->
                                response.addHeader(HttpHeaderNames.CONTENT_TYPE, "text/plain")
                                        .addHeader("id", Integer.toString(
                                                Optional.ofNullable(request.requestHeaders().getInt("randomInt"))
                                                        .map(randomInt -> randomInt + 1)
                                                        .orElse(-1)))
                                        .sendString(Mono.just(HELLO_WORLD)))
                        .head("/hello", (request, response) -> response.send())
                        .get("/noentity", (request, response) ->
                                response.status(HttpResponseStatus.NO_CONTENT).send())
                        .get("/listofstrings", (request, response) ->
                                response.addHeader(HttpHeaderNames.CONTENT_TYPE, "application/json")
                                        .sendString(Mono.just(LIST_OF_STRINGS_IN_JSON)))
                        .get("/notfound", (request, response) -> response.sendNotFound())
                        .get("/notfoundwithentity", (request, response) ->
                                response.status(HttpResponseStatus.NOT_FOUND)
                                        .sendString(Mono.just(RESOURCE_COULD_NOT_BE_FOUND)))
                        .get("/internalservererror", (request, response) ->
                                response.status(HttpResponseStatus.INTERNAL_SERVER_ERROR).send())
                        .get("/internalservererrorwithentity", (request, response) ->
                                response.status(HttpResponseStatus.INTERNAL_SERVER_ERROR)
                                        .sendString(Mono.just(SERVER_IS_NOT_ABLE_TO_RESPONSE)))
                        .get("/sleep/{timeout}", (request, response) ->
                                response.sendString(
                                        Mono.just(DELAYED_HELLO_WORLD)
                                            .delayElement(Duration.ofMillis(Long.parseLong(request.param("timeout")))))
                        ).get("/param/{name}", (request, response) ->
                                response.sendString(Mono.just(request.param("name"))))
                        .get("/query", (request, response) -> {
                            QueryStringDecoder query = new QueryStringDecoder(request.uri());
                            return response.sendString(Mono.just(query.rawQuery()));
                        })
                        .get("/json", (request, response) ->
                            response.addHeader(HttpHeaderNames.CONTENT_TYPE, "application/json")
                                .sendString(Mono.just("[]")))
                        .post("/birthday", (request, response) ->
                            response.addHeader(HttpHeaderNames.CONTENT_TYPE, "application/json")
                                    .sendString(
                                            request.receive()
                                                    .asString()
                                                    .map(json -> incrementAge(json))))
                        .get("/response500",(req, resp) ->
                            resp
                                .status(500)
                                .addHeader(HttpHeaderNames.CONTENT_TYPE, "text/plain")
                                .sendString(Mono.just("oh nos!")))
                        .post("/headers", (request, response) ->
                            response.sendString(
                                Mono.just(
                                    Optional.ofNullable(
                                        request.requestHeaders().get(HttpHeaderNames.CONTENT_LENGTH))
                                        .orElse("Content-Length header was not in request:(:(")))))
                .bindNow();
    }

    @AfterClass
    public static void cleanup() {
        mockServer.dispose();
    }

    private static String url(String path) {
        return "http://localhost:" + mockServer.port() + path;
    }

    @Test
    public void testSyncGet() {
        final Response response = client.target(url("/hello")).request().get();
        assertEquals(200, response.getStatus());
        assertEquals(HELLO_WORLD, response.readEntity(String.class));
    }

    @Test
    public void testSyncGetWithType() {
        final String entity = client.target(url("/hello")).request().get(String.class);
        assertEquals(HELLO_WORLD, entity);
    }

    @Test
    public void testSyncGetWithGenericType() {
        final GenericType<List<String>> stringListType = new GenericType<List<String>>() {};
        final List<String> listOfStrings = client.target(url("/listofstrings")).request().get(stringListType);
        assertEquals("somestring1", listOfStrings.get(0));
        assertEquals("somestring2", listOfStrings.get(1));
    }

    @Test
    public void testSyncGetNoResponseEntity() {
        final Response response = client.target(url("/noentity")).request().get();
        assertEquals(204, response.getStatus());
        assertFalse(response.hasEntity());
    }

    @Test
    public void testSyncPost() {
        final Person person = new Person("Mike", 24);
        final Person agedPerson =
                client.target(url("/birthday"))
                        .request()
                        .post(Entity.entity(person, MediaType.APPLICATION_JSON), Person.class);

        assertEquals(agedPerson.getName(), "Mike");
        assertEquals(agedPerson.getAge(), person.getAge() + 1);
    }

    @Test
    public void testSyncHead() {
        final Response response = client.target(url("/hello")).request().head();
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testAsyncGet() throws ExecutionException, InterruptedException {
        final Future<Response> future = client.target(url("/hello")).request().async().get();
        final Response response = future.get();
        assertEquals(200, response.getStatus());
        assertEquals(HELLO_WORLD, response.readEntity(String.class));
    }

    @Test
    public void testAsyncHead() throws ExecutionException, InterruptedException {
        final Future<Response> future = client.target(url("/hello")).request().async().head();
        final Response response = future.get();
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testAsyncGetWithType() throws ExecutionException, InterruptedException {
        final String entity2 = client.target(url("/hello")).request().get(String.class);
        assertEquals(HELLO_WORLD, entity2);

        final Future<String> future = client.target(url("/hello")).request().async().get(String.class);
        final String entity = future.get();
        assertEquals(HELLO_WORLD, entity);

    }

    @Test
    public void testAsyncGetWithGenericType() throws ExecutionException, InterruptedException {
        final GenericType<List<String>> stringListType = new GenericType<List<String>>() {};
        final Future<List<String>> future = client.target(url("/listofstrings")).request().async().get(stringListType);
        final List<String> listOfStrings = future.get();
        assertEquals("somestring1", listOfStrings.get(0));
        assertEquals("somestring2", listOfStrings.get(1));
    }

    @Test
    public void testAsyncGetNoResponseEntity() throws ExecutionException, InterruptedException {
        final Future<Response> future = client.target(url("/noentity")).request().async().get();
        final Response response = future.get();
        assertEquals(204, response.getStatus());
        assertFalse(response.hasEntity());
    }

    @Test
    public void testAsyncGetWithInvocationCallbackCompleted() throws ExecutionException, InterruptedException {
        final AtomicReference<String> entity = new AtomicReference<>();
        final Future<String> future =
                client.target(url("/hello"))
                        .request()
                        .async()
                        .get(new InvocationCallback<String>() {
                            @Override
                            public void completed(String s) {
                                entity.set(s);
                            }

                            @Override
                            public void failed(Throwable throwable) {
                                entity.set(throwable.getClass().getName());
                            }
                        });

        future.get(); // Wait till the result is ready.
        assertEquals(HELLO_WORLD, entity.get());
    }

    @Test
    public void testAsyncGetWithInvocationCallbackFailed() throws ExecutionException, InterruptedException {
        final Client client = setupClient(HttpClient.create().baseUrl("invalid"));
        final AtomicReference<String> entity = new AtomicReference<>();
        final Future<String> future =
                client.target("/hello")
                        .request()
                        .async()
                        .get(new InvocationCallback<String>() {
                            @Override
                            public void completed(String s) {
                                entity.set(s);
                            }

                            @Override
                            public void failed(Throwable throwable) {
                                entity.set(throwable.getClass().getName());
                            }
                        });

        while(!future.isDone()) {
            // Wait till the result is ready.
        }
        assertEquals(UnknownHostException.class.getName(), entity.get());
    }

    @Test
    public void testAsyncPost() throws ExecutionException, InterruptedException {

        final Person person = new Person("Mike", 24);
        final Future<Person> future =
                client.target(url("/birthday"))
                        .request()
                        .async()
                        .post(Entity.entity(person, MediaType.APPLICATION_JSON), Person.class);

        final Person agedPerson = future.get();
        assertEquals(agedPerson.getName(), "Mike");
        assertEquals(agedPerson.getAge(), person.getAge() + 1);
    }

    @Test
    public void testRxInvocationGet() throws ExecutionException, InterruptedException {
        final CompletionStage<Response> completionStage = client.target(url("/hello")).request().rx().get();
        final Response response = completionStage.toCompletableFuture().get();
        assertEquals(200, response.getStatus());
        assertEquals(HELLO_WORLD, response.readEntity(String.class));
    }

    @Test
    public void testRxInvocationGetWithType() throws ExecutionException, InterruptedException {
        final CompletionStage<String> completionStage = client.target(url("/hello")).request().rx().get(String.class);
        final String entity = completionStage.toCompletableFuture().get();
        assertEquals(HELLO_WORLD, entity);
    }

    @Test
    public void testHeaderPropagation() {
        final int randomInt = (new Random()).nextInt();
        final Response response = client.target(url("/hello")).request().header("randomInt", randomInt).get();
        assertEquals(200, response.getStatus());
        assertEquals(HELLO_WORLD, response.readEntity(String.class));
        assertEquals(response.getHeaders().getFirst("id"), Integer.toString(randomInt + 1));
    }

    @Test
    public void testHeaderPropagationWithNullHeader() {

        final ClientRequestFilter nullHeaderFilter =
                requestContext -> requestContext.getHeaders().add("someHeader", null);

        final WebTarget webTarget = client.target(url("/hello")).register(nullHeaderFilter);
        final Response response = webTarget.request().header("someHeader", null).get();
        assertEquals(200, response.getStatus());
        assertEquals(HELLO_WORLD, response.readEntity(String.class));
    }

    @Test
    public void testPathParamPropagation() {
        final Response response = client.target(url("/param/Mike")).request().get();
        assertEquals(200, response.getStatus());
        assertEquals("Mike", response.readEntity(String.class));
    }

    @Test
    public void test404() {
        final Response response = client.target(url("/notfound")).request().get();
        assertEquals(404, response.getStatus());
        assertFalse(response.hasEntity());

        final Response response2 = client.target(url("/invalidpath")).request().get();
        assertEquals(404, response2.getStatus());
        assertFalse(response2.hasEntity());
    }

    @Test
    public void test404WithEntity() {
        final Response response = client.target(url("/notfoundwithentity")).request().get();
        assertEquals(404, response.getStatus());
        assertEquals(RESOURCE_COULD_NOT_BE_FOUND, response.readEntity(String.class));
    }

    @Test
    public void test500() {
        final Response response = client.target(url("/internalservererror")).request().get();
        assertEquals(500, response.getStatus());
        assertFalse(response.hasEntity());
    }

    @Test
    public void test500WithEntity() {
        final Response response = client.target(url("/internalservererrorwithentity")).request().get();
        assertEquals(500, response.getStatus());
        assertEquals(SERVER_IS_NOT_ABLE_TO_RESPONSE, response.readEntity(String.class));
    }

    @Test
    public void testQueryParamPropagation() {
        final Response response = client.target(url("/query?name=Mike&age=24")).request().get();
        assertEquals(200, response.getStatus());
        assertEquals("name=Mike&age=24", response.readEntity(String.class));
    }

    @Test
    public void testClientExceptionWithNullPointerException() {
        RuntimeException runtimeException =
                ReactorNettyClientHttpEngine.clientException(null, Response.ok().build());

        assertEquals(ProcessingException.class, runtimeException.getClass());
        assertEquals(NullPointerException.class, runtimeException.getCause().getClass());
    }

    @Test
    public void testClientExceptionWithWebApplicationException() {
        RuntimeException runtimeException =
                ReactorNettyClientHttpEngine.clientException(new WebApplicationException(), Response.ok().build());

        assertEquals(WebApplicationException.class, runtimeException.getClass());
    }

    @Test
    public void testClientExceptionWithProcessingException() {
        RuntimeException runtimeException =
                ReactorNettyClientHttpEngine.clientException(
                        new ProcessingException(new IllegalStateException()), Response.ok().build());

        assertEquals(ProcessingException.class, runtimeException.getClass());
        assertEquals(IllegalStateException.class, runtimeException.getCause().getClass());
    }

    @Test
    public void testClientExceptionWithResponseNotNull() {
        RuntimeException runtimeException =
                ReactorNettyClientHttpEngine.clientException(new IllegalStateException(), Response.ok().build());

        assertEquals(ResponseProcessingException.class, runtimeException.getClass());
        assertEquals(IllegalStateException.class, runtimeException.getCause().getClass());
    }

    @Test
    public void testClientExceptionWithResponseNull() {
        RuntimeException runtimeException =
                ReactorNettyClientHttpEngine.clientException(new IllegalStateException(), null);

        assertEquals(ProcessingException.class, runtimeException.getClass());
        assertEquals(IllegalStateException.class, runtimeException.getCause().getClass());
    }

    @Test
    public void testClose() {
        final ClientBuilder builder = ClientBuilder.newBuilder();
        final ResteasyClientBuilder clientBuilder = (ResteasyClientBuilder)builder;
        final ChannelGroup channelGroup = new DefaultChannelGroup(new DefaultEventExecutor());
        final ConnectionProvider connectionProvider = ConnectionProvider.create("test");
        final HttpClient httpClient =
                HttpClient.create()
                        .baseUrl("http://localhost:" + mockServer.port())
                        .tcpConfiguration(tcpClient -> tcpClient.doOnConnected(c -> channelGroup.add(c.channel())));
        final ReactorNettyClientHttpEngine engine =
                new ReactorNettyClientHttpEngine(
                        httpClient,
                        channelGroup,
                        connectionProvider);
        clientBuilder.httpEngine(engine);
        final Client client = builder.build();

        final Response response = client.target("/hello").request().get();
        assertEquals(200, response.getStatus());
        assertEquals(HELLO_WORLD, response.readEntity(String.class));

        engine.close();
        assertTrue(channelGroup.isEmpty());
        assertTrue(connectionProvider.isDisposed());
    }

    @Test
    public void testThatRequestContentLengthIsSet() {
        final String payload = "hello";
        final WebTarget target = client.target(url("/headers"));
        final Response response = target.request().post(Entity.text(payload));
        assertEquals(200, response.getStatus());
        assertEquals(Integer.toString(payload.length()), response.readEntity(String.class));
    }

    @Test
    public void testThatRequestContentLengthHeaderIsOverwritten() {
        final String payload = "hello";
        final WebTarget target = client.target(url("/headers"));
        final Response response =
            target
                .request()
                .header("Content-Length", payload.length() + 42)
                .post(Entity.text(payload));
        assertEquals(200, response.getStatus());
        assertEquals(Integer.toString(payload.length()), response.readEntity(String.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeTimeout() {
        setupClient(HttpClient.create(), Duration.ofMillis(-1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testZeroTimeout() {
        setupClient(HttpClient.create(), Duration.ofMillis(0));
    }

    @Test
    public void testTimeoutSyncInvocation() {
        final Client timeoutClient = setupClient(HttpClient.create(), Duration.ofMillis(200));

        try {
            timeoutClient.target(url("/sleep/500")).request().get();
            Assert.fail("timeout exception expected");
        } catch(ProcessingException ex) {
            assertThat(ex.getMessage(),
                    containsString("java.util.concurrent.TimeoutException: Did not observe any item or terminal signal within 200ms"));
        }
    }

    @Test
    public void testTimeoutAsyncInvocation() throws Exception {
        final Client timeoutClient = setupClient(HttpClient.create(),Duration.ofMillis(200));

        final Future<Response> future = timeoutClient.target(url("/sleep/300")).request().async().get();

        try {
            future.get();
            Assert.fail("timeout exception expected");
        } catch (ExecutionException ex) {
            assertThat(ex.getMessage(),
                    containsString("java.util.concurrent.TimeoutException: Did not observe any item or terminal signal within 200ms"));
        }
    }

    @Test
    public void testTimeoutRxInvocation() throws ExecutionException, InterruptedException {
        final Client timeoutClient = setupClient(HttpClient.create(), Duration.ofMillis(100));

        final CompletionStage<String> completionStage = timeoutClient.target(url("/sleep/200")).request().rx().get(String.class);

        try {
            completionStage.toCompletableFuture().get();
            Assert.fail("timeout exception expected");
        } catch (ExecutionException ex) {
            assertThat(ex.getMessage(),
                    containsString("java.util.concurrent.TimeoutException: Did not observe any item or terminal signal within 100ms"));
        }
    }

    @Test
    public void testSyncInvocationWithTimeoutConfig() {
        final Client timeoutClient = setupClient(HttpClient.create(), Duration.ofMillis(100));

        final Response response = timeoutClient.target(url("/hello")).request().get();
        assertEquals(200, response.getStatus());
        assertEquals(HELLO_WORLD, response.readEntity(String.class));
    }

    @Test
    public void testAsyncInvocationWithTimeoutConfig() throws Exception {
        final Client timeoutClient = setupClient(HttpClient.create(), Duration.ofMillis(100));
        final Future<Response> future = timeoutClient.target(url("/hello")).request().async().get();

        final Response response = future.get();
        assertEquals(200, response.getStatus());
        assertEquals(HELLO_WORLD, response.readEntity(String.class));
    }

    @Test
    public void testRxInvocationWithTimeoutConfig() throws ExecutionException, InterruptedException {
        final Client timeoutClient = setupClient(HttpClient.create(), Duration.ofMillis(100));

        final CompletionStage<Response> completionStage = timeoutClient.target(url("/hello")).request().rx().get();
        final Response response = completionStage.toCompletableFuture().get();
        assertEquals(200, response.getStatus());
        assertEquals(HELLO_WORLD, response.readEntity(String.class));
    }

    @Test
    public void test500ResponseBodyClosedState() throws Exception {
        try {
            setupClient(HttpClient.create())
                .target(url("/response500"))
                .request()
                .rx()
                .get(String.class)
                .toCompletableFuture()
                .get();
        } catch (final ExecutionException e) {
            if (e.getCause() instanceof InternalServerErrorException) {
                final Response r = ((InternalServerErrorException)e.getCause()).getResponse();
                if (r instanceof ClientResponse) {
                    assertFalse(((ClientResponse)r).isClosed());
                    return;
                }
            }
            throw e;
        }
    }

    private static String incrementAge(final String json) {
        final int length = json.length();
        final String age = json.substring(length - 2, length - 1);
        return json.replaceFirst(age, Integer.toString(Integer.valueOf(age) + 1));
    }
}
