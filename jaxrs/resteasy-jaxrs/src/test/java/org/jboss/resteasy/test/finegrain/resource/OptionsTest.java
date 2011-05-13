package org.jboss.resteasy.test.finegrain.resource;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.OptionsMethod;
import org.apache.commons.httpclient.methods.PostMethod;
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
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashSet;

import static org.jboss.resteasy.test.TestPortProvider.*;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class OptionsTest
{
   private static Dispatcher dispatcher;

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
   }

   @AfterClass
   public static void after() throws Exception
   {
      EmbeddedContainer.stop();
   }

   @Test
   public void testOptions() throws Exception
   {
      HttpClient client = new HttpClient();
      {
         OptionsMethod method = createOptionsMethod("/options");
         try
         {
            int status = client.executeMethod(method);
            Assert.assertEquals(status, HttpResponseCodes.SC_OK);
            Header[] headers = method.getResponseHeaders("Allow");
            Assert.assertNotNull(headers);
            Assert.assertEquals(headers[0].getValue(), "GET, POST");
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }
      }
   }

   @Test
   public void testDefaultOptions() throws Exception
   {
      HttpClient client = new HttpClient();
      {
         OptionsMethod method = createOptionsMethod("/stuff");
         try
         {
            int status = client.executeMethod(method);
            Assert.assertEquals(status, HttpResponseCodes.SC_OK);
            Header[] headers = method.getResponseHeaders("Allow");
            Assert.assertNotNull(headers);
            String value = headers[0].getValue();
            HashSet<String> vals = new HashSet<String>();
            for (String v : value.split(","))
               vals.add(v.trim());
            Assert.assertEquals(4, vals.size());
            Assert.assertTrue(vals.contains("GET"));
            Assert.assertTrue(vals.contains("DELETE"));
            Assert.assertTrue(vals.contains("HEAD"));
            Assert.assertTrue(vals.contains("OPTIONS"));
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }
      }
   }

   @Test
   public void testMethodNotAllowed() throws Exception
   {
      HttpClient client = new HttpClient();
      {
         PostMethod method = createPostMethod("/stuff");
         try
         {
            int status = client.executeMethod(method);
            Assert.assertEquals(status, HttpResponseCodes.SC_METHOD_NOT_ALLOWED);
            Header[] headers = method.getResponseHeaders("Allow");
            Assert.assertNotNull(headers);
            String value = headers[0].getValue();
            HashSet<String> vals = new HashSet<String>();
            for (String v : value.split(","))
               vals.add(v.trim());
            Assert.assertEquals(4, vals.size());
            Assert.assertTrue(vals.contains("HEAD"));
            Assert.assertTrue(vals.contains("OPTIONS"));
            Assert.assertTrue(vals.contains("GET"));
            Assert.assertTrue(vals.contains("DELETE"));
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }
      }
   }
}
