package org.resteasy.mom.test.topic;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.junit.Assert;
import org.junit.Test;
import org.resteasy.Dispatcher;
import org.resteasy.plugins.server.tjws.TJWSEmbeddedJaxrsServer;
import org.resteasy.util.HttpResponseCodes;

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
public class TopicTest
{
   private static final String RESTEASY_MOM_URI = "http://localhost:8080/resteasy-mom/";
   private static final String RECEIVER_1 = RESTEASY_MOM_URI + "topics/testTopic/receivers/1";

   private static String lastMessage;
   private static CountDownLatch latch;

   /*
   @Test
   public void testDummy() throws Exception
   {
      HttpClient client = new HttpClient();
      postStupidMessageXmlMessage(client);
      Thread.sleep(3000000);
   }
   */

   @Test
   public void testDummy()
   {
   }

   @Test
   public void testTopicReceiverClientAcknowledged() throws Exception
   {
      HttpClient client = new HttpClient();

      createReceiver1(client);
      try
      {
         // test idempotence
         {
            PutMethod method = new PutMethod(RECEIVER_1);
            int status = client.executeMethod(method);
            Assert.assertEquals(HttpResponseCodes.SC_OK, status);
            method.releaseConnection();

         }
         postStupidMessage(client);
         post2ndStupidMessage(client);


         getStupidMethod(client);
         getStupidMethod(client);
         acknowledge(client);
         get2ndStupidMethod(client);
         acknowledge(client);
      }
      finally
      {
         deleteReceiver1(client);
      }
   }

   @Path("/")
   public static class Listener
   {
      @Path("/listener")
      @POST
      public Response post(String msg)
      {
         lastMessage = msg;
         latch.countDown();
         return Response.ok().build();
      }
   }

   @Test
   public void testtopicListener() throws Exception
   {
      TJWSEmbeddedJaxrsServer server = new TJWSEmbeddedJaxrsServer();
      server.setPort(8081);
      server.start();
      Dispatcher dispatcher = server.getDispatcher();
      HttpClient client = new HttpClient();
      try
      {
         dispatcher.getRegistry().addPerRequestResource(Listener.class);
         {
            PutMethod method = new PutMethod(RESTEASY_MOM_URI + "topics/testTopic/listeners/1");
            method.setRequestEntity(new StringRequestEntity("http://localhost:8081/listener", "text/plain", null));
            int status = client.executeMethod(method);
            Assert.assertEquals(HttpResponseCodes.SC_CREATED, status);
            method.releaseConnection();
         }
         latch = new CountDownLatch(1);
         postStupidMessage(client);
         Assert.assertTrue(latch.await(2, TimeUnit.SECONDS));

         Assert.assertEquals("stupid message", lastMessage);


      }
      finally
      {
         DeleteMethod method = new DeleteMethod(RESTEASY_MOM_URI + "topics/testTopic/listeners/1");
         client.executeMethod(method);
         method.releaseConnection();
         server.stop();
      }
   }

   @Test
   public void testtopicListenerFailure() throws Exception
   {
      TJWSEmbeddedJaxrsServer server = new TJWSEmbeddedJaxrsServer();
      server.setPort(8081);
      server.start();
      Dispatcher dispatcher = server.getDispatcher();
      HttpClient client = new HttpClient();
      try
      {
         dispatcher.getRegistry().addPerRequestResource(Listener.class);
         {
            PutMethod method = new PutMethod(RESTEASY_MOM_URI + "topics/testTopic/listeners/errorTesting");
            method.setRequestEntity(new StringRequestEntity("http://localhost:8085/listener", "text/plain", null));
            int status = client.executeMethod(method);
            Assert.assertEquals(HttpResponseCodes.SC_CREATED, status);
            method.releaseConnection();
         }
         postStupidMessage(client);
         Thread.sleep(1000);
         getStupidMethodViaPost(client, RESTEASY_MOM_URI + "queues/DLQ");
      }
      finally
      {
         try
         {
            DeleteMethod method = new DeleteMethod(RESTEASY_MOM_URI + "topics/testTopic/listeners/errorTesting");
            client.executeMethod(method);
            method.releaseConnection();
         }
         catch (Exception ignored) {}
         server.stop();
      }
   }

   @Path("/")
   public static class BigMessageListener
   {
      public static String compareTo;
      public static boolean failure = false;

      @Path("/biglistener")
      @POST
      public Response post(String msg)
      {
         if (!msg.equals(compareTo)) failure = true;
         latch.countDown();
         return Response.ok().build();
      }
   }

