package org.resteasy.test.finegrain.resource;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.resteasy.test.EmbeddedServletContainer;
import org.resteasy.util.HttpHeaderNames;
import org.resteasy.util.HttpResponseCodes;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.GregorianCalendar;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PreconditionTest
{

   private static HttpServletDispatcher dispatcher;


   @BeforeClass
   public static void before() throws Exception
   {
      dispatcher = EmbeddedServletContainer.start();
      dispatcher.getRegistry().addResource(LastModifiedResource.class);
      dispatcher.getRegistry().addResource(EtagResource.class);
   }

   @AfterClass
   public static void after() throws Exception
   {
      EmbeddedServletContainer.stop();
   }

   @Path("/")
   public static class LastModifiedResource
   {

      @GET
      public Response doGet(@Context Request request)
      {
         GregorianCalendar lastModified = new GregorianCalendar(2007, 0, 0, 0, 0, 0);
         Response.ResponseBuilder rb = request.evaluatePreconditions(lastModified.getTime());
         if (rb != null)
            return rb.build();

         return Response.ok("foo", "text/plain").build();
      }
   }

   @Test
   public void testIfUnmodifiedSinceBeforeLastModified()
   {
      HttpClient client = new HttpClient();
      GetMethod method = new GetMethod("http://localhost:8081/");
      method.addRequestHeader(HttpHeaderNames.IF_UNMODIFIED_SINCE, "Sat, 30 Dec 2006 00:00:00 GMT");
      try
      {
         int status = client.executeMethod(method);
         Assert.assertEquals(status, 412);
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
      method.releaseConnection();
   }

   @Test
   public void testIfUnmodifiedSinceAfterLastModified()
   {
      HttpClient client = new HttpClient();
      GetMethod method = new GetMethod("http://localhost:8081/");
      method.addRequestHeader(HttpHeaderNames.IF_UNMODIFIED_SINCE, "Tue, 2 Jan 2007 00:00:00 GMT");
      try
      {
         int status = client.executeMethod(method);
         Assert.assertEquals(status, HttpResponseCodes.SC_OK);
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
      method.releaseConnection();
   }

   @Test
   public void testIfModifiedSinceBeforeLastModified()
   {
      HttpClient client = new HttpClient();
      GetMethod method = new GetMethod("http://localhost:8081/");
      method.addRequestHeader(HttpHeaderNames.IF_MODIFIED_SINCE, "Sat, 30 Dec 2006 00:00:00 GMT");
      try
      {
         int status = client.executeMethod(method);
         Assert.assertEquals(status, HttpResponseCodes.SC_OK);
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
      method.releaseConnection();
   }

   @Test
   public void testIfModifiedSinceAfterLastModified()
   {
      HttpClient client = new HttpClient();
      GetMethod method = new GetMethod("http://localhost:8081/");
      method.addRequestHeader(HttpHeaderNames.IF_MODIFIED_SINCE, "Tue, 2 Jan 2007 00:00:00 GMT");
      try
      {
         int status = client.executeMethod(method);
         Assert.assertEquals(status, 304);
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
      method.releaseConnection();
   }

   @Test
   public void testIfUnmodifiedSinceBeforeLastModified_IfModifiedSinceBeforeLastModified()
   {
      HttpClient client = new HttpClient();
      GetMethod method = new GetMethod("http://localhost:8081/");
      method.addRequestHeader(HttpHeaderNames.IF_UNMODIFIED_SINCE, "Sat, 30 Dec 2006 00:00:00 GMT");
      method.addRequestHeader(HttpHeaderNames.IF_MODIFIED_SINCE, "Sat, 30 Dec 2006 00:00:00 GMT");
      try
      {
         int status = client.executeMethod(method);
         Assert.assertEquals(status, 412);
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
      method.releaseConnection();
   }

   @Test
   public void testIfUnmodifiedSinceBeforeLastModified_IfModifiedSinceAfterLastModified()
   {
      HttpClient client = new HttpClient();
      GetMethod method = new GetMethod("http://localhost:8081/");
      method.addRequestHeader(HttpHeaderNames.IF_UNMODIFIED_SINCE, "Sat, 30 Dec 2006 00:00:00 GMT");
      method.addRequestHeader(HttpHeaderNames.IF_MODIFIED_SINCE, "Tue, 2 Jan 2007 00:00:00 GMT");
      try
      {
         int status = client.executeMethod(method);
         Assert.assertEquals(status, 304);
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
      method.releaseConnection();
   }

   @Test
   public void testIfUnmodifiedSinceAfterLastModified_IfModifiedSinceAfterLastModified()
   {
      HttpClient client = new HttpClient();
      GetMethod method = new GetMethod("http://localhost:8081/");
      method.addRequestHeader(HttpHeaderNames.IF_UNMODIFIED_SINCE, "Tue, 2 Jan 2007 00:00:00 GMT");
      method.addRequestHeader(HttpHeaderNames.IF_MODIFIED_SINCE, "Tue, 2 Jan 2007 00:00:00 GMT");
      try
      {
         int status = client.executeMethod(method);
         Assert.assertEquals(status, 304);
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
      method.releaseConnection();
   }

   @Test
   public void testIfUnmodifiedSinceAfterLastModified_IfModifiedSinceBeforeLastModified()
   {
      HttpClient client = new HttpClient();
      GetMethod method = new GetMethod("http://localhost:8081/");
      method.addRequestHeader(HttpHeaderNames.IF_UNMODIFIED_SINCE, "Tue, 2 Jan 2007 00:00:00 GMT");
      method.addRequestHeader(HttpHeaderNames.IF_MODIFIED_SINCE, "Sat, 30 Dec 2006 00:00:00 GMT");
      try
      {
         int status = client.executeMethod(method);
         Assert.assertEquals(status, 200);
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
      method.releaseConnection();
   }

   @Path("/etag")
   public static class EtagResource
   {

      @GET
      public Response doGet(@Context Request request)
      {
         Response.ResponseBuilder rb = request.evaluatePreconditions(new EntityTag("1"));
         if (rb != null)
            return rb.build();

         return Response.ok("foo", "text/plain").build();
      }
   }

   @Test
   public void testIfMatchWithMatchingETag()
   {
      HttpClient client = new HttpClient();
      GetMethod method = new GetMethod("http://localhost:8081/etag");
      method.addRequestHeader(HttpHeaderNames.IF_MATCH, "\"1\"");
      try
      {
         int status = client.executeMethod(method);
         Assert.assertEquals(status, HttpResponseCodes.SC_OK);
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
      method.releaseConnection();
   }

   @Test
   public void testIfMatchWithoutMatchingETag()
   {
      HttpClient client = new HttpClient();
      GetMethod method = new GetMethod("http://localhost:8081/etag");
      method.addRequestHeader(HttpHeaderNames.IF_MATCH, "\"2\"");
      try
      {
         int status = client.executeMethod(method);
         Assert.assertEquals(status, 412);
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
      method.releaseConnection();
   }

   @Test
   public void testIfMatchWildCard()
   {
      HttpClient client = new HttpClient();
      GetMethod method = new GetMethod("http://localhost:8081/etag");
      method.addRequestHeader(HttpHeaderNames.IF_MATCH, "*");
      try
      {
         int status = client.executeMethod(method);
         Assert.assertEquals(status, HttpResponseCodes.SC_OK);
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
      method.releaseConnection();
   }

   @Test
   public void testIfNonMatchWithMatchingETag()
   {
      HttpClient client = new HttpClient();
      GetMethod method = new GetMethod("http://localhost:8081/etag");
      method.addRequestHeader(HttpHeaderNames.IF_NONE_MATCH, "\"1\"");
      try
      {
         int status = client.executeMethod(method);
         Assert.assertEquals(status, 304);
         Assert.assertEquals("1", method.getResponseHeader(HttpHeaderNames.ETAG).getValue());
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
      method.releaseConnection();
   }

   @Test
   public void testIfNonMatchWithoutMatchingETag()
   {
      HttpClient client = new HttpClient();
      GetMethod method = new GetMethod("http://localhost:8081/etag");
      method.addRequestHeader(HttpHeaderNames.IF_NONE_MATCH, "2");
      try
      {
         int status = client.executeMethod(method);
         Assert.assertEquals(status, HttpResponseCodes.SC_OK);
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
      method.releaseConnection();
   }

   @Test
   public void testIfNonMatchWildCard()
   {
      HttpClient client = new HttpClient();
      GetMethod method = new GetMethod("http://localhost:8081/etag");
      method.addRequestHeader(HttpHeaderNames.IF_NONE_MATCH, "*");
      try
      {
         int status = client.executeMethod(method);
         Assert.assertEquals(status, 304);
         Assert.assertEquals("1", method.getResponseHeader(HttpHeaderNames.ETAG).getValue());
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
      method.releaseConnection();
   }

   @Test
   public void testIfMatchWithMatchingETag_IfNonMatchWithMatchingETag()
   {
      HttpClient client = new HttpClient();
      GetMethod method = new GetMethod("http://localhost:8081/etag");
      method.addRequestHeader(HttpHeaderNames.IF_MATCH, "1");
      method.addRequestHeader(HttpHeaderNames.IF_NONE_MATCH, "1");
      try
      {
         int status = client.executeMethod(method);
         Assert.assertEquals(status, 304);
         Assert.assertEquals("1", method.getResponseHeader(HttpHeaderNames.ETAG).getValue());
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
      method.releaseConnection();
   }

   @Test
   public void testIfMatchWithMatchingETag_IfNonMatchWithoutMatchingETag()
   {
      HttpClient client = new HttpClient();
      GetMethod method = new GetMethod("http://localhost:8081/etag");
      method.addRequestHeader(HttpHeaderNames.IF_MATCH, "1");
      method.addRequestHeader(HttpHeaderNames.IF_NONE_MATCH, "2");
      try
      {
         int status = client.executeMethod(method);
         Assert.assertEquals(status, 200);
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
      method.releaseConnection();
   }

   @Test
   public void testIfMatchWithoutMatchingETag_IfNonMatchWithMatchingETag()
   {
      HttpClient client = new HttpClient();
      GetMethod method = new GetMethod("http://localhost:8081/etag");
      method.addRequestHeader(HttpHeaderNames.IF_MATCH, "2");
      method.addRequestHeader(HttpHeaderNames.IF_NONE_MATCH, "1");
      try
      {
         int status = client.executeMethod(method);
         Assert.assertEquals(status, 412);
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   @Test
   public void testIfMatchWithoutMatchingETag_IfNonMatchWithoutMatchingETag()
   {
      HttpClient client = new HttpClient();
      GetMethod method = new GetMethod("http://localhost:8081/etag");
      method.addRequestHeader(HttpHeaderNames.IF_MATCH, "2");
      method.addRequestHeader(HttpHeaderNames.IF_NONE_MATCH, "2");
      try
      {
         int status = client.executeMethod(method);
         Assert.assertEquals(status, 412);
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }


}
