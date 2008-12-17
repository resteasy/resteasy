package org.jboss.resteasy.springmvc.test.spring;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.HttpHeaders;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.jboss.resteasy.core.AsynchronousDispatcher;
import org.jboss.resteasy.springmvc.tjws.TJWSEmbeddedSpringMVCServerBean;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations =
{ "classpath:/spring-test-async.xml" })
public class AsynchSpringTest
{
   private static CountDownLatch latch;

   @Path("/")
   public static class MyResource
   {
      @POST
      public String post(String content) throws Exception
      {
         Thread.sleep(1500);
         latch.countDown();

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

   AsynchronousDispatcher dispatcher;

   @Autowired
   public void setServer(TJWSEmbeddedSpringMVCServerBean server)
   {
      dispatcher = (AsynchronousDispatcher) server.getServer()
            .getApplicationContext().getBeansOfType(
                  AsynchronousDispatcher.class).values().iterator().next();
   }

   @Test
   public void testOneway() throws Exception
   {
      HttpClient client = new HttpClient();
      {
         latch = new CountDownLatch(1);
         PutMethod method = new PutMethod("http://localhost:9091?oneway=true");
         method.setRequestEntity(new StringRequestEntity("content",
               "text/plain", null));
         long start = System.currentTimeMillis();
         int status = client.executeMethod(method);
         long end = System.currentTimeMillis() - start;
         Assert.assertEquals(HttpServletResponse.SC_ACCEPTED, status);
         Assert.assertTrue(end < 1000);
         Assert.assertTrue(latch.await(2, TimeUnit.SECONDS));

         method.releaseConnection();
      }
   }

   @Test
   public void testAsynch() throws Exception
   {
      HttpClient client = new HttpClient();
      {
         latch = new CountDownLatch(1);
         PostMethod method = new PostMethod("http://localhost:9091?asynch=true");
         method.setRequestEntity(new StringRequestEntity("content",
               "text/plain", null));
         long start = System.currentTimeMillis();
         int status = client.executeMethod(method);
         @SuppressWarnings("unused")
         long end = System.currentTimeMillis() - start;
         Assert.assertEquals(HttpServletResponse.SC_ACCEPTED, status);
         String jobUrl = method.getResponseHeader(HttpHeaders.LOCATION)
               .getValue();
         System.out.println("JOB: " + jobUrl);
         GetMethod get = new GetMethod(jobUrl);
         status = client.executeMethod(get);
         Assert.assertEquals(HttpServletResponse.SC_ACCEPTED, status);
         Assert.assertTrue(latch.await(3, TimeUnit.SECONDS));
         // there's a lag between when the latch completes and the executor
         // registers the completion of the call 
         Thread.sleep(150);
         status = client.executeMethod(get);
         Assert.assertEquals(HttpServletResponse.SC_OK, status);
         Assert.assertEquals(get.getResponseBodyAsString(), "content");

         // test its still there
         status = client.executeMethod(get);
         Assert.assertEquals(HttpServletResponse.SC_OK, status);
         Assert.assertEquals(get.getResponseBodyAsString(), "content");

         // delete and test delete
         DeleteMethod delete = new DeleteMethod(jobUrl);
         status = client.executeMethod(delete);
         Assert.assertEquals(HttpServletResponse.SC_NO_CONTENT, status);

         status = client.executeMethod(get);
         Assert.assertEquals(HttpServletResponse.SC_GONE, status);

         method.releaseConnection();
      }

      {
         dispatcher.setMaxCacheSize(1);
         latch = new CountDownLatch(1);
         PostMethod method = new PostMethod("http://localhost:9091?asynch=true");
         method.setRequestEntity(new StringRequestEntity("content",
               "text/plain", null));
         int status = client.executeMethod(method);
         Assert.assertEquals(HttpServletResponse.SC_ACCEPTED, status);
         String jobUrl1 = method.getResponseHeader(HttpHeaders.LOCATION)
               .getValue();
         Assert.assertTrue(latch.await(3, TimeUnit.SECONDS));

         latch = new CountDownLatch(1);
         method.setRequestEntity(new StringRequestEntity("content",
               "text/plain", null));
         status = client.executeMethod(method);
         Assert.assertEquals(HttpServletResponse.SC_ACCEPTED, status);
         String jobUrl2 = method.getResponseHeader(HttpHeaders.LOCATION)
               .getValue();
         Assert.assertTrue(latch.await(3, TimeUnit.SECONDS));

         Assert.assertTrue(!jobUrl1.equals(jobUrl2));

         GetMethod get = new GetMethod(jobUrl1);
         status = client.executeMethod(get);
         Assert.assertEquals(HttpServletResponse.SC_GONE, status);

         // test its still there
         get = new GetMethod(jobUrl2);
         status = client.executeMethod(get);
         Assert.assertEquals(HttpServletResponse.SC_OK, status);
         Assert.assertEquals(get.getResponseBodyAsString(), "content");

         // delete and test delete
         DeleteMethod delete = new DeleteMethod(jobUrl2);
         status = client.executeMethod(delete);
         Assert.assertEquals(HttpServletResponse.SC_NO_CONTENT, status);

         status = client.executeMethod(get);
         Assert.assertEquals(HttpServletResponse.SC_GONE, status);

         method.releaseConnection();
      }
      // test readAndRemove
      {
         dispatcher.setMaxCacheSize(10);
         latch = new CountDownLatch(1);
         PostMethod method = new PostMethod("http://localhost:9091?asynch=true");
         method.setRequestEntity(new StringRequestEntity("content",
               "text/plain", null));
         int status = client.executeMethod(method);
         Assert.assertEquals(HttpServletResponse.SC_ACCEPTED, status);
         String jobUrl2 = method.getResponseHeader(HttpHeaders.LOCATION)
               .getValue();
         Assert.assertTrue(latch.await(3, TimeUnit.SECONDS));

         // test its still there
         PostMethod post = new PostMethod(jobUrl2);
         status = client.executeMethod(post);
         Assert.assertEquals(HttpServletResponse.SC_OK, status);
         Assert.assertEquals(post.getResponseBodyAsString(), "content");

         GetMethod get = new GetMethod(jobUrl2);
         status = client.executeMethod(get);
         Assert.assertEquals(HttpServletResponse.SC_GONE, status);

         method.releaseConnection();
      }
   }

}
