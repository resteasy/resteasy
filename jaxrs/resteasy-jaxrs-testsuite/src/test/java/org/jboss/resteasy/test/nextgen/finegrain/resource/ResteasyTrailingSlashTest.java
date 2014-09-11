package org.jboss.resteasy.test.nextgen.finegrain.resource;
import static org.junit.Assert.assertEquals;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ResteasyTrailingSlashTest extends BaseResourceTest
{

   @Path("/")
   public static class Resource {
      @GET
      @Path("/test/")
      @Produces(MediaType.TEXT_PLAIN)
      public String get() {
         return "hello world";
      }
   }

   @BeforeClass
   public static void init() throws Exception {
      addPerRequestResource(Resource.class);
   }


   @Test
   public void testTrailingSlash() throws Exception {
      Client client = ClientBuilder.newClient();
      String val = client.target(TestPortProvider.generateURL("/test/"))
// String val = client.target(TestPortProvider.generateURL("/test"))
              .request().get(String.class);
      assertEquals("hello world", val);
      client.close();
   }
}