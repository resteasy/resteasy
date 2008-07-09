package org.jboss.resteasy.test.asynch;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.jboss.resteasy.core.AsynchronousDispatcher;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.jboss.resteasy.test.TJWSServletContainer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class AsynchTest
{
   private static CountDownLatch latch;

   @Path("/")
   public static class MyResource
   {
      @POST
      public String post(String content)
      {

         return content;
      }

      @PUT
      public void put(String content) throws Exception
      {
         System.out.println("IN PUT!!!!");
         Assert.assertEquals("content", content);
         Thread.sleep(500);
         System.out.println("******* countdown ****");
         latch.countDown();
      }
   }


   @BeforeClass
   public static void before() throws Exception
   {
      AsynchronousDispatcher dispatcher = new AsynchronousDispatcher();
      TJWSServletContainer.start(dispatcher);
      dispatcher.start();
      dispatcher.getRegistry().addPerRequestResource(MyResource.class);
   }

   @AfterClass
   public static void after() throws Exception
   {
      EmbeddedContainer.stop();
   }

   @Test
   public void testOneway() throws Exception
   {
      HttpClient client = new HttpClient();
      {
         latch = new CountDownLatch(1);
         PutMethod method = new PutMethod("http://localhost:8081?oneway=true");
         method.setRequestEntity(new StringRequestEntity("content", "text/plain", null));
         long start = System.currentTimeMillis();
         int status = client.executeMethod(method);
         long end = System.currentTimeMillis() - start;
         Assert.assertEquals(HttpServletResponse.SC_ACCEPTED, status);
         Assert.assertTrue(end < 1000);
         Assert.assertTrue(latch.await(2, TimeUnit.SECONDS));


         method.releaseConnection();
      }

   }


}
