package org.jboss.resteasy.test.regression;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class RegressionTest
{
   private static Dispatcher dispatcher;

   @BeforeClass
   public static void before() throws Exception
   {
   }

   @AfterClass
   public static void after() throws Exception
   {
   }

   @Path("/")
   public static class SimpleResource
   {
      @Path("/simple")
      @GET
      public Response get()
      {
         Response.ResponseBuilder builder = Response.ok("hello world".getBytes());
         builder.header("CoNtEnT-type", "text/plain");
         return builder.build();
      }

      @Path("/complex")
      @GET
      public Object getComplex()
      {
         Response.ResponseBuilder builder = Response.status(HttpResponseCodes.SC_FOUND).entity("hello world".getBytes());
         builder.header("CoNtEnT-type", "text/plain");
         return builder.build();
      }

   }

   /**
    * Test JIRA bugs RESTEASY-1 and RESTEASY-2
    *
    * @throws Exception
    */
   @Test
   public void test1and2() throws Exception
   {
      dispatcher = EmbeddedContainer.start();
      dispatcher.getRegistry().addPerRequestResource(SimpleResource.class);
      {
         HttpClient client = new HttpClient();
         GetMethod method = new GetMethod("http://localhost:8081/simple");
         int status = client.executeMethod(method);
         Assert.assertEquals(HttpResponseCodes.SC_OK, status);
         Assert.assertEquals(method.getResponseHeader("content-type").getValue(), "text/plain");
         byte[] responseBody = method.getResponseBody();
         String response = new String(responseBody, "US-ASCII");
         Assert.assertEquals("hello world", response);
         method.releaseConnection();
      }
      EmbeddedContainer.stop();
   }

   /**
    * Test JIRA bugs RESTEASY-61
    *
    * @throws Exception
    */
   @Test
   public void testJdkURLConnection() throws Exception
   {
      dispatcher = EmbeddedContainer.start();
      dispatcher.getRegistry().addPerRequestResource(SimpleResource.class);
      {
         URL url = new URL("http://localhost:8081/simple");
         HttpURLConnection conn = (HttpURLConnection) url.openConnection();
         @SuppressWarnings("unused")
         Object obj = conn.getContent();
      }
      EmbeddedContainer.stop();
   }


   /**
    * Test JIRA bug RESTEASY-24 and 139
    */
   @Test
   public void test24() throws Exception
   {
      dispatcher = EmbeddedContainer.start();
      dispatcher.getRegistry().addPerRequestResource(SimpleResource.class);
      {
         HttpClient client = new HttpClient();
         GetMethod method = new GetMethod("http://localhost:8081/complex");
         int status = client.executeMethod(method);
         Assert.assertEquals(HttpResponseCodes.SC_FOUND, status);
         Assert.assertEquals(method.getResponseHeader("content-type").getValue(), "text/plain");
         byte[] responseBody = method.getResponseBody();
         String response = new String(responseBody, "US-ASCII");
         Assert.assertEquals("hello world", response);
         method.releaseConnection();
      }
      EmbeddedContainer.stop();

   }


}