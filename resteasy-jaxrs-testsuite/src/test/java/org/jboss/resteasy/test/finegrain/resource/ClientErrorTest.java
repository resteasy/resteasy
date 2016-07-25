package org.jboss.resteasy.test.finegrain.resource;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
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
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
   private static Client client;

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
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void after() throws Exception
   {
      client.close();
      EmbeddedContainer.stop();
   }

   @Test
   public void testComplex()
   {
      // this tests a Accept failure match and that another method can match the accept
      Builder builder = client.target(generateURL("/complex/match")).request();
      builder.header(HttpHeaderNames.ACCEPT, "text/xml");
      Response response = null;
      try
      {
         response = builder.get();
         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
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
   public void testNotFound()
   {
      Builder builder = client.target(generateURL("/foo/notthere")).request();
      builder.header(HttpHeaderNames.ACCEPT, "application/foo");
      Response response = null;
      try
      {
         response = builder.get();
         Assert.assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());
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
   public void testMethodNotAllowed()
   {
      Builder builder = client.target(generateURL("")).request();
      builder.header(HttpHeaderNames.ACCEPT, "application/foo");
      Response response = null;
      try
      {
         response = builder.get();
         Assert.assertEquals(HttpServletResponse.SC_METHOD_NOT_ALLOWED, response.getStatus());
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
   public void testNotAcceptable()
   {
      Builder builder = client.target(generateBaseUrl()).request();
      builder.header(HttpHeaderNames.ACCEPT, "application/bar");
      Response response = null;
      try
      {
         response = builder.post(Entity.entity("content", "application/bar"));
         Assert.assertEquals(HttpServletResponse.SC_NOT_ACCEPTABLE, response.getStatus());
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
   public void testNoContentPost()
   {
      Builder builder = client.target(generateURL("/nocontent")).request();
      Response response = null;
      try
      {
         response = builder.post(Entity.entity("content", "text/plain"));
         Assert.assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());
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
   public void testNoContent()
   {
      Builder builder = client.target(generateBaseUrl()).request();
      Response response = null;
      try
      {
         response = builder.delete();
         Assert.assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());
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
   public void testUnsupportedMediaType()
   {
      Builder builder = client.target(generateBaseUrl()).request();
      builder.header(HttpHeaderNames.ACCEPT, "application/foo");
      Response response = null;
      try
      {
         response = builder.post(Entity.entity("content", "text/plain"));
         Assert.assertEquals(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, response.getStatus());
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
   public void testBadAcceptMediaTypeNoSubType()
   {
      Builder builder = client.target(generateURL("/complex/match")).request();
      builder.header(HttpHeaderNames.ACCEPT, "text");
      Response response = null;
      try
      {
         response = builder.get();
         Assert.assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
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
   public void testBadAcceptMediaTypeNonNumericQualityValue()
   {
      Builder builder = client.target(generateURL("/complex/match")).request();
      builder.header(HttpHeaderNames.ACCEPT, "text/plain; q=bad");
      Response response = null;
      try
      {
         response = builder.get();
         Assert.assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
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
//         response.close();
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
