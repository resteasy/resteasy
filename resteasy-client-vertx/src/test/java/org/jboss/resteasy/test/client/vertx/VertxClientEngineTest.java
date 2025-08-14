package org.jboss.resteasy.test.client.vertx;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.client.ClientResponseFilter;
import jakarta.ws.rs.client.CompletionStageRxInvoker;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.InvocationCallback;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.engines.vertx.VertxClientHttpEngine;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

public class VertxClientEngineTest {
    Vertx vertx;
    HttpServer server;
    Client client;
    ScheduledExecutorService executorService;

    @BeforeEach
    public void before() {
        vertx = Vertx.vertx();
        server = vertx.createHttpServer();
        executorService = Executors.newSingleThreadScheduledExecutor();
    }

    @AfterEach
    public void stop() throws Exception {
        if (client != null) {
            client.close();
        }
        CountDownLatch latch = new CountDownLatch(1);
        vertx.close().onComplete(ar -> latch.countDown());
        latch.await(2, TimeUnit.MINUTES);
        executorService.shutdownNow();
    }

    private Client client() throws Exception {
        if (server.actualPort() == 0) {
            CompletableFuture<Void> fut = new CompletableFuture<>();
            server.listen(0).onComplete(ar -> {
                if (ar.succeeded()) {
                    fut.complete(null);
                } else {
                    fut.completeExceptionally(ar.cause());
                }
            });
            fut.get(2, TimeUnit.MINUTES);
        }
        if (client == null) {
            client = ClientBuilder.newBuilder()
                    .scheduledExecutorService(executorService)
                    .register(new VertxClientHttpEngine(vertx))
                    .build();
        }
        return client;
    }

    @Test
    public void checkClientEngine() {
        final Client client = ClientBuilder.newClient();
        Assertions.assertInstanceOf(ResteasyClient.class, client,
                () -> String.format("Expected the client to be an instance of %s", ResteasyClient.class));
        final ResteasyClient resteasyClient = (ResteasyClient) client;
        Assertions.assertInstanceOf(VertxClientHttpEngine.class, resteasyClient.httpEngine());
    }

    @Test
    public void testSimple() throws Exception {
        server.requestHandler(req -> {
            HttpServerResponse response = req.response();
            if (req.getHeader("User-Agent").contains("Apache")) {
                response.setStatusCode(503).end();
            } else if (!"abracadabra".equals(req.getHeader("Password"))) {
                response.setStatusCode(403).end();
            } else {
                req.response().end("Success");
            }
        });

        final Response response = client().target(baseUri()).request()
                .header("Password", "abracadabra")
                .get();

        assertEquals(200, response.getStatus());
        assertEquals("Success", response.readEntity(String.class));
    }

    @Test
    public void testQueryParams() throws Exception {
        final String queryParam = "testQueryParam";
        final String queryParamValue = "testQueryParamValue";
        server.requestHandler(req -> {
            HttpServerResponse response = req.response();
            if (String.format("%s=%s", queryParam, queryParamValue).equals(req.query())) {
                response.setStatusCode(200).end("Success");
            } else {
                response.setStatusCode(503).end("fail");
            }
        });

        final Response response = client().target(baseUri())
                .queryParam(queryParam, queryParamValue)
                .request()
                .get();
        assertEquals(200, response.getStatus());
        assertEquals("Success", response.readEntity(String.class));
    }

    @Test
    public void testSimpleCustomUserAgent() throws Exception {
        final String customUserAgent = "CUSTOM_USER_AGENT";
        server.requestHandler(req -> {
            HttpServerResponse response = req.response();
            if (req.getHeader(HttpHeaders.USER_AGENT).equals(customUserAgent)) {
                response.setStatusCode(200).end("Success");
            } else {
                response.setStatusCode(503).end("fail");
            }
        });

        final Response response = client().target(baseUri()).request()
                .header(HttpHeaders.USER_AGENT.toString(), customUserAgent)
                .get();

        assertEquals(200, response.getStatus());
        assertEquals("Success", response.readEntity(String.class));
    }

