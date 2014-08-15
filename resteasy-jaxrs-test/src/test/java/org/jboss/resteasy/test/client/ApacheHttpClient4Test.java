package org.jboss.resteasy.test.client;

import static org.apache.http.params.CoreConnectionPNames.*;

import java.util.concurrent.atomic.AtomicLong;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ClientResponseFailure;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;
import org.jboss.resteasy.spi.NoLogWebApplicationException;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test connection cleanup
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ApacheHttpClient4Test extends BaseResourceTest
{

   public static class MyResourceImpl implements MyResource
   {
      @Override
      public String get()
      {
         return "hello world";
      }

      @Override
      public String error()
      {
         throw new NoLogWebApplicationException(404);
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

   @Override
   @Before
   public void before() throws Exception
   {
      addPerRequestResource(MyResourceImpl.class);
   }

   private final AtomicLong counter = new AtomicLong();

   @Test
   public void testConnectionCleanupGC() throws Exception
   {
      final ApacheHttpClient4Executor executor = createClient();
      counter.set(0);


      Thread[] threads = new Thread[3];

      for (int i = 0; i < 3; i++)
      {
         threads[i] = new Thread()
         {
            @Override
            public void run()
            {
               for (int j = 0; j < 10; j++)
               {
                  runit(executor, false);
                  System.gc();
               }
            }
         };
      }

      for (int i = 0; i < 3; i++) threads[i].start();
      for (int i = 0; i < 3; i++) threads[i].join();

      Assert.assertEquals(30l, counter.get());
   }

   @Test
   public void testConnectionCleanupManual() throws Exception
   {
      final ApacheHttpClient4Executor executor = createClient();
      counter.set(0);


      Thread[] threads = new Thread[3];

      for (int i = 0; i < 3; i++)
      {
         threads[i] = new Thread()
         {
            @Override
            public void run()
            {
               for (int j = 0; j < 10; j++)
               {
                  runit(executor, true);
               }
            }
         };
      }

      for (int i = 0; i < 3; i++) threads[i].start();
      for (int i = 0; i < 3; i++) threads[i].join();

      Assert.assertEquals(30l, counter.get());
   }

   @Test
   public void testConnectionCleanupProxy() throws Exception
   {
      final ApacheHttpClient4Executor executor = createClient();
      final MyResource proxy = ProxyFactory.create(MyResource.class, TestPortProvider.generateBaseUrl(), executor);
      counter.set(0);


      Thread[] threads = new Thread[3];


      for (int i = 0; i < 3; i++)
      {
         threads[i] = new Thread()
         {
            @Override
            public void run()
            {
               for (int j = 0; j < 10; j++)
               {
                  System.out.println("calling proxy");
                  String str = proxy.get();
                  System.out.println("returned: " + str);
                  Assert.assertEquals("hello world", str);
                  counter.incrementAndGet();
               }
            }
         };
      }

      for (int i = 0; i < 3; i++) threads[i].start();
      for (int i = 0; i < 3; i++) threads[i].join();

      Assert.assertEquals(30l, counter.get());
   }

   @Test
   public void testConnectionCleanupErrorGC() throws Exception
   {
      final ApacheHttpClient4Executor executor = createClient();
      final MyResource proxy = ProxyFactory.create(MyResource.class, TestPortProvider.generateBaseUrl(), executor);
      counter.set(0);


      Thread[] threads = new Thread[3];


      for (int i = 0; i < 3; i++)
      {
         threads[i] = new Thread()
         {
            @Override
            public void run()
            {
               for (int j = 0; j < 10; j++)
               {
                  System.out.println("calling proxy");
                  callProxy(proxy);
                  System.gc();
                  System.out.println("returned");
               }
            }
         };
      }

      for (int i = 0; i < 3; i++) threads[i].start();
      for (int i = 0; i < 3; i++) threads[i].join();

      Assert.assertEquals(30l, counter.get());
   }

   @Test
   public void testConnectionCleanupErrorNoGC() throws Exception
   {
      final ApacheHttpClient4Executor executor = createClient();
      final MyResource proxy = ProxyFactory.create(MyResource.class, TestPortProvider.generateBaseUrl(), executor);
      counter.set(0);


      Thread[] threads = new Thread[3];


      for (int i = 0; i < 3; i++)
      {
         threads[i] = new Thread()
         {
            @Override
            public void run()
            {
               for (int j = 0; j < 10; j++)
               {
                  System.out.println("calling proxy");
                  try
                  {
                     proxy.error();
                  }
                  catch (ClientResponseFailure e)
                  {
                     Assert.assertEquals(e.getResponse().getStatus(), 404);
                     e.getResponse().releaseConnection();
                     counter.incrementAndGet();
                  }
                  System.out.println("returned");
               }
            }
         };
      }

      for (int i = 0; i < 3; i++) threads[i].start();
      for (int i = 0; i < 3; i++) threads[i].join();

      Assert.assertEquals(30l, counter.get());
   }

   private void callProxy(MyResource proxy)
   {
      try
      {
         proxy.error();
      }
      catch (ClientResponseFailure e)
      {
         Assert.assertEquals(e.getResponse().getStatus(), 404);
         counter.incrementAndGet();
      }
   }


   private ApacheHttpClient4Executor createClient()
   {
      HttpParams params = new BasicHttpParams();
      params.setLongParameter(CONNECTION_TIMEOUT, 1000);
      HttpConnectionParams.setConnectionTimeout(params, 1000);


      // Create and initialize scheme registry
      SchemeRegistry schemeRegistry = new SchemeRegistry();
      schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));

      // Create an HttpClient with the ThreadSafeClientConnManager.
      // This connection manager must be used if more than one thread will
      // be using the HttpClient.
      ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(schemeRegistry);
      cm.setMaxTotal(3);
      HttpClient httpClient = new DefaultHttpClient(cm, params);

      final ApacheHttpClient4Executor executor = new ApacheHttpClient4Executor(httpClient);
      return executor;
   }

   private void runit(ApacheHttpClient4Executor executor, boolean release)
   {
      ClientRequest request = executor.createRequest(TestPortProvider.generateURL("/test"));
      ClientResponse<?> response = null;
      try
      {
         System.out.println("get");
         response = request.get();
         Assert.assertEquals(200, response.getStatus());
         //Assert.assertEquals("hello world", response.getEntity(String.class));
         System.out.println("ok");
         if (release) response.releaseConnection();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
      counter.incrementAndGet();
   }
}
