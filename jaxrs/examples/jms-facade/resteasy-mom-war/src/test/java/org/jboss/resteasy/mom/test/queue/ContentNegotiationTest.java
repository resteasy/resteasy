package org.jboss.resteasy.mom.test.queue;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.junit.Assert;
import org.junit.Test;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.plugins.server.tjws.TJWSEmbeddedJaxrsServer;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.util.HttpResponseCodes;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ContentNegotiationTest
{
   private static String plainMessage;
   private static String xmlMessage;
   private static CountDownLatch latch;
   private static boolean error = false;

   private static final String RESTEASY_MOM_URI = "http://localhost:8080/resteasy-mom/";

   @Test
   public void testDummy()
   {
   }

   @Path("/")
   public static class PlainTextListener
   {
      @Path("/plain")
      @POST
      @Consumes("text/plain")
      public Response post(String msg)
      {
         plainMessage = msg;
         if (!plainMessage.equals("stupid message")) error = true;
         latch.countDown();
         return Response.ok().build();
      }
   }

   @Path("/")
   public static class XmlListener
   {
      @Path("/xml")
      @POST
      @Consumes("application/xml")
      public Response post(String msg)
      {
         xmlMessage = msg;
         if (!xmlMessage.equals("<hello/>")) error = true;
         latch.countDown();
         return Response.ok().build();
      }
   }

   @Test
   public void testQueueListener() throws Exception
   {
      TJWSEmbeddedJaxrsServer server = new TJWSEmbeddedJaxrsServer();
      server.setPort(8081);
      server.start();
      Dispatcher dispatcher = server.getDispatcher();
      dispatcher.getRegistry().addPerRequestResource(PlainTextListener.class);
      dispatcher.getRegistry().addPerRequestResource(XmlListener.class);
      HttpClient client = new HttpClient();
      try
      {
         {
            PutMethod method = new PutMethod(RESTEASY_MOM_URI + "queues/A/listeners/1");
            method.setRequestEntity(new StringRequestEntity("http://localhost:8081/plain", "text/plain", null));
            method.addRequestHeader(HttpHeaderNames.ACCEPT, "text/plain");
            int status = client.executeMethod(method);
            Assert.assertEquals(HttpResponseCodes.SC_CREATED, status);
            method.releaseConnection();
         }
         {
            PutMethod method = new PutMethod(RESTEASY_MOM_URI + "queues/A/listeners/2");
            method.setRequestEntity(new StringRequestEntity("http://localhost:8081/xml", "application/xml", null));
            method.addRequestHeader(HttpHeaderNames.ACCEPT, "application/xml");
            int status = client.executeMethod(method);
            Assert.assertEquals(HttpResponseCodes.SC_CREATED, status);
            method.releaseConnection();
         }
         latch = new CountDownLatch(10);
         postMessage(client, "text/plain", "stupid message");
         postMessage(client, "application/xml", "<hello/>");
         postMessage(client, "text/plain", "stupid message");
         postMessage(client, "text/plain", "stupid message");
         postMessage(client, "text/plain", "stupid message");
         postMessage(client, "application/xml", "<hello/>");
         postMessage(client, "application/xml", "<hello/>");
         postMessage(client, "text/plain", "stupid message");
         postMessage(client, "application/xml", "<hello/>");
         postMessage(client, "application/xml", "<hello/>");
         Assert.assertTrue(latch.await(4, TimeUnit.SECONDS));

         Assert.assertEquals("stupid message", plainMessage);
         Assert.assertEquals("<hello/>", xmlMessage);
         Assert.assertFalse(error);


      }
      finally
      {
         {
            DeleteMethod method = new DeleteMethod(RESTEASY_MOM_URI + "queues/A/listeners/1");
            client.executeMethod(method);
            method.releaseConnection();
         }
         {
            DeleteMethod method = new DeleteMethod(RESTEASY_MOM_URI + "queues/A/listeners/2");
            client.executeMethod(method);
            method.releaseConnection();
         }
         server.stop();
      }
   }

   @Test
   public void testReceiver() throws Exception
   {
      HttpClient client = new HttpClient();
      try
      {
         {
            PutMethod method = new PutMethod(RESTEASY_MOM_URI + "queues/A/receivers/1");
            method.addRequestHeader(HttpHeaderNames.ACCEPT, "text/plain");
            int status = client.executeMethod(method);
            Assert.assertEquals(HttpResponseCodes.SC_CREATED, status);
            method.releaseConnection();
         }
         {
            PutMethod method = new PutMethod(RESTEASY_MOM_URI + "queues/A/receivers/2");
            method.addRequestHeader(HttpHeaderNames.ACCEPT, "application/xml");
            int status = client.executeMethod(method);
            Assert.assertEquals(HttpResponseCodes.SC_CREATED, status);
            method.releaseConnection();
         }
         postMessage(client, "text/plain", "stupid message");
         postMessage(client, "application/xml", "<hello/>");
         postMessage(client, "text/plain", "stupid message");
         postMessage(client, "text/plain", "stupid message");
         postMessage(client, "text/plain", "stupid message");
         postMessage(client, "application/xml", "<hello/>");
         postMessage(client, "application/xml", "<hello/>");
         postMessage(client, "text/plain", "stupid message");
         postMessage(client, "application/xml", "<hello/>");
         postMessage(client, "application/xml", "<hello/>");

         getViaPost(client, RESTEASY_MOM_URI + "queues/A/receivers/1", "stupid message");
         getViaPost(client, RESTEASY_MOM_URI + "queues/A/receivers/1", "stupid message");
         getViaPost(client, RESTEASY_MOM_URI + "queues/A/receivers/1", "stupid message");
         getViaPost(client, RESTEASY_MOM_URI + "queues/A/receivers/1", "stupid message");
         getViaPost(client, RESTEASY_MOM_URI + "queues/A/receivers/1", "stupid message");

         getViaPost(client, RESTEASY_MOM_URI + "queues/A/receivers/2", "<hello/>");
         getViaPost(client, RESTEASY_MOM_URI + "queues/A/receivers/2", "<hello/>");
         getViaPost(client, RESTEASY_MOM_URI + "queues/A/receivers/2", "<hello/>");
         getViaPost(client, RESTEASY_MOM_URI + "queues/A/receivers/2", "<hello/>");
         getViaPost(client, RESTEASY_MOM_URI + "queues/A/receivers/2", "<hello/>");
      }
      finally
      {
         {
            DeleteMethod method = new DeleteMethod(RESTEASY_MOM_URI + "queues/A/receivers/1");
            client.executeMethod(method);
            method.releaseConnection();
         }
         {
            DeleteMethod method = new DeleteMethod(RESTEASY_MOM_URI + "queues/A/receivers/2");
            client.executeMethod(method);
            method.releaseConnection();
         }
      }
   }

   private void getViaPost(HttpClient client, String receiver, String test)
           throws IOException
   {
      PostMethod method = new PostMethod(receiver + "/head");
      NameValuePair[] params = {new NameValuePair("wait", "1000")};
      method.setQueryString(params);
      int status = client.executeMethod(method);
      Assert.assertEquals(HttpResponseCodes.SC_OK, status);
      byte[] responseBody = method.getResponseBody();
      String response = new String(responseBody, "US-ASCII");
      Assert.assertEquals(test, response);
      method.releaseConnection();
   }


   private void postMessage(HttpClient client, String mediaType, String content)
           throws IOException
   {
      PostMethod method = new PostMethod(RESTEASY_MOM_URI + "queues/A");
      method.setRequestEntity(new StringRequestEntity(content, mediaType, null));
      int status = client.executeMethod(method);
      Assert.assertEquals(HttpResponseCodes.SC_NO_CONTENT, status);
      method.releaseConnection();
   }
}
