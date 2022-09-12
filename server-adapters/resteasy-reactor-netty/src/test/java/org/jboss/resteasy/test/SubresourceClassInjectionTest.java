package org.jboss.resteasy.test;

import org.jboss.resteasy.plugins.server.reactor.netty.ReactorNettyContainer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * Created by weinanli on 16/06/2017.
 */
public class SubresourceClassInjectionTest {
   public static class SubResource {

      public SubResource() {
      }

      @GET
      public String get(@PathParam("val") String val) {
         return val;
      }
   }

   @Path("/")
   public static class Resource {

      @Path("/sub/{val}")
      public Class<SubResource> sub2(@PathParam("val") String val) {
         return SubResource.class;
      }
   }

   static Client client;

   @BeforeClass
   public static void setup() throws Exception {
      ReactorNettyContainer.start().getRegistry().addPerRequestResource(Resource.class);
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void end() throws Exception {
      try {
         client.close();
      } catch (Exception e) {

      }
      ReactorNettyContainer.stop();
   }

   @Test
   public void testQuery() throws Exception
   {
      WebTarget target = client.target(generateURL("/sub/val"));
      String val = target.request().get(String.class);
      Assert.assertEquals("val", val);
   }
}
