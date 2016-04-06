package org.jboss.resteasy.test.finegrain.resource;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.jboss.resteasy.util.DateUtil;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.util.HttpResponseCodes;
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
import java.util.Hashtable;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PreconditionRfc7232Test
{

   private static Dispatcher dispatcher;

   @BeforeClass
   public static void before() throws Exception
   {
      Hashtable<String,String> initParams = new Hashtable<>();
      initParams.put("resteasy.rfc7232preconditions", "true");

      dispatcher = EmbeddedContainer.start(initParams).getDispatcher();
      dispatcher.getRegistry().addPerRequestResource(PrecedenceResource.class);
   }

   @AfterClass
   public static void after() throws Exception
   {
      EmbeddedContainer.stop();
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
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
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
