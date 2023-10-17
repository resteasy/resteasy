package org.jboss.resteasy.test.asyncio;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.sse.SseEventSource;

import org.jboss.resteasy.plugins.server.vertx.VertxContainer;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class SSETest {
    static Client client;

    @BeforeAll
    public static void setup() throws Exception {
        ResteasyDeployment deployment = VertxContainer.start();
        Registry registry = deployment.getRegistry();
        registry.addPerRequestResource(SSEResource.class);
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void end() throws Exception {
        try {
            client.close();
        } catch (Exception e) {

        }
        VertxContainer.stop();
    }

    @Test
    public void testSSE() throws Exception {
        WebTarget target = client.target(generateURL("/close/closed"));
        querySSEAndAssert("RESET", "/close/reset");
        querySSEAndAssert("HELLO", "/close/send");

        boolean closed = false;
        int cnt = 0;
        while (!closed && cnt < 20) {
            closed = target.request().get(Boolean.class);
            Thread.sleep(200);
            cnt++;
        }

        querySSEAndAssert("CHECK", "/close/check");
    }

    private void querySSEAndAssert(String message, String uri)
            throws InterruptedException, ExecutionException, TimeoutException {
        WebTarget target = client.target(generateURL(uri));
        SseEventSource source = SseEventSource.target(target).build();
        CompletableFuture<String> cf = new CompletableFuture<>();
        source.register(event -> {
            cf.complete(event.readData());
        },
                error -> {
                    cf.completeExceptionally(error);
                },
                () -> {
                    if (!cf.isDone())
                        cf.completeExceptionally(new RuntimeException("closed with no data"));
                });
        source.open();
        try (SseEventSource x = source) {
            Assertions.assertEquals(message, cf.get(5, TimeUnit.SECONDS));
        }
    }
}