    @Test
    public void testHTTP() throws Exception {
        server.requestHandler(req -> {
            HttpServerResponse response = req.response();
            if (req.getHeader("User-Agent").contains("Apache")) {
                response.setStatusCode(503).end();
            } else {
                req.response().end("Success");
            }
        });
        final Response resp = client().target(baseUri()).request().get();
        assertEquals(200, resp.getStatus());
        assertEquals("Success", resp.readEntity(String.class));
    }

    @Test
    public void testSimpleResponseRx() throws Exception {
        server.requestHandler(req -> {
            HttpServerResponse response = req.response();
            if (req.getHeader("User-Agent").contains("Apache")) {
                response.setStatusCode(503).end();
            } else if (!"abracadabra".equals(req.getHeader("Password"))) {
                response.setStatusCode(403).end();
            } else {
                req.response().putHeader("Content-Type", "text/plain").end("Success");
            }
        });

        final CompletionStage<Response> cs = client().target(baseUri()).request()
                .header("Password", "abracadabra").rx(CompletionStageRxInvoker.class)
                .get();

        Response response = cs.toCompletableFuture().get();
        assertEquals(200, response.getStatus());
        assertEquals("Success", response.readEntity(String.class));
    }

    @Test
    public void testSimpleStringRx() throws Exception {
        server.requestHandler(req -> {
            HttpServerResponse response = req.response();
            if (req.getHeader("User-Agent").contains("Apache")) {
                response.setStatusCode(503).end();
            } else if (!"abracadabra".equals(req.getHeader("Password"))) {
                response.setStatusCode(403).end();
            } else {
                req.response().putHeader("Content-Type", "text/plain").end("Success");
            }
        });

        final CompletionStage<String> cs = client().target(baseUri()).request()
                .header("Password", "abracadabra").rx(CompletionStageRxInvoker.class)
                .get(String.class);

        String response = cs.toCompletableFuture().get();
        assertEquals("Success", response);
    }

    @Test
    public void testBigly() throws Exception {
        server.requestHandler(new EchoHandler());
        final byte[] valuableData = randomAlpha().getBytes(StandardCharsets.UTF_8);
        final Response response = client().target(baseUri()).request()
                .post(Entity.entity(valuableData, MediaType.APPLICATION_OCTET_STREAM_TYPE));

        assertEquals(200, response.getStatus());
        assertArrayEquals(valuableData, response.readEntity(byte[].class));
    }

    @Test
    public void testFutureResponse() throws Exception {
        server.requestHandler(new EchoHandler());
        final String valuableData = randomAlpha();
        final Future<Response> response = client().target(baseUri()).request()
                .buildPost(Entity.entity(valuableData, MediaType.APPLICATION_OCTET_STREAM_TYPE))
                .submit();

        final Response resp = response.get(10, TimeUnit.SECONDS);
        assertEquals(200, resp.getStatus());
        assertEquals(valuableData, resp.readEntity(String.class));
    }

    @Test
    public void testFutureString() throws Exception {
        server.requestHandler(new EchoHandler());
        final String valuableData = randomAlpha();
        final Future<String> response = client().target(baseUri()).request()
                .buildPost(Entity.entity(valuableData, MediaType.APPLICATION_OCTET_STREAM_TYPE))
                .submit(String.class);

        final String result = response.get(10, TimeUnit.SECONDS);
        assertEquals(valuableData, result);
    }

    private String randomAlpha() {
        final StringBuilder builder = new StringBuilder();
        final Random r = new Random();
        for (int i = 0; i < 20 * 1024 * 1024; i++) {
            builder.append((char) ('a' + (char) r.nextInt('z' - 'a')));
            if (i % 100 == 0)
                builder.append('\n');
        }
        return builder.toString();
    }

