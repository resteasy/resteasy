package org.jboss.resteasy.test.client.old;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.ClientExecutor;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;
import static org.junit.Assert.fail;

/**
 * Unit tests for https://issues.jboss.org/browse/RESTEASY-621.
 * 
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1 $
 */
public class ClientExecutorShutdownTest extends BaseResourceTest
{
   private static Logger log = Logger.getLogger(ClientExecutorShutdownTest.class);
   
   @Path("/test")
   public interface TestService
   {  
      @POST
      ClientResponse<String> post();
   }
   
   @Path("/test")
   public static class TestServiceImpl
   {
      @POST
      public void post()
      {
         log.info("In POST");
      }
   }

   @Before
   public void setUp() throws Exception
   {
      addPerRequestResource(TestServiceImpl.class);
   }

   /**
    * Verify that each ClientRequest gets its own ClientExecutor
    * if one is not supplied through the constructor.
    */
   @Test
   public void testClientRequestNonSharedExecutor() throws Exception
   {
      ClientRequest request1 = new ClientRequest(generateURL("/test"));
      ClientResponse<?> response1 = request1.post();
      Assert.assertEquals(204, response1.getStatus());
      ClientRequest request2 = new ClientRequest(generateURL("/test"));
      ClientResponse<?> response2 = request2.post();
      Assert.assertEquals(204, response2.getStatus());
      ClientExecutor executor1 = request1.getExecutor();
      ClientExecutor executor2 = request2.getExecutor();
      Assert.assertNotSame(executor1, executor2);
      executor2.close();
      response1 = request1.post();
      Assert.assertEquals(204, response1.getStatus());
      executor1.close();

   }
   
   /**
    * Verify that ClientRequest uses the ClientExecutor
    * supplied through a constructor.
    */
   @Test
   public void testClientRequestSharedExecutor() throws Exception
   {
      ClientExecutor executor = new ApacheHttpClient4Executor();
      ClientRequest request1 = new ClientRequest(generateURL("/test"), executor);
      ClientResponse<?> response1 = request1.post();
      Assert.assertEquals(204, response1.getStatus());
      ClientRequest request2 = new ClientRequest(generateURL("/test"), executor);
      ClientResponse<?> response2 = request2.post();
      Assert.assertEquals(204, response2.getStatus());
      ClientExecutor executor1 = request1.getExecutor();
      ClientExecutor executor2 = request2.getExecutor();
      Assert.assertSame(executor, executor1);
      Assert.assertSame(executor, executor2);
      executor.close();
   }
   
   /**
    * Verify that if ApacheHttpClient4Executor creates its own HttpClient,
    * then ApacheHttpClient4Executor.finalize() will close the HttpClient's
    * org.apache.http.conn.ClientConnectionManager.
    */
   @Test
   public void testApacheHttpClient4ExecutorNonSharedHttpClientFinalize() throws Throwable
   {
      ApacheHttpClient4Executor executor = new ApacheHttpClient4Executor();
      ClientRequest request = new ClientRequest(generateURL("/test"), executor);
      ClientResponse<?> response = request.post();
      Assert.assertEquals(204, response.getStatus());
      executor.finalize();
      HttpClient httpClient = executor.getHttpClient();
      HttpPost post = new HttpPost(generateURL("/test"));
      try
      {
         httpClient.execute(post);
         fail("Expected IllegalStateException");
      }
      catch (IllegalStateException e)
      {
         log.info("Got expected IllegalStateException");
      }
   }
   
   /**
    * Verify that if ApacheHttpClient4Executor creates its own HttpClient,
    * then ApacheHttpClient4Executor.close() will close the HttpClient's
    * org.apache.http.conn.ClientConnectionManager.
    */
   @Test
   public void testApacheHttpClient4ExecutorNonSharedHttpClientClose() throws Throwable
   {
      ApacheHttpClient4Executor executor = new ApacheHttpClient4Executor();
      ClientRequest request = new ClientRequest(generateURL("/test"), executor);
      ClientResponse<?> response = request.post();
      Assert.assertEquals(204, response.getStatus());
      executor.close();
      HttpClient httpClient = executor.getHttpClient();
      HttpPost post = new HttpPost(generateURL("/test"));
      try
      {
         httpClient.execute(post);
         fail("Expected IllegalStateException");
      }
      catch (IllegalStateException e)
      {
         log.info("Got expected IllegalStateException");
      }
   }
   
   /**
    * Verify that if ApacheHttpClient4Executor receives an HttpClient through
    * a constructor, then ApacheHttpClient4Executor.finalize() will not close the
    * HttpClient's org.apache.http.conn.ClientConnectionManager.
    */
   @Test
   public void testApacheHttpClient4ExecutorSharedHttpClientFinalize() throws Throwable
   {
      HttpClient httpClient = new DefaultHttpClient();
      ApacheHttpClient4Executor executor = new ApacheHttpClient4Executor(httpClient);
      ClientRequest request = new ClientRequest(generateURL("/test"), executor);
      ClientResponse<?> response = request.post();
      Assert.assertEquals(204, response.getStatus());
      executor.finalize();
      Assert.assertEquals(httpClient, executor.getHttpClient());
      HttpPost post = new HttpPost(generateURL("/test"));
      HttpResponse httpResponse = httpClient.execute(post);
      Assert.assertEquals(204, httpResponse.getStatusLine().getStatusCode());
      httpClient.getConnectionManager().shutdown();
   }
   
   /**
    * Verify that if ApacheHttpClient4Executor receives an HttpClient through
    * a constructor, then ApacheHttpClient4Executor.close() will not close the
    * HttpClient's org.apache.http.conn.ClientConnectionManager.
    */
   @Test
   public void testApacheHttpClient4ExecutorSharedHttpClientClose() throws Throwable
   {
      HttpClient httpClient = new DefaultHttpClient();
      ApacheHttpClient4Executor executor = new ApacheHttpClient4Executor(httpClient);
      ClientRequest request = new ClientRequest(generateURL("/test"), executor);
      ClientResponse<?> response = request.post();
      Assert.assertEquals(204, response.getStatus());
      executor.close();
      Assert.assertEquals(httpClient, executor.getHttpClient());
      HttpPost post = new HttpPost(generateURL("/test"));
      HttpResponse httpResponse = httpClient.execute(post);
      Assert.assertEquals(204, httpResponse.getStatusLine().getStatusCode());
      httpClient.getConnectionManager().shutdown();
   }
}
