package org.jboss.resteasy.test.nextgen.interceptors;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.plugins.interceptors.CorsFilter;
import org.jboss.resteasy.spi.CorsHeaders;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

public class CorsFiltersTest extends BaseResourceTest
{

   private static CorsFilter corsFilter;

   @Path("/")
   public static class TestResource
   {
      @Path("test")
      @GET
      @Produces("text/plain")
      public String get() {
         return "hello";
      }
   }

   @BeforeClass
   public static void setup() throws Exception
   {
      addPerRequestResource(TestResource.class);
      corsFilter = new CorsFilter();
      deployment.getProviderFactory().register(corsFilter);


   }

   @Test
   public void testPreflight() throws Exception
   {
      ResteasyClient client = new ResteasyClientBuilder().build();
      WebTarget target = client.target(TestPortProvider.generateURL("/test"));
      Response response = target.request().header(CorsHeaders.ORIGIN, "http://localhost")
              .options();
      Assert.assertEquals(403, response.getStatus());
      response.close();
      response = target.request().header(CorsHeaders.ORIGIN, "http://localhost")
              .get();
      Assert.assertEquals(403, response.getStatus());
      response.close();
      corsFilter.getAllowedOrigins().add("http://localhost");
      response = target.request().header(CorsHeaders.ORIGIN, "http://localhost")
                                 .options();
      Assert.assertEquals(200, response.getStatus());
      response.close();
      response = target.request().header(CorsHeaders.ORIGIN, "http://localhost")
              .get();
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("hello", response.readEntity(String.class));
      response.close();




      client.close();
   }

}
