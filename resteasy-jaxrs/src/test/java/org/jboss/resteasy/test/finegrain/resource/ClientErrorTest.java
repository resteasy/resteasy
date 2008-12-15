package org.jboss.resteasy.test.finegrain.resource;

import static org.jboss.resteasy.test.TestPortProvider.*;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
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
      dispatcher = EmbeddedContainer.start();
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
      HttpClient client = new HttpClient();
      GetMethod method = createGetMethod("/complex/match");
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
      GetMethod method = createGetMethod("/foo/notthere");
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

      GetMethod method = createGetMethod("");
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
      PostMethod method = new PostMethod(generateBaseUrl());
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
   public void testNoContentPost()
   {
      HttpClient client = new HttpClient();
      PostMethod method = createPostMethod("/nocontent");
      try
      {
         method.setRequestEntity(new StringRequestEntity("content", "text/plain", null));
         int status = client.executeMethod(method);
         Assert.assertEquals(status, HttpResponseCodes.SC_NO_CONTENT);
      }
      catch (IOException e)
      {
         method.releaseConnection();
         throw new RuntimeException(e);
      }
      method.releaseConnection();
   }

   @Test
   public void testNoContent()
   {
      HttpClient client = new HttpClient();
      DeleteMethod method = new DeleteMethod(generateBaseUrl());
      try
      {
         int status = client.executeMethod(method);
         Assert.assertEquals(status, HttpResponseCodes.SC_NO_CONTENT);
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
      PostMethod method = new PostMethod(generateBaseUrl());
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
