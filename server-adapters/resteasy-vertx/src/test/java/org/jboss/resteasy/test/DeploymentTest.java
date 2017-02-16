package org.jboss.resteasy.test;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpServer;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;

import org.jboss.resteasy.plugins.server.vertx.VertxContainer;
import org.jboss.resteasy.plugins.server.vertx.VertxRequestHandler;
import org.jboss.resteasy.plugins.server.vertx.VertxResteasyDeployment;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class DeploymentTest
{

   @Path("/")
   public static class Resource
   {
      @GET
      @Path("/test")
      @Produces("text/plain")
      public String context(
            @Context io.vertx.core.Context context,
            @Context io.vertx.core.Vertx vertx,
            @Context io.vertx.core.http.HttpServerRequest req,
            @Context io.vertx.core.http.HttpServerResponse resp)
      {
         if (context != null && vertx != null && req != null && resp != null)
         {
            return Thread.currentThread().getName();
         } else
         {
            return "fail";
         }
      }
   }

   @Test
   public void testPerInstance() throws Exception
   {
      VertxContainer.start().getRegistry().addPerInstanceResource(Resource.class);
      try
      {
         Set<String> results = new HashSet<>();
         for (int i = 0; i < 100; i++)
         {
            Client client = ClientBuilder.newClient();
            String val;
            try
            {
               WebTarget target = client.target(generateURL("/test"));
               val = target.request().get(String.class);
            } finally
            {
               client.close();
            }
            Assert.assertTrue(val.startsWith("vert.x-eventloop-thread-"));
            results.add(val);
         }
         Assert.assertEquals(VertxOptions.DEFAULT_EVENT_LOOP_POOL_SIZE, results.size());
      } finally
      {
         try
         {
            VertxContainer.stop();
         } catch (Exception ignore)
         {
         }
      }
   }

   @Test
   public void testEmbed() throws Exception
   {
      VertxResteasyDeployment deployment = new VertxResteasyDeployment();
      deployment.start();
      deployment.getRegistry().addPerInstanceResource(Resource.class);
      Vertx vertx = Vertx.factory.vertx();
      Client client = ClientBuilder.newClient();
      try
      {
         HttpServer server = vertx.createHttpServer();
         server.requestHandler(new VertxRequestHandler(vertx, deployment));
         final CompletableFuture<Void> listenLatch = new CompletableFuture<>();
         server.listen(TestPortProvider.getPort(), new Handler<AsyncResult<HttpServer>>()
         {
            @Override
            public void handle(AsyncResult<HttpServer> ar)
            {
               if (ar.succeeded())
               {
                  listenLatch.complete(null);
               } else
               {
                  listenLatch.completeExceptionally(ar.cause());
               }
            }
         });
         listenLatch.get(10, TimeUnit.SECONDS);
         WebTarget target = client.target(generateURL("/test"));
         String val = target.request().get(String.class);
         Assert.assertTrue(val.startsWith("vert.x-eventloop-thread-"));
      } finally
      {
         client.close();
         vertx.close();
         deployment.stop();
      }
   }
}
