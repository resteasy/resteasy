package org.jboss.resteasy.test.finegrain.resource;

import static org.jboss.resteasy.test.TestPortProvider.*;

import java.io.IOException;
import java.util.GregorianCalendar;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PreconditionTest
{

   private static Dispatcher dispatcher;

   @BeforeClass
   public static void before() throws Exception
   {
      dispatcher = EmbeddedContainer.start();
      dispatcher.getRegistry().addPerRequestResource(LastModifiedResource.class);
      dispatcher.getRegistry().addPerRequestResource(EtagResource.class);
   }

   @AfterClass
   public static void after() throws Exception
   {
      EmbeddedContainer.stop();
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
      GetMethod method = createGetMethod("/");
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
      GetMethod method = createGetMethod("/");
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
      GetMethod method = createGetMethod("/");
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
      GetMethod method = createGetMethod("/");
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
      GetMethod method = createGetMethod("/");
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
      GetMethod method = createGetMethod("/");
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
      GetMethod method = createGetMethod("/");
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
      GetMethod method = createGetMethod("/");
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

      @Context
      Request myRequest;

      @GET
      @Path("/fromField")
      public Response doGet()
      {
         Response.ResponseBuilder rb = myRequest.evaluatePreconditions(new EntityTag("1"));
         if (rb != null)
            return rb.build();

         return Response.ok("foo", "text/plain").build();
      }

   }

   @Test
   public void testIfMatchWithMatchingETag()
   {
      testIfMatchWithMatchingETag("");
      testIfMatchWithMatchingETag("/fromField");
   }

   @Test
   public void testIfMatchWithoutMatchingETag()
   {
      testIfMatchWithoutMatchingETag("");
      testIfMatchWithoutMatchingETag("/fromField");
   }

   @Test
   public void testIfMatchWildCard()
   {
      testIfMatchWildCard("");
      testIfMatchWildCard("/fromField");
   }

   @Test
   public void testIfNonMatchWithMatchingETag()
   {
      testIfNonMatchWithMatchingETag("");
      testIfNonMatchWithMatchingETag("/fromField");
   }

   @Test
   public void testIfNonMatchWithoutMatchingETag()
   {
      testIfNonMatchWithoutMatchingETag("");
      testIfNonMatchWithoutMatchingETag("/fromField");
   }

   @Test
   public void testIfNonMatchWildCard()
   {
      testIfNonMatchWildCard("");
      testIfNonMatchWildCard("/fromField");
   }

   @Test
   public void testIfMatchWithMatchingETag_IfNonMatchWithMatchingETag()
   {
      testIfMatchWithMatchingETag_IfNonMatchWithMatchingETag("");
      testIfMatchWithMatchingETag_IfNonMatchWithMatchingETag("/fromField");

   }

   @Test
   public void testIfMatchWithMatchingETag_IfNonMatchWithoutMatchingETag()
   {
      testIfMatchWithMatchingETag_IfNonMatchWithoutMatchingETag("");
      testIfMatchWithMatchingETag_IfNonMatchWithoutMatchingETag("/fromField");
   }

   @Test
   public void testIfMatchWithoutMatchingETag_IfNonMatchWithMatchingETag()
   {
      testIfMatchWithoutMatchingETag_IfNonMatchWithMatchingETag("");
      testIfMatchWithoutMatchingETag_IfNonMatchWithMatchingETag("/fromField");
   }

   @Test
   public void testIfMatchWithoutMatchingETag_IfNonMatchWithoutMatchingETag()
   {
      testIfMatchWithoutMatchingETag_IfNonMatchWithoutMatchingETag("");
      testIfMatchWithoutMatchingETag_IfNonMatchWithoutMatchingETag("/fromField");
   }

   ////////////

   public void testIfMatchWithMatchingETag(String fromField)
   {
      HttpClient client = new HttpClient();
      GetMethod method = createGetMethod("/etag" + fromField);
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

   public void testIfMatchWithoutMatchingETag(String fromField)
   {
      HttpClient client = new HttpClient();
      GetMethod method = createGetMethod("/etag" + fromField);
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

   public void testIfMatchWildCard(String fromField)
   {
      HttpClient client = new HttpClient();
      GetMethod method = createGetMethod("/etag" + fromField);
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

   public void testIfNonMatchWithMatchingETag(String fromField)
   {
      HttpClient client = new HttpClient();
      GetMethod method = createGetMethod("/etag" + fromField);
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

   public void testIfNonMatchWithoutMatchingETag(String fromField)
   {
      HttpClient client = new HttpClient();
      GetMethod method = createGetMethod("/etag" + fromField);
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

   public void testIfNonMatchWildCard(String fromField)
   {
      HttpClient client = new HttpClient();
      GetMethod method = createGetMethod("/etag" + fromField);
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

   public void testIfMatchWithMatchingETag_IfNonMatchWithMatchingETag(String fromField)
   {
      HttpClient client = new HttpClient();
      GetMethod method = createGetMethod("/etag" + fromField);
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

   public void testIfMatchWithMatchingETag_IfNonMatchWithoutMatchingETag(String fromField)
   {
      HttpClient client = new HttpClient();
      GetMethod method = createGetMethod("/etag" + fromField);
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

   public void testIfMatchWithoutMatchingETag_IfNonMatchWithMatchingETag(String fromField)
   {
      HttpClient client = new HttpClient();
      GetMethod method = createGetMethod("/etag" + fromField);
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

   public void testIfMatchWithoutMatchingETag_IfNonMatchWithoutMatchingETag(String fromField)
   {
      HttpClient client = new HttpClient();
      GetMethod method = createGetMethod("/etag" + fromField);
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
