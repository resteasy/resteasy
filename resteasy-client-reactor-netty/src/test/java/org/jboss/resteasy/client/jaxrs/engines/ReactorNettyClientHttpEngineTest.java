package org.jboss.resteasy.client.jaxrs.engines;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.InvocationCallback;
import jakarta.ws.rs.client.ResponseProcessingException;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Providers;

import org.hamcrest.MatcherAssert;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.internal.ClientResponse;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.concurrent.DefaultEventExecutor;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.NettyOutbound;
import reactor.netty.http.HttpResources;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.server.HttpServer;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;
import reactor.netty.resources.ConnectionProvider;

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

        final ReactorNettyClientHttpEngine engine = new ReactorNettyClientHttpEngine(
                httpClient,
                new DefaultChannelGroup(new DefaultEventExecutor()),
                HttpResources.get(),
                timeout);

        final ClientBuilder builder = ClientBuilder.newBuilder();
        final ResteasyClientBuilder clientBuilder = (ResteasyClientBuilder) builder;
        clientBuilder.httpEngine(engine);
        return builder.build();
    }

    private static Client setupClient(HttpClient httpClient) {
        final ReactorNettyClientHttpEngine engine = new ReactorNettyClientHttpEngine(
                httpClient,
                new DefaultChannelGroup(new DefaultEventExecutor()),
                HttpResources.get());

        final ClientBuilder builder = ClientBuilder.newBuilder();
        final ResteasyClientBuilder clientBuilder = (ResteasyClientBuilder) builder;
        clientBuilder.httpEngine(engine);
        return builder.build();
    }

    private static void setupMockServer() {
        mockServer = HttpServer.create()
                .host("localhost")
                .route(routes -> routes
                        .get("/hello", (request, response) -> response.addHeader(HttpHeaderNames.CONTENT_TYPE, "text/plain")
                                .addHeader("id", Integer.toString(
                                        Optional.ofNullable(request.requestHeaders().getInt("randomInt"))
                                                .map(randomInt -> randomInt + 1)
                                                .orElse(-1)))
                                .sendString(Mono.just(HELLO_WORLD)))
                        .head("/hello", (request, response) -> response.send())
                        .get("/noentity", (request, response) -> response.status(HttpResponseStatus.NO_CONTENT).send())
                        .get("/listofstrings",
                                (request, response) -> response.addHeader(HttpHeaderNames.CONTENT_TYPE, "application/json")
                                        .sendString(Mono.just(LIST_OF_STRINGS_IN_JSON)))
                        .get("/notfound", (request, response) -> response.sendNotFound())
                        .get("/notfoundwithentity", (request, response) -> response.status(HttpResponseStatus.NOT_FOUND)
                                .sendString(Mono.just(RESOURCE_COULD_NOT_BE_FOUND)))
                        .get("/internalservererror",
                                (request, response) -> response.status(HttpResponseStatus.INTERNAL_SERVER_ERROR).send())
                        .get("/internalservererrorwithentity",
                                (request, response) -> response.status(HttpResponseStatus.INTERNAL_SERVER_ERROR)
                                        .sendString(Mono.just(SERVER_IS_NOT_ABLE_TO_RESPONSE)))
                        .get("/sleep/{timeout}", (request, response) -> response.sendString(
                                Mono.just(DELAYED_HELLO_WORLD)
                                        .delayElement(Duration.ofMillis(Long.parseLong(request.param("timeout"))))))
                        .get("/param/{name}", (request, response) -> response.sendString(Mono.just(request.param("name"))))
                        .get("/query", (request, response) -> {
                            QueryStringDecoder query = new QueryStringDecoder(request.uri());
                            return response.sendString(Mono.just(query.rawQuery()));
                        })
                        .get("/json",
                                (request, response) -> response.addHeader(HttpHeaderNames.CONTENT_TYPE, "application/json")
                                        .sendString(Mono.just("[]")))
                        .post("/echo",
                                (request, response) -> response
                                        .addHeader(HttpHeaderNames.CONTENT_TYPE,
                                                request.requestHeaders().get(HttpHeaderNames.CONTENT_TYPE))
                                        .send(request.receive().aggregate()))
                        .post("/birthday",
                                (request, response) -> response.addHeader(HttpHeaderNames.CONTENT_TYPE, "application/json")
                                        .sendString(
                                                request.receive()
                                                        .asString()
                                                        .map(json -> incrementAge(json))))
                        .get("/response500", (req, resp) -> resp
                                .status(500)
                                .addHeader(HttpHeaderNames.CONTENT_TYPE, "text/plain")
                                .sendString(Mono.just("oh nos!")))
                        .post("/headers/content-length",
                                (req, resp) -> headerEcho(req, resp, HttpHeaderNames.CONTENT_LENGTH.toString()))
                        .post("/headers/content-encoding",
                                (req, resp) -> headerEcho(req, resp, HttpHeaderNames.CONTENT_ENCODING.toString()))
                        .post("/headers/content-type",
                                (req, resp) -> allHeaderEcho(req, resp, HttpHeaderNames.CONTENT_TYPE.toString())))
                .bindNow();
    }

    private static NettyOutbound allHeaderEcho(HttpServerRequest req, HttpServerResponse resp, String header) {
        return resp.sendString(
                Mono.just(
                        Optional.ofNullable(String.join(",", req.requestHeaders().getAll(header)))
                                .orElse(header + " header was not in request:(:(")));
    }

    private static NettyOutbound headerEcho(
            final HttpServerRequest req,
            final HttpServerResponse resp,
            final String header) {
        return resp.sendString(
                Mono.just(
                        Optional.ofNullable(req.requestHeaders().get(header))
                                .orElse(header + " header was not in request:(:(")));
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
        final GenericType<List<String>> stringListType = new GenericType<List<String>>() {
        };
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
        final Person agedPerson = client.target(url("/birthday"))
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
        final GenericType<List<String>> stringListType = new GenericType<List<String>>() {
        };
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
        final Future<String> future = client.target(url("/hello"))
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
        final Future<String> future = client.target("/hello")
                .request()
                .async()
                .get(new InvocationCallback<String>() {
                    @Override
                    public void completed(String s) {
                        entity.set(s);
                    }

                    @Override
                    public void failed(Throwable throwable) {
                        if (throwable instanceof CompletionException) {
                            entity.set(throwable.getCause().getClass().getName());
                        } else {
                            entity.set(throwable.getClass().getName());
                        }
                    }
                });

        while (!future.isDone()) {
            // Wait till the result is ready.
        }
        assertEquals(UnknownHostException.class.getName(), entity.get());
    }

    @Test
    public void testAsyncPost() throws ExecutionException, InterruptedException {

        final Person person = new Person("Mike", 24);
        final Future<Person> future = client.target(url("/birthday"))
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

        final ClientRequestFilter nullHeaderFilter = requestContext -> requestContext.getHeaders().add("someHeader", null);

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
        RuntimeException runtimeException = ReactorNettyClientHttpEngine.clientException(null, Response.ok().build());

        assertEquals(ProcessingException.class, runtimeException.getClass());
        assertEquals(NullPointerException.class, runtimeException.getCause().getClass());
    }

    @Test
    public void testClientExceptionWithWebApplicationException() {
        RuntimeException runtimeException = ReactorNettyClientHttpEngine.clientException(new WebApplicationException(),
                Response.ok().build());

        assertEquals(WebApplicationException.class, runtimeException.getClass());
    }

    @Test
    public void testClientExceptionWithProcessingException() {
        RuntimeException runtimeException = ReactorNettyClientHttpEngine.clientException(
                new ProcessingException(new IllegalStateException()), Response.ok().build());

        assertEquals(ProcessingException.class, runtimeException.getClass());
        assertEquals(IllegalStateException.class, runtimeException.getCause().getClass());
    }

    @Test
    public void testClientExceptionWithResponseNotNull() {
        RuntimeException runtimeException = ReactorNettyClientHttpEngine.clientException(new IllegalStateException(),
                Response.ok().build());

        assertEquals(ResponseProcessingException.class, runtimeException.getClass());
        assertEquals(IllegalStateException.class, runtimeException.getCause().getClass());
    }

    @Test
    public void testClientExceptionWithResponseNull() {
        RuntimeException runtimeException = ReactorNettyClientHttpEngine.clientException(new IllegalStateException(), null);

        assertEquals(ProcessingException.class, runtimeException.getClass());
        assertEquals(IllegalStateException.class, runtimeException.getCause().getClass());
    }

    @Test
    public void testClose() {
        final ClientBuilder builder = ClientBuilder.newBuilder();
        final ResteasyClientBuilder clientBuilder = (ResteasyClientBuilder) builder;
        final ChannelGroup channelGroup = new DefaultChannelGroup(new DefaultEventExecutor());
        final ConnectionProvider connectionProvider = ConnectionProvider.create("test");
        final HttpClient httpClient = HttpClient.create()
                .baseUrl("http://localhost:" + mockServer.port())
                .doOnConnected(c -> channelGroup.add(c.channel()));
        final ReactorNettyClientHttpEngine engine = new ReactorNettyClientHttpEngine(
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
        final WebTarget target = client.target(url("/headers/content-length"));
        final Response response = target.request().post(Entity.text(payload));
        assertEquals(200, response.getStatus());
        assertEquals(Integer.toString(payload.length()), response.readEntity(String.class));
    }

    @Test
    public void testThatRequestContentLengthHeaderIsOverwritten() {
        final String payload = "hello";
        final WebTarget target = client.target(url("/headers/content-length"));
        final Response response = target
                .request()
                .header("Content-Length", payload.length() + 42)
                .post(Entity.text(payload));
        assertEquals(200, response.getStatus());
        assertEquals(Integer.toString(payload.length()), response.readEntity(String.class));
    }

    @Test
    public void testContextInWriter() {
        final String payload = "hello";
        final WebTarget target = client.target(url("/echo")).register(new ContextNeedingWriter());
        final String respBody = target.request().post(Entity.text(payload), String.class);
        assertEquals(ContextNeedingWriter.transform(payload), respBody);
    }

    @Test
    public void testContextInWriterAsyncChaining() throws Exception {
        final String firstPayload = "first";
        final String secondPayload = "second";
        final WebTarget target = client.target(url("/echo")).register(new ContextNeedingWriter());

        final Function<String, CompletionStage<String>> httpGet = payload -> target.request().rx().post(Entity.text(payload),
                String.class);

        final CompletionStage<String> dataFut = httpGet.apply(firstPayload)
                .thenCompose(firstResp -> httpGet.apply(secondPayload).thenApply(secResp -> firstResp + " : " + secResp));

        final String data = dataFut.toCompletableFuture().get(500, TimeUnit.MILLISECONDS);
        assertEquals(
                ContextNeedingWriter.transform(firstPayload) + " : " + ContextNeedingWriter.transform(secondPayload),
                data);
    }

    @Test
    public void testThatMessageBodyWriterHeadersAreRespected() {
        final String WRITER_ESTABLISHED_HEADER_VAL = "Binary";
        class StringWriter implements MessageBodyWriter<String> {
            @Override
            public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
                return type == String.class;
            }

            @Override
            public long getSize(String s, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
                return s.length();
            }

            @Override
            public void writeTo(String s, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                    MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
                    throws IOException, WebApplicationException {
                httpHeaders.add("Content-Encoding", WRITER_ESTABLISHED_HEADER_VAL);
                entityStream.write(s.getBytes());
            }
        }
        final WebTarget target = client.target(url("/headers/content-encoding")).register(new StringWriter());
        final Response response = target.request().post(Entity.text("marcel sent me some text!"));
        assertEquals(200, response.getStatus());
        assertEquals(WRITER_ESTABLISHED_HEADER_VAL, response.readEntity(String.class));
    }

    @Test
    public void testCaseInsensitiveHeaderReplace() {
        final String CONTENT_TYPE_CLIENT_HEADER_VAL = "text/plain";
        final String CONTENT_TYPE_WRITER_HEADER_VAL = "application/json";
        final String CONTENT_TYPE_UC_HEADER_NAME = "CONTENT-TYPE";
        final String CONTENT_TYPE_LC_HEADER_NAME = "content-type";
        class StringWriter implements MessageBodyWriter<String> {
            @Override
            public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
                return type == String.class;
            }

            @Override
            public long getSize(String s, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
                return s.length();
            }

            @Override
            public void writeTo(String s, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                    MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
                    throws IOException, WebApplicationException {
                httpHeaders.replace(CONTENT_TYPE_LC_HEADER_NAME, Collections.singletonList(CONTENT_TYPE_WRITER_HEADER_VAL));
                entityStream.write(s.getBytes(StandardCharsets.UTF_8));
            }
        }

        final WebTarget target = client.target(url("/headers/content-type")).register(new StringWriter());
        final Response response = target.request().header(CONTENT_TYPE_UC_HEADER_NAME, CONTENT_TYPE_CLIENT_HEADER_VAL)
                .post(Entity.text("{'inputKey' : 'value'}"));
        assertEquals(200, response.getStatus());
        assertEquals(CONTENT_TYPE_WRITER_HEADER_VAL, response.readEntity(String.class));
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
        } catch (ProcessingException ex) {
            MatcherAssert.assertThat(ex.getMessage(),
                    containsString(
                            "java.util.concurrent.TimeoutException: Did not observe any item or terminal signal within 200ms"));
        }
    }

    @Test
    public void testTimeoutAsyncInvocation() throws Exception {
        final Client timeoutClient = setupClient(HttpClient.create(), Duration.ofMillis(200));

        final Future<Response> future = timeoutClient.target(url("/sleep/300")).request().async().get();

        try {
            future.get();
            Assert.fail("timeout exception expected");
        } catch (ExecutionException ex) {
            MatcherAssert.assertThat(ex.getMessage(),
                    containsString(
                            "java.util.concurrent.TimeoutException: Did not observe any item or terminal signal within 200ms"));
        }
    }

    @Test
    public void testTimeoutRxInvocation() throws ExecutionException, InterruptedException {
        final Client timeoutClient = setupClient(HttpClient.create(), Duration.ofMillis(100));

        final CompletionStage<String> completionStage = timeoutClient.target(url("/sleep/200")).request().rx()
                .get(String.class);

        try {
            completionStage.toCompletableFuture().get();
            Assert.fail("timeout exception expected");
        } catch (ExecutionException ex) {
            MatcherAssert.assertThat(ex.getMessage(),
                    containsString(
                            "java.util.concurrent.TimeoutException: Did not observe any item or terminal signal within 100ms"));
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
                final Response r = ((InternalServerErrorException) e.getCause()).getResponse();
                if (r instanceof ClientResponse) {
                    assertFalse(((ClientResponse) r).isClosed());
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

    static class ContextNeedingWriter implements MessageBodyWriter<String> {

        @Context
        private Providers providers;

        static String transform(final String s) {
            return "My contextual writer did this: " + s;
        }

        @Override
        public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return true;
        }

        @Override
        public void writeTo(String s, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
                throws IOException, WebApplicationException {
            providers.getMessageBodyWriter(type, genericType, annotations, mediaType);
            entityStream.write(transform(s).getBytes(StandardCharsets.UTF_8));
        }
    }
}
