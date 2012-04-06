package org.jboss.resteasy.test.asynch;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.core.AsynchronousDispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;

import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.jboss.resteasy.test.TestPortProvider.*;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class AsynchTest
{
   private static CountDownLatch latch;

   private static AsynchronousDispatcher dispatcher;

   @Path("/")
   public static class MyResource
   {
      @Context
      private ServletConfig config;

      @Context
      private ServletContext context;


      @POST
      public String post(String content) throws Exception
      {
         System.out.println("in post");
         Assert.assertNotNull(config);
         Assert.assertNotNull(context);
         System.out.println("Asserts passed");
         config.getServletContext();
         context.getMajorVersion();
         System.out.println("Called injected passed");

         Thread.sleep(1500);
         latch.countDown();

         return content;
      }

      @PUT
      public void put(String content) throws Exception
      {
         System.out.println("IN PUT!!!!");
         Assert.assertNotNull(config);
         Assert.assertNotNull(context);
         System.out.println("Asserts passed");
         config.getServletContext();
         context.getMajorVersion();
         System.out.println("Called injected passed");
         Assert.assertEquals("content", content);
         Thread.sleep(500);
         System.out.println("******* countdown ****");
         latch.countDown();
         System.out.println("******* countdown complete ****");
      }
   }

   @BeforeClass
   public static void before() throws Exception
   {
      ResteasyDeployment deployment = new ResteasyDeployment();
      deployment.setAsyncJobServiceEnabled(true);
      EmbeddedContainer.start(deployment);

      dispatcher = (AsynchronousDispatcher) deployment.getDispatcher();
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
      ClientRequest request = new ClientRequest(generateURL("?oneway=true"));
      request.body("text/plain", "content");
      ClientResponse<?> response = null;
      try
      {
         latch = new CountDownLatch(1);
         long start = System.currentTimeMillis();
         response = request.put();
         long end = System.currentTimeMillis() - start;
         Assert.assertEquals(HttpServletResponse.SC_ACCEPTED, response.getStatus());
         Assert.assertTrue(end < 1000);
         Assert.assertTrue(latch.await(2, TimeUnit.SECONDS));
      }
      finally
      {
         response.releaseConnection();
      }
   }

   @Test
   public void testAsynch() throws Exception
   {
      ClientResponse<?> response = null;
      ClientRequest request = null;
      
      {
         latch = new CountDownLatch(1);
         request = new ClientRequest(generateURL("?asynch=true"));
         request.body("text/plain", "content");
         long start = System.currentTimeMillis();
         response = request.post();
         @SuppressWarnings("unused")
         long end = System.currentTimeMillis() - start;
         Assert.assertEquals(HttpServletResponse.SC_ACCEPTED, response.getStatus());
         String jobUrl = response.getHeaders().getFirst(HttpHeaders.LOCATION);
         System.out.println("JOB: " + jobUrl);
         response.releaseConnection();

         request = new ClientRequest(jobUrl);
         response = request.get();
         Assert.assertTrue(latch.await(3, TimeUnit.SECONDS));
         response.releaseConnection();
         // there's a lag between when the latch completes and the executor
         // registers the completion of the call 
         URI uri = new URI(request.getUri());
         String query = (uri.getQuery() == null ? "" : "&") + "wait=1000";
         URI newURI = new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), query, uri.getFragment());
         request = new ClientRequest(newURI.toString());
         response = request.get();
         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
         Assert.assertEquals(response.getEntity(String.class), "content");

         // test its still there
         response = request.get();
         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
         Assert.assertEquals(response.getEntity(String.class), "content");

         // delete and test delete
         request = new ClientRequest(jobUrl);
         response = request.delete();
         Assert.assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());
         response = request.get();
         Assert.assertEquals(HttpServletResponse.SC_GONE, response.getStatus());
         response.releaseConnection();
      }
      
      // test cache size
      {
         dispatcher.setMaxCacheSize(1);
         latch = new CountDownLatch(1);
         request = new ClientRequest(generateURL("?asynch=true"));
         request.body("text/plain", "content");
         response = request.post();
         Assert.assertEquals(HttpServletResponse.SC_ACCEPTED, response.getStatus());
         String jobUrl1 = response.getHeaders().getFirst(HttpHeaders.LOCATION);
         Assert.assertTrue(latch.await(3, TimeUnit.SECONDS));
         response.releaseConnection();
         
         latch = new CountDownLatch(1);
         response = request.post();
         Assert.assertEquals(HttpServletResponse.SC_ACCEPTED, response.getStatus());
         String jobUrl2 = response.getHeaders().getFirst(HttpHeaders.LOCATION);
         Assert.assertTrue(latch.await(3, TimeUnit.SECONDS));
         Assert.assertTrue(!jobUrl1.equals(jobUrl2));

         request = new ClientRequest(jobUrl1);
         response = request.get();
         Assert.assertEquals(HttpServletResponse.SC_GONE, response.getStatus());
         response.releaseConnection();

         // test its still there
         request = new ClientRequest(jobUrl2);
         response = request.get();
         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
         Assert.assertEquals(response.getEntity(String.class), "content");

         // delete and test delete
         request = new ClientRequest(jobUrl2);
         response = request.delete();
         Assert.assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());
         response = request.get();
         Assert.assertEquals(HttpServletResponse.SC_GONE, response.getStatus());
         response.releaseConnection();
      }
      
      // test readAndRemove
      {
         dispatcher.setMaxCacheSize(10);
         latch = new CountDownLatch(1);
         request = new ClientRequest(generateURL("?asynch=true"));
         request.body("text/plain", "content");
         response = request.post();
         Assert.assertEquals(HttpServletResponse.SC_ACCEPTED, response.getStatus());
         String jobUrl2 = response.getHeaders().getFirst(HttpHeaders.LOCATION);
         Assert.assertTrue(latch.await(3, TimeUnit.SECONDS));
  
         // test its still there
         request = new ClientRequest(jobUrl2);
         response = request.post();
         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus()) ;
         Assert.assertEquals(response.getEntity(String.class), "content");         

         response = request.get();
         Assert.assertEquals(HttpServletResponse.SC_GONE, response.getStatus());
         response.releaseConnection();
      }
   }

}
