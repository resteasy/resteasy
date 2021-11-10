package org.jboss.resteasy.test.async;

import org.jboss.resteasy.plugins.server.vertx.VertxContainer;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

public class AsyncTest {

   static Client client;

   @BeforeClass
   public static void setup() throws Exception
   {
      VertxContainer.start().getRegistry().addPerRequestResource(AsyncResource.class);
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void end() throws Exception
   {
      try
      {
         client.close();
      } catch (Exception e)
      {

      }
      VertxContainer.stop();
   }

   /**
    * @tpTestDetails Test for correct response
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testAsync() throws Exception {
      Response response = client.target(generateURL("/async")).request().get();
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertEquals("Wrong response content", "hello", response.readEntity(String.class));
   }

   /**
    * @tpTestDetails Service unavailable test
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testTimeout() throws Exception {
      Response response = client.target(generateURL("/async/timeout")).request().get();
      Assert.assertEquals(HttpResponseCodes.SC_SERVICE_UNAVAILABLE, response.getStatus());
   }
}
