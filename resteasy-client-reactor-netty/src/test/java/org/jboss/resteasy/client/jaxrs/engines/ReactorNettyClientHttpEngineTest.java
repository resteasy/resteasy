package org.jboss.resteasy.client.jaxrs.engines;

import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.concurrent.DefaultEventExecutor;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.HttpResources;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.server.HttpServer;
import reactor.netty.resources.ConnectionProvider;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.client.ResponseProcessingException;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ReactorNettyClientHttpEngineTest {

    private static Client client;
    private static DisposableServer mockServer;
    private static final String HELLO_WORLD = "Hello World!";
    private static final String RESOURCE_COULD_NOT_BE_FOUND = "Resource could not be found!";
    private static final String SERVER_IS_NOT_ABLE_TO_RESPONSE = "Server is not able to response!";
    private static final String LIST_OF_STRINGS_IN_JSON = "[\"somestring1\", \"somestring2\"]";

    @BeforeClass
    public static void setup() {
        setupMockServer();
        client = setupClient("http://localhost:" + mockServer.port());
    }

    private static Client setupClient(String baseUrl) {
        ClientBuilder builder = ClientBuilder.newBuilder();
        ResteasyClientBuilder clientBuilder = (ResteasyClientBuilder)builder;
        HttpClient httpClient = HttpClient.create().baseUrl(baseUrl);
        ReactorNettyClientHttpEngine engine =
                new ReactorNettyClientHttpEngine(
                        httpClient,
                        new DefaultChannelGroup(new DefaultEventExecutor()),
                        HttpResources.get());
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
                        .get("/param/{name}", (request, response) ->
                                response.sendString(Mono.just(request.param("name"))))
                        .get("/query", (request, response) -> {
                            QueryStringDecoder query = new QueryStringDecoder(request.uri());
                            return response.sendString(Mono.just(query.rawQuery()));
                        })
                        .post("/birthday", (request, response) ->
                            response.addHeader(HttpHeaderNames.CONTENT_TYPE, "application/json")
                                    .sendString(
                                            request.receive()
                                                    .asString()
                                                    .map(json -> incrementAge(json))))
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

    @Test
    public void testSyncGet() {
        final Response response = client.target("/hello").request().get();
        assertEquals(200, response.getStatus());
        assertEquals(HELLO_WORLD, response.readEntity(String.class));
    }

    @Test
    public void testSyncGetWithType() {
        final String entity = client.target("/hello").request().get(String.class);
        assertEquals(HELLO_WORLD, entity);
    }

    @Test
    public void testSyncGetWithGenericType() {
        final GenericType<List<String>> stringListType = new GenericType<List<String>>() {};
        final List<String> listOfStrings = client.target("/listofstrings").request().get(stringListType);
        assertEquals("somestring1", listOfStrings.get(0));
        assertEquals("somestring2", listOfStrings.get(1));
    }

    @Test
    public void testSyncGetNoResponseEntity() {
        final Response response = client.target("/noentity").request().get();
        assertEquals(204, response.getStatus());
        assertFalse(response.hasEntity());
    }

    @Test
    public void testSyncPost() {
        final Person person = new Person("Mike", 24);
        final Person agedPerson =
                client.target("/birthday")
                        .request()
                        .post(Entity.entity(person, MediaType.APPLICATION_JSON), Person.class);

        assertEquals(agedPerson.getName(), "Mike");
        assertEquals(agedPerson.getAge(), person.getAge() + 1);
    }

    @Test
    public void testSyncHead() {
        final Response response = client.target("/hello").request().head();
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testAsyncGet() throws ExecutionException, InterruptedException {
        final Future<Response> future = client.target("/hello").request().async().get();
        final Response response = future.get();
        assertEquals(200, response.getStatus());
        assertEquals(HELLO_WORLD, response.readEntity(String.class));
    }

    @Test
    public void testAsyncHead() throws ExecutionException, InterruptedException {
        final Future<Response> future = client.target("/hello").request().async().head();
        final Response response = future.get();
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testAsyncGetWithType() throws ExecutionException, InterruptedException {
        final String entity2 = client.target("/hello").request().get(String.class);
        assertEquals(HELLO_WORLD, entity2);

        final Future<String> future = client.target("/hello").request().async().get(String.class);
        final String entity = future.get();
        assertEquals(HELLO_WORLD, entity);

    }

    @Test
    public void testAsyncGetWithGenericType() throws ExecutionException, InterruptedException {
        final GenericType<List<String>> stringListType = new GenericType<List<String>>() {};
        final Future<List<String>> future = client.target("/listofstrings").request().async().get(stringListType);
        final List<String> listOfStrings = future.get();
        assertEquals("somestring1", listOfStrings.get(0));
        assertEquals("somestring2", listOfStrings.get(1));
    }

    @Test
    public void testAsyncGetNoResponseEntity() throws ExecutionException, InterruptedException {
        final Future<Response> future = client.target("/noentity").request().async().get();
        final Response response = future.get();
        assertEquals(204, response.getStatus());
        assertFalse(response.hasEntity());
    }

    @Test
    public void testAsyncGetWithInvocationCallbackCompleted() throws ExecutionException, InterruptedException {
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

        future.get(); // Wait till the result is ready.
        assertEquals(HELLO_WORLD, entity.get());
    }

    @Test
    public void testAsyncGetWithInvocationCallbackFailed() throws ExecutionException, InterruptedException {
        final Client client = setupClient("invalid");
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
                client.target("/birthday")
                        .request()
                        .async()
                        .post(Entity.entity(person, MediaType.APPLICATION_JSON), Person.class);

        final Person agedPerson = future.get();
        assertEquals(agedPerson.getName(), "Mike");
        assertEquals(agedPerson.getAge(), person.getAge() + 1);
    }

    @Test
    public void testRxInvocationGet() throws ExecutionException, InterruptedException {
        final CompletionStage<Response> completionStage = client.target("/hello").request().rx().get();
        final Response response = completionStage.toCompletableFuture().get();
        assertEquals(200, response.getStatus());
        assertEquals(HELLO_WORLD, response.readEntity(String.class));
    }

    @Test
    public void testRxInvocationGetWithType() throws ExecutionException, InterruptedException {
        final CompletionStage<String> completionStage = client.target("/hello").request().rx().get(String.class);
        final String entity = completionStage.toCompletableFuture().get();
        assertEquals(HELLO_WORLD, entity);
    }

    @Test
    public void testHeaderPropagation() {
        final int randomInt = (new Random()).nextInt();
        final Response response = client.target("/hello").request().header("randomInt", randomInt).get();
        assertEquals(200, response.getStatus());
        assertEquals(HELLO_WORLD, response.readEntity(String.class));
        assertEquals(response.getHeaders().getFirst("id"), Integer.toString(randomInt + 1));
    }

    @Test
    public void testPathParamPropagation() {
        final Response response = client.target("/param/Mike").request().get();
        assertEquals(200, response.getStatus());
        assertEquals("Mike", response.readEntity(String.class));
    }

    @Test
    public void test404() {
        final Response response = client.target("/notfound").request().get();
        assertEquals(404, response.getStatus());
        assertFalse(response.hasEntity());

        final Response response2 = client.target("/invalidpath").request().get();
        assertEquals(404, response2.getStatus());
        assertFalse(response2.hasEntity());
    }

    @Test
    public void test404WithEntity() {
        final Response response = client.target("/notfoundwithentity").request().get();
        assertEquals(404, response.getStatus());
        assertEquals(RESOURCE_COULD_NOT_BE_FOUND, response.readEntity(String.class));
    }

    @Test
    public void test500() {
        final Response response = client.target("/internalservererror").request().get();
        assertEquals(500, response.getStatus());
        assertFalse(response.hasEntity());
    }

    @Test
    public void test500WithEntity() {
        final Response response = client.target("/internalservererrorwithentity").request().get();
        assertEquals(500, response.getStatus());
        assertEquals(SERVER_IS_NOT_ABLE_TO_RESPONSE, response.readEntity(String.class));
    }

    @Test
    public void testQueryParamPropagation() {
        final Response response = client.target("/query?name=Mike&age=24").request().get();
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
        final ConnectionProvider connectionProvider = ConnectionProvider.fixed("");
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
        final WebTarget target = client.target("/headers");
        final Response response = target.request().post(Entity.text(payload));
        assertEquals(200, response.getStatus());
        assertEquals(Integer.toString(payload.length()), response.readEntity(String.class));
    }

    @Test
    public void testThatRequestContentLengthHeaderIsOverwritten() {
        final String payload = "hello";
        final WebTarget target = client.target("/headers");
        final Response response =
            target
                .request()
                .header("Content-Length", payload.length() + 42)
                .post(Entity.text(payload));
        assertEquals(200, response.getStatus());
        assertEquals(Integer.toString(payload.length()), response.readEntity(String.class));
    }

    private static String incrementAge(final String json) {
        final int length = json.length();
        final String age = json.substring(length - 2, length - 1);
        return json.replaceFirst(age, Integer.toString(Integer.valueOf(age) + 1));
    }
}