package org.jboss.resteasy.test.client.vertx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.util.concurrent.*;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.RxInvoker;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.internal.VertxFutureRxInvoker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;

class VertxFutureTest {

    Vertx vertx;
    HttpServer server;
    ScheduledExecutorService executorService;
    Client client;

    @BeforeEach
    void beforeEach() {
        vertx = Vertx.vertx();
        server = vertx.createHttpServer();
        executorService = Executors.newSingleThreadScheduledExecutor();
    }

    @AfterEach
    void afterEach() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        vertx.close(ar -> latch.countDown());
        latch.await(1, TimeUnit.MINUTES);
        executorService.shutdownNow();
    }

    private Client client() throws Exception {
        if (server.actualPort() == 0) {
            CompletableFuture<Void> fut = new CompletableFuture<>();
            server.listen(0, ar -> {
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
                    .build();
        }
        return client;
    }

    URI baseUri() {
        return URI.create("http://localhost:" + server.actualPort());
    }

    @Test
    void testSimple() throws Exception {
        server.requestHandler(req -> req.response().end("pong"));

        Invocation.Builder builder = client().target(baseUri()).request();
        RxInvoker<?> invoker = builder.rx(VertxFutureRxInvoker.class);
        CountDownLatch latch = new CountDownLatch(1);

        Future<Response> future = (Future<Response>) invoker.get();
        future.onComplete(ar -> latch.countDown());

        latch.await(1, TimeUnit.MINUTES);

        assertTrue(future.succeeded());
        assertEquals("pong", future.result().readEntity(String.class));
    }

    @Test
    void testError() throws Exception {
        server.requestHandler(req -> req.response().setStatusCode(503).end());

        Invocation.Builder builder = client().target(baseUri()).request();
        RxInvoker<?> invoker = builder.rx(VertxFutureRxInvoker.class);
        CountDownLatch latch = new CountDownLatch(1);

        Future<Response> future = (Future<Response>) invoker.get();
        future.onComplete(ar -> latch.countDown());

        latch.await(1, TimeUnit.MINUTES);

        assertTrue(future.succeeded());
        assertEquals(503, future.result().getStatus());
    }
}
