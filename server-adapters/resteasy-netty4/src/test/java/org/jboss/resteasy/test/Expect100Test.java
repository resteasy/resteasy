package org.jboss.resteasy.test;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.plugins.server.netty.NettyContainer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:rsigal@redhat.com">Ron Sigal</a>
 * @version $Revision: 1 $
 */
public class Expect100Test
{
   @Path("/")
   public static class Resource
   {
      @POST
      @Path("/test")
      @Produces("text/plain")
      public String hello(@Context HttpHeaders headers, String s)
      {
         return "hello world";
      }
   }

   static Client client;

   @BeforeClass
   public static void setup() throws Exception
   {
      NettyContainer.start().getRegistry().addPerRequestResource(Resource.class);
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void end() throws Exception
   {
      client.close();
      NettyContainer.stop();
   }

   @Test
   public void testExpect100() throws Exception
   {
      WebTarget target = client.target(generateURL("/test"));
      Response response = target.request().header("Expect", "100-continue").post(Entity.entity("hi", "text/plain"));
      Assert.assertEquals(200,  response.getStatus());
      Assert.assertEquals("hello world", response.readEntity(String.class));
   }
}
