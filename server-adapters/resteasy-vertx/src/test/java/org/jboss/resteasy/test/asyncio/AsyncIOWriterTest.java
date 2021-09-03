package org.jboss.resteasy.test.asyncio;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;

import org.jboss.resteasy.plugins.server.vertx.VertxContainer;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class AsyncIOWriterTest
{

   static Client client;
   @BeforeClass
   public static void setup() throws Exception
   {
      ResteasyDeployment deployment = VertxContainer.start();
      deployment.getProviderFactory().register(MyTypeWriter.class);
      deployment.getProviderFactory().register(MyTypeInterceptor.class);
      Registry registry = deployment.getRegistry();
      registry.addPerRequestResource(AsyncIOWriterResource.class);
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void end() throws Exception
   {
      try
      {
         client.close();
      }
      catch (Exception e)
      {

      }
      VertxContainer.stop();
   }

   @Test
   public void testAsyncIoWriter() throws Exception
   {
      WebTarget target = client.target(generateURL("/async-io-writer"));
      String val = target.request().get(String.class);
      Assert.assertEquals("OK", val);
   }
}