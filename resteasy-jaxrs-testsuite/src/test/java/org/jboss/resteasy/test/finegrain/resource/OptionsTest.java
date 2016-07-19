package org.jboss.resteasy.test.finegrain.resource;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Response;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashSet;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class OptionsTest
{
   private static Dispatcher dispatcher;
   private static Client client;

   @HttpMethod("OPTIONS")
   @Retention(RetentionPolicy.RUNTIME)
   private static @interface OPTIONS
   {
   }

   @Path("/")
   public static class SimpleResource
   {
      @OPTIONS
      @Path("/options")
      public Response options()
      {
         return Response.ok().header("Allow", "GET, POST").build();
      }

      @Path("/stuff")
      @GET
      public String goodbye()
      {
         System.out.println("Goodbye");
         return "GOODBYE";
      }

      @Path("/stuff")
      @DELETE
      public void stuff()
      {
      }
   }

   @BeforeClass
   public static void before() throws Exception
   {
      dispatcher = EmbeddedContainer.start().getDispatcher();
      dispatcher.getRegistry().addPerRequestResource(SimpleResource.class);
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void after() throws Exception
   {
      client.close();
      EmbeddedContainer.stop();
   }

   @Test
   public void testOptions() throws Exception
   {
      Builder builder = client.target(generateURL("/options")).request();
      Response response = null;
      try
      {
         response = builder.options();
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
         Assert.assertEquals("GET, POST", response.getHeaderString("Allow"));
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
      finally
      {
         response.close();
      }
   }

   @Test
   public void testDefaultOptions() throws Exception
   {
      Builder builder = client.target(generateURL("/stuff")).request();
      Response response = null;
      try
      {
         response = builder.options();
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
         String allowed = response.getHeaderString("Allow");
         Assert.assertNotNull(allowed);
         HashSet<String> vals = new HashSet<String>();
         for (String v : allowed.split(","))
         {
            vals.add(v.trim());
         }  
         Assert.assertEquals(4, vals.size());
         Assert.assertTrue(vals.contains("GET"));
         Assert.assertTrue(vals.contains("DELETE"));
         Assert.assertTrue(vals.contains("HEAD"));
         Assert.assertTrue(vals.contains("OPTIONS"));
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
      finally
      {
         response.close();
      }
   }

   @Test
   public void testMethodNotAllowed() throws Exception
   {
      Builder builder = client.target(generateURL("/stuff")).request();
      Response response = null;
      try
      {
         response = builder.post(null);
         Assert.assertEquals(HttpResponseCodes.SC_METHOD_NOT_ALLOWED, response.getStatus());
         String allowed = response.getHeaderString("Allow");
         Assert.assertNotNull(allowed);
         HashSet<String> vals = new HashSet<String>();
         for (String v : allowed.split(","))
         {
            vals.add(v.trim());
         }  
         Assert.assertEquals(4, vals.size());
         Assert.assertTrue(vals.contains("GET"));
         Assert.assertTrue(vals.contains("DELETE"));
         Assert.assertTrue(vals.contains("HEAD"));
         Assert.assertTrue(vals.contains("OPTIONS"));
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
      finally
      {
         response.close();
      }
   }
}
