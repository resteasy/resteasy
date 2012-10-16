package org.jboss.resteasy.test.nextgen.client;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;
import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

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
         System.out.println("In POST");
      }
   }

   @Before
   public void setUp() throws Exception
   {
      addPerRequestResource(TestServiceImpl.class);
   }

   /**
    * Verify that if ApacheHttpClient4Executor creates its own HttpClient,
    * then ApacheHttpClient4Executor.finalize() will close the HttpClient's
    * org.apache.http.conn.ClientConnectionManager.
    */
   @Test
   public void testApacheHttpClient4ExecutorNonSharedHttpClientFinalize() throws Throwable
   {
      ApacheHttpClient4Engine engine = new ApacheHttpClient4Engine();
      ResteasyClient client = new ResteasyClient();
      client.httpEngine(engine);
      Response response = client.target(generateURL("/test")).request().post(null);
      Assert.assertEquals(204, response.getStatus());
      engine.finalize();
      HttpClient httpClient = engine.getHttpClient();
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
      ApacheHttpClient4Engine engine = new ApacheHttpClient4Engine();
      ResteasyClient client = new ResteasyClient();
      client.httpEngine(engine);
      Response response = client.target(generateURL("/test")).request().post(null);
      Assert.assertEquals(204, response.getStatus());
      engine.close();
      HttpClient httpClient = engine.getHttpClient();
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
      ApacheHttpClient4Engine engine = new ApacheHttpClient4Engine(httpClient);
      ResteasyClient client = new ResteasyClient();
      client.httpEngine(engine);
      Response response = client.target(generateURL("/test")).request().post(null);
      Assert.assertEquals(204, response.getStatus());
      engine.finalize();
      Assert.assertEquals(httpClient, engine.getHttpClient());
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
      ApacheHttpClient4Engine engine = new ApacheHttpClient4Engine(httpClient);
      ResteasyClient client = new ResteasyClient();
      client.httpEngine(engine);
      Response response = client.target(generateURL("/test")).request().post(null);
      Assert.assertEquals(204, response.getStatus());
      engine.close();
      Assert.assertEquals(httpClient, engine.getHttpClient());
      HttpPost post = new HttpPost(generateURL("/test"));
      HttpResponse httpResponse = httpClient.execute(post);
      Assert.assertEquals(204, httpResponse.getStatusLine().getStatusCode());
      httpClient.getConnectionManager().shutdown();
   }
}
