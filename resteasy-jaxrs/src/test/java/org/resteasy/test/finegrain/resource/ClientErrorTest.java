package org.resteasy.test.finegrain.resource;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.resteasy.Dispatcher;
import org.resteasy.test.EmbeddedContainer;
import org.resteasy.util.HttpHeaderNames;
import org.resteasy.util.HttpResponseCodes;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.ConsumeMime;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.ProduceMime;
import java.io.IOException;

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
      public String getXml(@PathParam("uriparam")String param)
      {
         return "<" + param + "/>";
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
