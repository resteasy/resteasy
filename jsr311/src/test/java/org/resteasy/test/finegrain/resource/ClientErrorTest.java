package org.resteasy.test.finegrain.resource;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Assert;
import org.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.resteasy.test.EmbeddedServletContainer;
import org.resteasy.util.HttpHeaderNames;
import org.resteasy.util.HttpResponseCodes;
import org.resteasy.mock.MockHttpServletRequest;
import org.resteasy.mock.MockHttpServletResponse;

import javax.ws.rs.ConsumeMime;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.ProduceMime;
import javax.ws.rs.PathParam;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ClientErrorTest
{
   private static HttpServletDispatcher dispatcher;

   @Path("/")
   public static class WebResourceUnsupportedMediaType
   {
      @ConsumeMime("application/bar")
      @ProduceMime("application/foo")
      @POST
      public String doPost(String entity)
      {
         return "content";
      }

      @ProduceMime("text/plain")
      @GET
      @Path("match")
      public String get()
      {
         return "content";
      }
      @ProduceMime("text/xml")
      @GET
      @Path("{uriparam}")
      public String getXml(@PathParam("uriparam") String param)
      {
         return "<" + param + "/>";
      }
   }

   @BeforeClass
   public static void before() throws Exception
   {
      dispatcher = EmbeddedServletContainer.start();
      dispatcher.getRegistry().addResource(WebResourceUnsupportedMediaType.class);
   }

   @AfterClass
   public static void after() throws Exception
   {
      EmbeddedServletContainer.stop();
   }

   @Test
   public void testComplex()
   {
      HttpClient client = new HttpClient();
      GetMethod method = new GetMethod("http://localhost:8081/match");
      method.addRequestHeader(HttpHeaderNames.ACCEPT, "text/xml");
      try
      {
         int status = client.executeMethod(method);
         Assert.assertEquals(status, HttpServletResponse.SC_OK);
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
      method.releaseConnection();
   }

   @Test
   public void testNotFound()
   {
      HttpClient client = new HttpClient();
      GetMethod method = new GetMethod("http://localhost:8081/foo/notthere");
      method.addRequestHeader(HttpHeaderNames.ACCEPT, "application/foo");
      try
      {
         int status = client.executeMethod(method);
         Assert.assertEquals(status, HttpResponseCodes.SC_NOT_FOUND);
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
      method.releaseConnection();
   }

   @Test
   public void testMethodNotAllowed()
   {
      HttpClient client = new HttpClient();
      {
         MockHttpServletRequest request = new MockHttpServletRequest("GET", "/");
         request.setPathInfo("/");
         request.addHeader(HttpHeaderNames.ACCEPT, "application/foo");
         MockHttpServletResponse response = new MockHttpServletResponse();

         try
         {
            dispatcher.invoke(request, response);
         }
         catch (ServletException e)
         {
            throw new RuntimeException(e);
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }


         Assert.assertEquals(HttpServletResponse.SC_METHOD_NOT_ALLOWED, response.getStatus());
      }

      GetMethod method = new GetMethod("http://localhost:8081");
      method.addRequestHeader(HttpHeaderNames.ACCEPT, "application/foo");
      try
      {
         int status = client.executeMethod(method);
         Assert.assertEquals(status, HttpResponseCodes.SC_METHOD_NOT_ALLOWED);
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
      method.releaseConnection();

   }

   @Test
   public void testNotAcceptable()
   {
      HttpClient client = new HttpClient();
      {
         MockHttpServletRequest request = new MockHttpServletRequest("POST", "/");
         request.setPathInfo("/");
         request.addHeader(HttpHeaderNames.ACCEPT, "application/bar");
         request.setContent("basic".getBytes());
         request.setContentType("application/bar");

         MockHttpServletResponse response = new MockHttpServletResponse();

         try
         {
            dispatcher.invoke(request, response);
         }
         catch (ServletException e)
         {
            throw new RuntimeException(e);
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }


         Assert.assertEquals(HttpServletResponse.SC_NOT_ACCEPTABLE, response.getStatus());
      }
      PostMethod method = new PostMethod("http://localhost:8081");
      method.addRequestHeader(HttpHeaderNames.ACCEPT, "application/bar");
      try
      {
         method.setRequestEntity(new StringRequestEntity("content", "application/bar", null));
         int status = client.executeMethod(method);
         Assert.assertEquals(status, HttpResponseCodes.SC_NOT_ACCEPTABLE);
      }
      catch (IOException e)
      {
         method.releaseConnection();
         throw new RuntimeException(e);
      }
      method.releaseConnection();
   }

   @Test
   public void testUnsupportedMediaType()
   {
      HttpClient client = new HttpClient();
      PostMethod method = new PostMethod("http://localhost:8081");
      method.addRequestHeader(HttpHeaderNames.ACCEPT, "application/foo");
      try
      {
         method.setRequestEntity(new StringRequestEntity("content", "text/plain", null));
         int status = client.executeMethod(method);
         Assert.assertEquals(status, HttpResponseCodes.SC_UNSUPPORTED_MEDIA_TYPE);
      }
      catch (IOException e)
      {
         method.releaseConnection();
         throw new RuntimeException(e);
      }
      method.releaseConnection();

   }

}
