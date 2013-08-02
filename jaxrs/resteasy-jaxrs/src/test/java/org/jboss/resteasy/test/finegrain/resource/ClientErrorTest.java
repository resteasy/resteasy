package org.jboss.resteasy.test.finegrain.resource;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.plugins.delegates.MediaTypeHeaderDelegate;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

import static org.jboss.resteasy.test.TestPortProvider.generateBaseUrl;
import static org.jboss.resteasy.test.TestPortProvider.generateURL;
import static org.jboss.resteasy.util.HttpClient4xUtils.consumeEntity;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ClientErrorTest
{
   private static Dispatcher dispatcher;

   @Path("/")
   public static class WebResourceUnsupportedMediaType
   {
      @Consumes("application/bar")
      @Produces("application/foo")
      @POST
      public String doPost(String entity)
      {
         return "content";
      }

      @Produces("text/plain")
      @GET
      @Path("complex/match")
      public String get()
      {
         return "content";
      }

      @Produces("text/xml")
      @GET
      @Path("complex/{uriparam: [^/]+}")
      public String getXml(@PathParam("uriparam") String param)
      {
         return "<" + param + "/>";
      }

      @DELETE
      public void delete()
      {

      }

      @Path("/nocontent")
      @POST
      public void noreturn(String entity)
      {

      }
   }

   @BeforeClass
   public static void before() throws Exception
   {
      dispatcher = EmbeddedContainer.start().getDispatcher();
      dispatcher.getRegistry().addPerRequestResource(WebResourceUnsupportedMediaType.class);
   }

   @AfterClass
   public static void after() throws Exception
   {
      EmbeddedContainer.stop();
   }

   @Test
   public void testComplex()
   {
      // this tests a Accept failure match and that another method can match the accept
      ClientRequest request = new ClientRequest(generateURL("/complex/match"));
      request.header(HttpHeaderNames.ACCEPT, "text/xml");
      try
      {
         ClientResponse<?> response = request.get();
         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
         response.releaseConnection();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   @Test
   public void testNotFound()
   {
      ClientRequest request = new ClientRequest(generateURL("/foo/notthere"));
      request.header(HttpHeaderNames.ACCEPT, "application/foo");
      try
      {
         ClientResponse<?> response = request.get();
         Assert.assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());
         response.releaseConnection();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   @Test
   public void testMethodNotAllowed()
   {
      ClientRequest request = new ClientRequest(generateURL(""));
      request.header(HttpHeaderNames.ACCEPT, "application/foo");
      try
      {
         ClientResponse<?> response = request.get();
         Assert.assertEquals(HttpServletResponse.SC_METHOD_NOT_ALLOWED, response.getStatus());
         response.releaseConnection();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   @Test
   public void testNotAcceptable()
   {
      ClientRequest request = new ClientRequest(generateBaseUrl());
      request.header(HttpHeaderNames.ACCEPT, "application/bar");
      request.body("application/bar", "content");
      try
      {
         ClientResponse<?> response = request.post();
         Assert.assertEquals(HttpServletResponse.SC_NOT_ACCEPTABLE, response.getStatus());
         response.releaseConnection();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   @Test
   public void testNoContentPost()
   {
      ClientRequest request = new ClientRequest(generateURL("/nocontent"));
      request.body("text/plain", "content");
      try
      {
         ClientResponse<?> response = request.post();
         Assert.assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());
         response.releaseConnection();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   @Test
   public void testNoContent()
   {
      ClientRequest request = new ClientRequest(generateBaseUrl());
      try
      {
         ClientResponse<?> response = request.delete();
         Assert.assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());
         response.releaseConnection();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }  
   }

   @Test
   public void testUnsupportedMediaType()
   {
      ClientRequest request = new ClientRequest(generateBaseUrl());
      request.header(HttpHeaderNames.ACCEPT, "application/foo");
      request.body("text/plain", "content");
      try
      {
         ClientResponse<?> response = request.post();
         Assert.assertEquals(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, response.getStatus());
         response.releaseConnection();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   @Test
   public void testBadAcceptMediaTypeNoSubType()
   {
      ClientRequest request = new ClientRequest(generateURL("/complex/match"));
      request.header(HttpHeaderNames.ACCEPT, "text");
      try
      {
         ClientResponse<?> response = request.get();
         Assert.assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
         response.releaseConnection();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   @Test
   public void testBadAcceptMediaTypeNonNumericQualityValue()
   {
      ClientRequest request = new ClientRequest(generateURL("/complex/match"));
      request.header(HttpHeaderNames.ACCEPT, "text/plain; q=bad");
      try
      {
         ClientResponse<?> response = request.get();
         Assert.assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
         response.releaseConnection();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   /**
    * This test needs a special extension of MediaTypeHeaderDelegate
    * to work with the RESTEasy client framework, so it has been moved
    * to a separate class, ClientErrorBadMediaTest.
    */
   @Test
   public void testBadContentType()
   {
      HttpClient client = new DefaultHttpClient();
      HttpPost method = new HttpPost(generateURL("/"));
      HttpResponse response = null;
      try
      {
         method.setEntity(new StringEntity("content", "text", null));
         response = client.execute(method);
         Assert.assertEquals(response.getStatusLine().getStatusCode(), HttpResponseCodes.SC_BAD_REQUEST);
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
      finally
      {
         consumeEntity(response);
      }
   }
   
   static class TestMediaTypeHeaderDelegate extends MediaTypeHeaderDelegate
   {
      public static MediaType parse(String type)
      {
         if ("text".equals(type))
         {
            return new MediaType("text", "");
         }
         return MediaTypeHeaderDelegate.parse(type);
      }
   }
}
