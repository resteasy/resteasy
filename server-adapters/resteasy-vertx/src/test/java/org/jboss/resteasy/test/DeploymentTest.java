package org.jboss.resteasy.test;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Context;

import org.jboss.resteasy.plugins.server.vertx.VertxContainer;
import org.jboss.resteasy.plugins.server.vertx.VertxRequestHandler;
import org.jboss.resteasy.plugins.server.vertx.VertxResteasyDeployment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpServer;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class DeploymentTest {

    @Path("/")
    public static class Resource {
        @GET
        @Path("/test")
        @Produces("text/plain")
        public String context(
                @Context io.vertx.core.Context context,
                @Context io.vertx.core.Vertx vertx,
                @Context io.vertx.core.http.HttpServerRequest req,
                @Context io.vertx.core.http.HttpServerResponse resp) {
            if (context != null && vertx != null && req != null && resp != null) {
                return Thread.currentThread().getName();
            } else {
                return "fail";
            }
        }
    }

    @Test
    public void testPerInstance() throws Exception {
        VertxContainer.start().getRegistry().addPerInstanceResource(Resource.class);
        try {
            Set<String> results = new HashSet<>();
            for (int i = 0; i < 100; i++) {
                Client client = ClientBuilder.newClient();
                String val;
                try {
                    WebTarget target = client.target(generateURL("/test"));
                    val = target.request().get(String.class);
                } finally {
                    client.close();
                }
                Assertions.assertTrue(val.startsWith("vert.x-eventloop-thread-"));
                results.add(val);
            }
            Assertions.assertEquals(VertxOptions.DEFAULT_EVENT_LOOP_POOL_SIZE, results.size());
        } finally {
            try {
                VertxContainer.stop();
            } catch (Exception ignore) {
            }
        }
    }

    @Test
    public void testEmbed() throws Exception {
        VertxResteasyDeployment deployment = new VertxResteasyDeployment();
        deployment.start();
        deployment.getRegistry().addPerInstanceResource(Resource.class);
        Vertx vertx = Vertx.vertx();
        Client client = ClientBuilder.newClient();
        HttpServer server = null;
        try {
            server = vertx.createHttpServer();
            server.requestHandler(new VertxRequestHandler(vertx, deployment));
            CompletableFuture<Void> listenLatch = new CompletableFuture<>();
            server.listen(TestPortProvider.getPort()).onComplete(ar -> {
                if (ar.succeeded()) {
                    listenLatch.complete(null);
                } else {
                    listenLatch.completeExceptionally(ar.cause());
                }
            });
            listenLatch.get(10, TimeUnit.SECONDS);
            WebTarget target = client.target(generateURL("/test"));
            String val = target.request().get(String.class);
            Assertions.assertTrue(val.startsWith("vert.x-eventloop-thread-"));
        } finally {
            client.close();
            if (server != null) {
                server.close();
            }
            vertx.close();
            deployment.stop();
        }
    }
}
