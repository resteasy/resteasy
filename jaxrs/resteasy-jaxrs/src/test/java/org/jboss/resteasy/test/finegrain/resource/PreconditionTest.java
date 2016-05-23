package org.jboss.resteasy.test.finegrain.resource;

import java.util.Date;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.util.DateUtil;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import java.util.Date;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;
import org.jboss.resteasy.util.DateUtil;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PreconditionTest
{

   private static Dispatcher dispatcher;
   private static final long ARBITRARY_MILLISECOND_OFFSET = 117;

   @BeforeClass
   public static void before() throws Exception
   {
      dispatcher = EmbeddedContainer.start().getDispatcher();
      dispatcher.getRegistry().addPerRequestResource(LastModifiedResource.class);
      dispatcher.getRegistry().addPerRequestResource(EtagResource.class);
      dispatcher.getRegistry().addPerRequestResource(PrecedenceResource.class);
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
         Date lastModified = DateUtil.parseDate("Mon, 1 Jan 2007 00:00:00 GMT");
         lastModified = new Date(lastModified.getTime() + ARBITRARY_MILLISECOND_OFFSET);
         Response.ResponseBuilder rb = request.evaluatePreconditions(lastModified);
         if (rb != null)
            return rb.build();

         return Response.ok("foo", "text/plain").build();
      }
   }

   @Test
   public void testIfUnmodifiedSinceBeforeLastModified()
   {
      ClientRequest request = new ClientRequest(generateURL("/"));
      request.header(HttpHeaderNames.IF_UNMODIFIED_SINCE, "Sat, 30 Dec 2006 00:00:00 GMT");
      try
      {
         ClientResponse<?> response = request.get();
         Assert.assertEquals(412, response.getStatus());
         shutdownConnections(request);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   @Test
   public void testIfUnmodifiedSinceSameAsLastModified()
   {
      ClientRequest request = new ClientRequest(generateURL("/"));
      request.header(HttpHeaderNames.IF_UNMODIFIED_SINCE, "Mon, 1 Jan 2007 00:00:00 GMT");
      try
      {
         ClientResponse<?> response = request.get();
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
         shutdownConnections(request);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   @Test
   public void testIfUnmodifiedSinceAfterLastModified()
   {
      ClientRequest request = new ClientRequest(generateURL("/"));
      request.header(HttpHeaderNames.IF_UNMODIFIED_SINCE, "Tue, 2 Jan 2007 00:00:00 GMT");
      try
      {
         ClientResponse<?> response = request.get();
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
         shutdownConnections(request);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   @Test
   public void testIfModifiedSinceBeforeLastModified()
   {
      ClientRequest request = new ClientRequest(generateURL("/"));
      request.header(HttpHeaderNames.IF_MODIFIED_SINCE, "Sat, 30 Dec 2006 00:00:00 GMT");
      try
      {
         ClientResponse<?> response = request.get();
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
         shutdownConnections(request);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   @Test
   public void testIfModifiedSinceSameAsLastModified()
   {
      ClientRequest request = new ClientRequest(generateURL("/"));
      request.header(HttpHeaderNames.IF_MODIFIED_SINCE, "Mon, 1 Jan 2007 00:00:00 GMT");
      try
      {
         ClientResponse<?> response = request.get();
         Assert.assertEquals(304, response.getStatus());
         shutdownConnections(request);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   @Test
   public void testIfModifiedSinceAfterLastModified()
   {
      ClientRequest request = new ClientRequest(generateURL("/"));
      request.header(HttpHeaderNames.IF_MODIFIED_SINCE, "Tue, 2 Jan 2007 00:00:00 GMT");
      try
      {
         ClientResponse<?> response = request.get();
         Assert.assertEquals(304, response.getStatus());
         shutdownConnections(request);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   @Test
   public void testIfUnmodifiedSinceBeforeLastModified_IfModifiedSinceBeforeLastModified()
   {
      ClientRequest request = new ClientRequest(generateURL("/"));
      request.header(HttpHeaderNames.IF_UNMODIFIED_SINCE, "Sat, 30 Dec 2006 00:00:00 GMT");
      request.header(HttpHeaderNames.IF_MODIFIED_SINCE, "Sat, 30 Dec 2006 00:00:00 GMT");
      try
      {
         ClientResponse<?> response = request.get();
         Assert.assertEquals(412, response.getStatus());
         shutdownConnections(request);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   @Test
   public void testIfUnmodifiedSinceBeforeLastModified_IfModifiedSinceAfterLastModified()
   {
      ClientRequest request = new ClientRequest(generateURL("/"));
      request.header(HttpHeaderNames.IF_UNMODIFIED_SINCE, "Sat, 30 Dec 2006 00:00:00 GMT");
      request.header(HttpHeaderNames.IF_MODIFIED_SINCE, "Tue, 2 Jan 2007 00:00:00 GMT");
      try
      {
         ClientResponse<?> response = request.get();
         Assert.assertEquals(304, response.getStatus());
         shutdownConnections(request);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   @Test
   public void testIfUnmodifiedSinceAfterLastModified_IfModifiedSinceAfterLastModified()
   {
      ClientRequest request = new ClientRequest(generateURL("/"));
      request.header(HttpHeaderNames.IF_UNMODIFIED_SINCE, "Tue, 2 Jan 2007 00:00:00 GMT");
      request.header(HttpHeaderNames.IF_MODIFIED_SINCE, "Tue, 2 Jan 2007 00:00:00 GMT");
      try
      {
         ClientResponse<?> response = request.get();
         Assert.assertEquals(304, response.getStatus());
         shutdownConnections(request);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   @Test
   public void testIfUnmodifiedSinceAfterLastModified_IfModifiedSinceBeforeLastModified()
   {
      ClientRequest request = new ClientRequest(generateURL("/"));
      request.header(HttpHeaderNames.IF_UNMODIFIED_SINCE, "Tue, 2 Jan 2007 00:00:00 GMT");
      request.header(HttpHeaderNames.IF_MODIFIED_SINCE, "Sat, 30 Dec 2006 00:00:00 GMT");
      try
      {
         ClientResponse<?> response = request.get();
         Assert.assertEquals(200, response.getStatus());
         shutdownConnections(request);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
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

       @GET
       @Path("/weak")
       public Response GetWeak() {
           Response.ResponseBuilder rb = myRequest.evaluatePreconditions(new EntityTag("1", true));
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

   @Test
   public void testIfMatchWithMatchingWeakETag()
   {
      ClientRequest request = new ClientRequest(generateURL("/etag/weak"));
      request.header(HttpHeaderNames.IF_MATCH, "W/\"1\"");
      try
      {
         ClientResponse<?> response = request.get();
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
         shutdownConnections(request);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   @Test
   public void testIfMatchWithNonMatchingWeakEtag()
   {
      ClientRequest request = new ClientRequest(generateURL("/etag/weak"));
      request.header(HttpHeaderNames.IF_MATCH, "W/\"2\"");
      try
      {
          ClientResponse<?> response = request.get();
          Assert.assertEquals(HttpResponseCodes.SC_PRECONDITION_FAILED, response.getStatus());
          shutdownConnections(request);
      }
      catch (Exception e)
      {
          throw new RuntimeException(e);
      }
   }

   ////////////

   public void testIfMatchWithMatchingETag(String fromField)
   {
      ClientRequest request = new ClientRequest(generateURL("/etag" + fromField));
      request.header(HttpHeaderNames.IF_MATCH, "\"1\"");
      try
      {
         ClientResponse<?> response = request.get();
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
         shutdownConnections(request);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   public void testIfMatchWithoutMatchingETag(String fromField)
   {
      ClientRequest request = new ClientRequest(generateURL("/etag" + fromField));
      request.header(HttpHeaderNames.IF_MATCH, "\"2\"");
      try
      {
         ClientResponse<?> response = request.get();
         Assert.assertEquals(412, response.getStatus());
         shutdownConnections(request);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   public void testIfMatchWildCard(String fromField)
   {
      ClientRequest request = new ClientRequest(generateURL("/etag" + fromField));
      request.header(HttpHeaderNames.IF_MATCH, "*");
      try
      {
         ClientResponse<?> response = request.get();
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
         shutdownConnections(request);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   public void testIfNonMatchWithMatchingETag(String fromField)
   {
      ClientRequest request = new ClientRequest(generateURL("/etag" + fromField));
      request.header(HttpHeaderNames.IF_NONE_MATCH, "\"1\"");
      try
      {
         ClientResponse<?> response = request.get();
         Assert.assertEquals(304, response.getStatus());
         Assert.assertEquals("\"1\"", response.getResponseHeaders().getFirst(HttpHeaderNames.ETAG));
         shutdownConnections(request);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   public void testIfNonMatchWithoutMatchingETag(String fromField)
   {
      ClientRequest request = new ClientRequest(generateURL("/etag" + fromField));
      request.header(HttpHeaderNames.IF_NONE_MATCH, "2");
      try
      {
         ClientResponse<?> response = request.get();
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
         shutdownConnections(request);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
  }

   public void testIfNonMatchWildCard(String fromField)
   {
      ClientRequest request = new ClientRequest(generateURL("/etag" + fromField));
      request.header(HttpHeaderNames.IF_NONE_MATCH, "*");
      try
      {
         ClientResponse<?> response = request.get();
         Assert.assertEquals(304, response.getStatus());
         Assert.assertEquals("\"1\"", response.getResponseHeaders().getFirst(HttpHeaderNames.ETAG));
         shutdownConnections(request);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   public void testIfMatchWithMatchingETag_IfNonMatchWithMatchingETag(String fromField)
   {
      ClientRequest request = new ClientRequest(generateURL("/etag" + fromField));
      request.header(HttpHeaderNames.IF_MATCH, "1");
      request.header(HttpHeaderNames.IF_NONE_MATCH, "1");
      try
      {
         ClientResponse<?> response = request.get();
         Assert.assertEquals(304, response.getStatus());
         Assert.assertEquals("\"1\"", response.getResponseHeaders().getFirst(HttpHeaderNames.ETAG));
         shutdownConnections(request);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   public void testIfMatchWithMatchingETag_IfNonMatchWithoutMatchingETag(String fromField)
   {
      ClientRequest request = new ClientRequest(generateURL("/etag" + fromField));
      request.header(HttpHeaderNames.IF_MATCH, "1");
      request.header(HttpHeaderNames.IF_NONE_MATCH, "2");
      try
      {
         ClientResponse<?> response = request.get();
         Assert.assertEquals(200, response.getStatus());
         shutdownConnections(request);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   public void testIfMatchWithoutMatchingETag_IfNonMatchWithMatchingETag(String fromField)
   {
      ClientRequest request = new ClientRequest(generateURL("/etag" + fromField));
      request.header(HttpHeaderNames.IF_MATCH, "2");
      request.header(HttpHeaderNames.IF_NONE_MATCH, "1");
      try
      {
         ClientResponse<?> response = request.get();
         Assert.assertEquals(412, response.getStatus());
         shutdownConnections(request);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   public void testIfMatchWithoutMatchingETag_IfNonMatchWithoutMatchingETag(String fromField)
   {
      ClientRequest request = new ClientRequest(generateURL("/etag" + fromField));
      request.header(HttpHeaderNames.IF_MATCH, "2");
      request.header(HttpHeaderNames.IF_NONE_MATCH, "2");
      try
      {
         ClientResponse<?> response = request.get();
         Assert.assertEquals(412, response.getStatus());
         shutdownConnections(request);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   @Path("/precedence")
   public static class PrecedenceResource
   {
      @GET
      public Response doGet(@Context Request request)
      {
         Date lastModified = DateUtil.parseDate("Mon, 1 Jan 2007 00:00:00 GMT");
         Response.ResponseBuilder rb = request.evaluatePreconditions(lastModified, new EntityTag("1"));
         if (rb != null)
            return rb.build();

         return Response.ok("foo", "text/plain").build();
      }
   }

   @Test
   public void testPrecedence_AllMatch()
   {
      ClientRequest request = new ClientRequest(generateURL("/precedence"));
      request.header(HttpHeaderNames.IF_MATCH, "1");  // true
      request.header(HttpHeaderNames.IF_UNMODIFIED_SINCE, "Mon, 1 Jan 2007 00:00:00 GMT");  // true
      request.header(HttpHeaderNames.IF_NONE_MATCH, "2");  // true
      request.header(HttpHeaderNames.IF_MODIFIED_SINCE, "Sat, 30 Dec 2006 00:00:00 GMT"); // true
      try
      {
         ClientResponse<?> response = request.get();
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
         shutdownConnections(request);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   @Test
   public void testPrecedence_IfMatchWithNonMatchingEtag()
   {
      ClientRequest request = new ClientRequest(generateURL("/precedence"));
      request.header(HttpHeaderNames.IF_MATCH, "2");  // false
      request.header(HttpHeaderNames.IF_UNMODIFIED_SINCE, "Mon, 1 Jan 2007 00:00:00 GMT");  // true
      request.header(HttpHeaderNames.IF_NONE_MATCH, "2");  // true
      request.header(HttpHeaderNames.IF_MODIFIED_SINCE, "Sat, 30 Dec 2006 00:00:00 GMT"); // true
      try
      {
         ClientResponse<?> response = request.get();
         Assert.assertEquals(HttpResponseCodes.SC_PRECONDITION_FAILED, response.getStatus());
         shutdownConnections(request);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   @Test
   public void testPrecedence_IfMatchNotPresentUnmodifiedSinceBeforeLastModified()
   {
      ClientRequest request = new ClientRequest(generateURL("/precedence"));
      request.header(HttpHeaderNames.IF_UNMODIFIED_SINCE, "Sat, 30 Dec 2006 00:00:00 GMT"); //false
      request.header(HttpHeaderNames.IF_NONE_MATCH, "2");  // true
      request.header(HttpHeaderNames.IF_MODIFIED_SINCE, "Sat, 30 Dec 2006 00:00:00 GMT"); // true
      try
      {
         ClientResponse<?> response = request.get();
         Assert.assertEquals(HttpResponseCodes.SC_PRECONDITION_FAILED, response.getStatus());
         shutdownConnections(request);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   @Test
   public void testPrecedence_IfNoneMatchWithMatchingEtag()
   {
      ClientRequest request = new ClientRequest(generateURL("/precedence"));
      request.header(HttpHeaderNames.IF_NONE_MATCH, "1");  // true
      request.header(HttpHeaderNames.IF_MODIFIED_SINCE, "Mon, 1 Jan 2007 00:00:00 GMT");  // true
      try
      {
         ClientResponse<?> response = request.get();
         Assert.assertEquals(HttpResponseCodes.SC_NOT_MODIFIED, response.getStatus());
         shutdownConnections(request);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   @Test
   public void testPrecedence_IfNoneMatchWithNonMatchingEtag()
   {
      ClientRequest request = new ClientRequest(generateURL("/precedence"));
      request.header(HttpHeaderNames.IF_NONE_MATCH, "2");  // false
      request.header(HttpHeaderNames.IF_MODIFIED_SINCE, "Mon, 1 Jan 2007 00:00:00 GMT");  // true
      try
      {
         ClientResponse<?> response = request.get();
         Assert.assertEquals(HttpResponseCodes.SC_NOT_MODIFIED, response.getStatus());
         shutdownConnections(request);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   @Test
   public void testPrecedence_IfNoneMatchNotPresent_IfModifiedSinceBeforeLastModified()
   {
      ClientRequest request = new ClientRequest(generateURL("/precedence"));
      request.header(HttpHeaderNames.IF_MODIFIED_SINCE, "Sat, 30 Dec 2006 00:00:00 GMT"); // false
      try
      {
         ClientResponse<?> response = request.get();
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
         shutdownConnections(request);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   @Test
   public void testPrecedence_IfNoneMatchNotPresent_IfModifiedSinceAfterLastModified()
   {
      ClientRequest request = new ClientRequest(generateURL("/precedence"));
      request.header(HttpHeaderNames.IF_MODIFIED_SINCE, "Tue, 2 Jan 2007 00:00:00 GMT");  // true
      try
      {
         ClientResponse<?> response = request.get();
         Assert.assertEquals(HttpResponseCodes.SC_NOT_MODIFIED, response.getStatus());
         shutdownConnections(request);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   private void shutdownConnections(ClientRequest request)
   {
      ApacheHttpClient4Executor executor = (ApacheHttpClient4Executor) request.getExecutor();
      executor.getHttpClient().getConnectionManager().shutdown();
//      try
//      {
//         request.getExecutor().close();
//      } catch (Exception e)
//      {
//         throw new RuntimeException(e);
//      }
   }
}
