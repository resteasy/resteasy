package org.jboss.resteasy.test.client;

import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.jboss.resteasy.client.ClientResponseFailure;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.core.executors.ApacheHttpClientExecutor;
import org.jboss.resteasy.spi.NoLogWebApplicationException;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * Test connection cleanup
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ClientResponseFailureTest extends BaseResourceTest
{

   public static class MyResourceImpl implements MyResource
   {
      public String get()
      {
         return "hello world";
      }

      public String error()
      {
         Response r = Response.status(404).type("text/plain").entity("there was an error").build();
         throw new NoLogWebApplicationException(r);
      }
   }

   @Path("/test")
   public static interface MyResource
   {
      @GET
      @Produces("text/plain")
      public String get();

      @GET
      @Path("error")
      @Produces("text/plain")
      String error();
   }

   @BeforeClass
   public static void setUp() throws Exception
   {
      addPerRequestResource(MyResourceImpl.class);
   }


   @Test
   public void testStreamStillOpen() throws Exception
   {
      final MyResource proxy = ProxyFactory.create(MyResource.class, "http://localhost:8081");
      boolean failed = true;
      try
      {
         String str = proxy.error();
         failed = false;
      }
      catch (ClientResponseFailure e)
      {
         Assert.assertEquals(e.getResponse().getStatus(), 404);
         Assert.assertEquals(e.getResponse().getEntity(String.class), "there was an error");
         e.getResponse().releaseConnection();
      }

      Assert.assertTrue(failed);
   }

   @Test
   public void test31ConnectionCleanupOnError() throws Exception
   {
      HttpClientParams params = new HttpClientParams();
      params.setSoTimeout(5000);
      params.setConnectionManagerTimeout(5000);


      MultiThreadedHttpConnectionManager cm = new
              MultiThreadedHttpConnectionManager();
      cm.setMaxConnectionsPerHost(10);
      cm.setMaxTotalConnections(100);

      org.apache.commons.httpclient.HttpClient client = new org.apache.commons.httpclient.HttpClient();
      client.setParams(params);
      client.setHttpConnectionManager(cm);

      final MyResource proxy = ProxyFactory.create(
              MyResource.class, "http://localhost:8081", new
                      ApacheHttpClientExecutor(client));

      Background[] threads = new Background[31];
      for (int i = 0; i < 31; i++)
      {
         threads[i] = new Background(proxy);
      }
      for (int i = 0; i < 31; i++)
      {
         threads[i].start();
      }
      for (int i = 0; i < 31; i++)
      {
         threads[i].join();
      }

      for (int i = 0; i < 31; i++)
      {
         Assert.assertTrue("Deveria finalizar", threads[i].finished);
      }
   }

   public static class Background extends Thread
   {

      private MyResource proxy;

      boolean finished = false;

      public Background(MyResource proxy)
      {
         this.proxy = proxy;
      }

      public void run()
      {
         boolean failed = true;
         try
         {
            String str = proxy.error();
            failed = false;
         }
         catch (ClientResponseFailure e)
         {
            Assert.assertEquals(e.getResponse()
                    .getStatus(), 404);
            String str = (String) e.getResponse().getEntity(String.class);
            Assert.assertEquals("there was an error", str);

//                Assert.assertEquals( e.getResponse()
//                    .getEntity( String.class ), "there was an error" );
            //e.getResponse().releaseConnection();
            //e.printStackTrace();
         }

         Assert.assertTrue(failed);
         finished = true;
      }
   }


}