   @Test
   public void testtopicListenerBigMessage() throws Exception
   {
      TJWSEmbeddedJaxrsServer server = new TJWSEmbeddedJaxrsServer();
      server.setPort(8081);
      server.start();
      Dispatcher dispatcher = server.getDispatcher();
      HttpClient client = new HttpClient();
      try
      {
         dispatcher.getRegistry().addPerRequestResource(BigMessageListener.class);
         {
            PutMethod method = new PutMethod(RESTEASY_MOM_URI + "topics/testTopic/listeners/1");
            method.setRequestEntity(new StringRequestEntity("http://localhost:8081/biglistener", "text/plain", null));
            int status = client.executeMethod(method);
            Assert.assertEquals(HttpResponseCodes.SC_CREATED, status);
            method.releaseConnection();
         }
         latch = new CountDownLatch(1);

         StringBuffer msg = new StringBuffer();
         for (int i = 0; i < 1000; i++)
         {
            msg.append(Integer.toString(i));
         }
         BigMessageListener.compareTo = msg.toString();


         PostMethod method = new PostMethod(RESTEASY_MOM_URI + "topics/testTopic");
         method.setRequestEntity(new StringRequestEntity(BigMessageListener.compareTo, "text/plain", null));
         int status = client.executeMethod(method);
         Assert.assertEquals(HttpResponseCodes.SC_OK, status);
         method.releaseConnection();
         Assert.assertTrue(latch.await(2, TimeUnit.SECONDS));

         Assert.assertFalse(BigMessageListener.failure);
      }
      finally
      {
         DeleteMethod method = new DeleteMethod(RESTEASY_MOM_URI + "topics/testTopic/listeners/1");
         client.executeMethod(method);
         method.releaseConnection();
         server.stop();
      }
   }

   private void deleteReceiver1(HttpClient client)
           throws IOException
   {
      // delete receiver 1
      {
         DeleteMethod method = new DeleteMethod(RECEIVER_1);
         int status = client.executeMethod(method);
         Assert.assertEquals(HttpResponseCodes.SC_NO_CONTENT, status);
         method.releaseConnection();
      }
   }

   private void createReceiver1(HttpClient client)
           throws IOException
   {
      PutMethod method = new PutMethod(RECEIVER_1);
      int status = client.executeMethod(method);
      Assert.assertEquals(HttpResponseCodes.SC_CREATED, status);
      method.releaseConnection();
   }

   private void get2ndStupidMethod(HttpClient client)
           throws IOException
   {
      GetMethod method = new GetMethod(RECEIVER_1 + "/head");
      NameValuePair[] params = {new NameValuePair("wait", "1000")};
      method.setQueryString(params);
      int status = client.executeMethod(method);
      Assert.assertEquals(HttpResponseCodes.SC_OK, status);
      byte[] responseBody = method.getResponseBody();
      System.out.println("body length: " + responseBody.length);
      String response = new String(responseBody, "US-ASCII");
      Assert.assertEquals("2nd stupid message", response);
      method.releaseConnection();
   }

   private void acknowledge(HttpClient client)
           throws IOException
   {
      DeleteMethod method = new DeleteMethod(RECEIVER_1 + "/head");
      int status = client.executeMethod(method);
      Assert.assertEquals(HttpResponseCodes.SC_OK, status);
      method.releaseConnection();
   }

   private void getStupidMethod(HttpClient client)
           throws IOException
   {
      GetMethod method = new GetMethod(RECEIVER_1 + "/head");
      NameValuePair[] params = {new NameValuePair("wait", "1000")};
      method.setQueryString(params);
      int status = client.executeMethod(method);
      Assert.assertEquals(HttpResponseCodes.SC_OK, status);
      byte[] responseBody = method.getResponseBody();
      System.out.println("body length: " + responseBody.length);
      String response = new String(responseBody, "US-ASCII");
      Assert.assertEquals("stupid message", response);
      method.releaseConnection();
   }

   private void getStupidMethodViaPost(HttpClient client, String receiver)
           throws IOException
   {
      PostMethod method = new PostMethod(receiver + "/head");
      NameValuePair[] params = {new NameValuePair("wait", "1000")};
      method.setQueryString(params);
      int status = client.executeMethod(method);
      Assert.assertEquals(HttpResponseCodes.SC_OK, status);
      byte[] responseBody = method.getResponseBody();
      System.out.println("body length: " + responseBody.length);
      String response = new String(responseBody, "US-ASCII");
      Assert.assertEquals("stupid message", response);
      method.releaseConnection();
   }

   private void post2ndStupidMessage(HttpClient client)
           throws IOException
   {
      PostMethod method = new PostMethod(RESTEASY_MOM_URI + "topics/testTopic");
      method.setRequestEntity(new StringRequestEntity("2nd stupid message", "text/plain", null));
      int status = client.executeMethod(method);
      Assert.assertEquals(HttpResponseCodes.SC_OK, status);
      method.releaseConnection();
   }

   private void postStupidMessage(HttpClient client)
           throws IOException
   {
      PostMethod method = new PostMethod(RESTEASY_MOM_URI + "topics/testTopic");
      method.setRequestEntity(new StringRequestEntity("stupid message", "text/plain", null));
      int status = client.executeMethod(method);
      Assert.assertEquals(HttpResponseCodes.SC_OK, status);
      method.releaseConnection();
   }

}