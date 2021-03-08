package org.jboss.resteasy.plugins.server.reactor.netty;

import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import reactor.core.publisher.Mono;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import java.time.Duration;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

public class MonoTest {

   @Path("/mono")
   public static class MonoResource {
      @GET
      public Mono<String> hello(@QueryParam("delay") Integer delayMs) {
         final Mono<String> businessLogic = Mono.just("Mono says hello!");

         return delayMs != null
                 ? businessLogic.delayElement(Duration.ofMillis(delayMs))
                 : businessLogic;
      }
   }

   private static Client client;

   @BeforeClass
   public static void setup() throws Exception {
      ResteasyDeployment deployment = ReactorNettyContainer.start();
      Registry registry = deployment.getRegistry();
      registry.addPerRequestResource(MonoResource.class);
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void end() {
      client.close();
      ReactorNettyContainer.stop();
   }

   @Test(timeout = 5_000)
   public void testNoDelayMono() {
      WebTarget target = client.target(generateURL("/mono"));
      String val = target.request().get(String.class);
      Assert.assertEquals("Mono says hello!", val);
   }

   @Test(timeout = 5_000)
   public void testDelayedMono() {
      WebTarget target = client.target(generateURL("/mono")).queryParam("delay", "1000");
      String val = target.request().get(String.class);
      Assert.assertEquals("Mono says hello!", val);
   }
}