    @Test
    public void testCallbackString() throws Exception {
        server.requestHandler(new EchoHandler());
        final String valuableData = randomAlpha();
        CompletableFuture<String> cf = new CompletableFuture<>();
        client().target(baseUri()).request()
                .buildPost(Entity.entity(valuableData, MediaType.APPLICATION_OCTET_STREAM_TYPE))
                .submit(new InvocationCallback<String>() {
                    @Override
                    public void completed(String s) {
                        cf.complete(s);
                    }

                    @Override
                    public void failed(Throwable throwable) {
                        cf.completeExceptionally(throwable);
                    }
                });

        final String result = cf.get(10, TimeUnit.SECONDS);
        assertEquals(valuableData, result);
    }

    @Test
    public void testTimeout() throws Exception {
        server.requestHandler(req -> {
            vertx.setTimer(1000, id -> {
                req.response().end();
            });
        });
        try {
            Invocation.Builder property = client()
                    .target(baseUri())
                    .request()
                    .property(VertxClientHttpEngine.REQUEST_TIMEOUT_MS, Duration.ofMillis(500));
            property
                    .get();
            fail();
        } catch (ProcessingException e) {
            assertTrue(e.getCause() instanceof TimeoutException);
        }
    }

    @Test
    public void testDeferContent() throws Exception {
        server.requestHandler(new EchoHandler());
        final byte[] valuableData = randomAlpha().getBytes(StandardCharsets.UTF_8);
        final Response response = client().target(baseUri()).request()
                .post(Entity.entity(new StreamingOutput() {
                    @Override
                    public void write(OutputStream output) throws IOException, WebApplicationException {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            throw new AssertionError(e);
                        }
                        output.write(valuableData);
                    }
                }, MediaType.APPLICATION_OCTET_STREAM_TYPE));

        assertEquals(200, response.getStatus());
        assertArrayEquals(valuableData, response.readEntity(byte[].class));
    }

    @Test
    public void testFilterBufferReplay() throws Exception {
        final String greeting = "Success";
        final byte[] expected = greeting.getBytes(StandardCharsets.UTF_8);
        server.requestHandler(req -> {
            req.response().putHeader(HttpHeaders.CONTENT_TYPE, "text/plain").end(greeting);
        });

        final byte[] content = new byte[expected.length];
        final ClientResponseFilter capturer = new ClientResponseFilter() {
            @Override
            public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
                responseContext.getEntityStream().read(content);
            }
        };

        try (InputStream response = client().register(capturer).target(baseUri()).request()
                .get(InputStream.class)) {
            // ignored, we are checking filter
        }

        assertArrayEquals(expected, content);
    }

    @Test
    public void testServerFailure1() throws Exception {
        server.requestHandler(req -> {
            req.response().reset();
        });

        try {
            client().target(baseUri()).request().get();
            fail();
        } catch (ProcessingException ignore) {
            // Expected
        }
    }

    @Test
    public void testServerFailure2() throws Exception {
        server.requestHandler(req -> {
            HttpServerResponse resp = req.response();
            resp.setChunked(true).write("something");
            vertx.setTimer(1000, id -> {
                // Leave it some time to receive the response headers and start processing the response
                resp.reset();
            });
        });

        try {
            Response response = client().target(baseUri()).request().get();
            response.readEntity(String.class);
            fail();
        } catch (ProcessingException ignore) {
            // Expected
        }
    }

    public URI baseUri() {
        return URI.create("http://localhost:" + server.actualPort());
    }

    static class EchoHandler implements Handler<HttpServerRequest> {
        @Override
        public void handle(HttpServerRequest req) {
            req.bodyHandler(body -> {
                String type = req.getHeader(HttpHeaders.CONTENT_TYPE);
                if (type == null) {
                    type = "text/plain";
                }
                req.response()
                        .putHeader(HttpHeaders.CONTENT_TYPE, type)
                        .end(body);
            });
        }
    }
}
