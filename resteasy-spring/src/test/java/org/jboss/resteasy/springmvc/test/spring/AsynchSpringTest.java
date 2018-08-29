package org.jboss.resteasy.springmvc.test.spring;

import org.jboss.logging.Logger;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.core.AsynchronousDispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.springmvc.tjws.TJWSEmbeddedSpringMVCServerBean;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.HttpHeaders;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations =
{ "classpath:/spring-test-async.xml" })
@DirtiesContext
public class AsynchSpringTest
{
   private static final Logger LOG = Logger.getLogger(AsynchSpringTest.class);
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
         LOG.info("IN PUT!!!!");
         Assert.assertEquals("content", content);
         Thread.sleep(500);
         LOG.info("******* countdown ****");
         latch.countDown();
      }
   }

   AsynchronousDispatcher dispatcher;

   @Autowired
   public void setServer(TJWSEmbeddedSpringMVCServerBean server)
   {
      ResteasyDeployment deployment = (ResteasyDeployment)server.getServer()
            .getApplicationContext().getBeansOfType(
                  ResteasyDeployment.class).values().iterator().next();
      dispatcher = (AsynchronousDispatcher)deployment.getDispatcher();
   }

   @Test
   public void testOneway() throws Exception
   {
      latch = new CountDownLatch(1);
      ClientRequest request = new ClientRequest("http://localhost:9091?oneway=true");
      request.body("text/plain", "content");
      long start = System.currentTimeMillis();
      ClientResponse<?> response = request.put();
      long end = System.currentTimeMillis() - start;
      Assert.assertEquals(HttpServletResponse.SC_ACCEPTED, response.getStatus());
      Assert.assertTrue(end < 1000);
      Assert.assertTrue(latch.await(2, TimeUnit.SECONDS));
      response.releaseConnection();
   }
   
   @Test
   public void testAsynch() throws Exception
   {
      ClientRequest request = null;
      ClientResponse<?> response = null;
      {
         latch = new CountDownLatch(1);
         request = new ClientRequest("http://localhost:9091?asynch=true");
         request.body("text/plain", "content");
         long start = System.currentTimeMillis();
         response = request.post();
         @SuppressWarnings("unused")
         long end = System.currentTimeMillis() - start;
         Assert.assertEquals(HttpServletResponse.SC_ACCEPTED, response.getStatus());
         String jobUrl = response.getResponseHeaders().getFirst(HttpHeaders.LOCATION);
         LOG.info("JOB: " + jobUrl);
         response.releaseConnection();
         
         request = new ClientRequest(jobUrl);
         response = request.get();
         Assert.assertEquals(HttpServletResponse.SC_ACCEPTED, response.getStatus());
         Assert.assertTrue(latch.await(3, TimeUnit.SECONDS));
         response.releaseConnection();
         // there's a lag between when the latch completes and the executor
         // registers the completion of the call
         URI oldUri = new URI(request.getUri());
         String existingQueryString = oldUri.getQuery();
         String newQuery = (existingQueryString == null ? "" : "&") + "wait=1000";
         URI newUri = new URI(oldUri.getScheme(), oldUri.getAuthority(), oldUri.getPath(), newQuery, oldUri.getFragment());
         request = new ClientRequest(newUri.toString());
         response = request.get();
         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
         Assert.assertEquals("content", response.getEntity(String.class));
         
         // test its still there
         response = request.get();
         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
         Assert.assertEquals("content", response.getEntity(String.class));

         // delete and test delete
         request = new ClientRequest(jobUrl);
         response = request.delete();
         Assert.assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());      
         
         response = request.get();
         Assert.assertEquals(HttpServletResponse.SC_GONE, response.getStatus());
         response.releaseConnection();
      }

      {
         dispatcher.setMaxCacheSize(1);
         latch = new CountDownLatch(1);
         request = new ClientRequest("http://localhost:9091?asynch=true");
         request.body("text/plain", "content");
         response = request.post();
         Assert.assertEquals(HttpServletResponse.SC_ACCEPTED, response.getStatus());
         String jobUrl1 = response.getResponseHeaders().getFirst(HttpHeaders.LOCATION);
         Assert.assertTrue(latch.await(3, TimeUnit.SECONDS));
         response.releaseConnection();

         latch = new CountDownLatch(1);
         response = request.post();
         Assert.assertEquals(HttpServletResponse.SC_ACCEPTED, response.getStatus());
         String jobUrl2 = response.getResponseHeaders().getFirst(HttpHeaders.LOCATION);
         Assert.assertTrue(latch.await(3, TimeUnit.SECONDS));
         Assert.assertTrue(!jobUrl1.equals(jobUrl2));
         response.releaseConnection();
         
         request = new ClientRequest(jobUrl1);
         response = request.get();
         Assert.assertEquals(HttpServletResponse.SC_GONE, response.getStatus());
         response.releaseConnection();

         // test its still there
         Thread.sleep(1000);
         request = new ClientRequest(jobUrl2);
         response = request.get();
         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
         Assert.assertEquals("content", response.getEntity(String.class));
         
         // delete and test delete
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
         request = new ClientRequest("http://localhost:9091?asynch=true");
         request.body("text/plain", "content");
         response = request.post();
         Assert.assertEquals(HttpServletResponse.SC_ACCEPTED, response.getStatus());
         String jobUrl2 = response.getResponseHeaders().getFirst(HttpHeaders.LOCATION);
         Assert.assertTrue(latch.await(3, TimeUnit.SECONDS));
         response.releaseConnection();

         Thread.sleep(1000);
         // test its still there
         request = new ClientRequest(jobUrl2);
         response = request.post();
         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
         Assert.assertEquals("content", response.getEntity(String.class));
         
         response = request.get();
         Assert.assertEquals(HttpServletResponse.SC_GONE, response.getStatus());
         response.releaseConnection();
      }
   }
}
