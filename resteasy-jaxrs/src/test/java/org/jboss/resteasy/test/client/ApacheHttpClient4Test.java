package org.jboss.resteasy.test.client;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.jboss.resteasy.client.*;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;
import org.jboss.resteasy.client.core.executors.URLConnectionClientExecutor;
import org.jboss.resteasy.spi.NoLogWebApplicationException;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import javax.ws.rs.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Test connection cleanup
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@RunWith(value = Parameterized.class)
public class ApacheHttpClient4Test extends BaseResourceTest
{

    private Class<ClientExecutor> clazz;

    public ApacheHttpClient4Test(Class<ClientExecutor> clazz) {
        this.clazz = clazz;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        Object[][] data = new Object[][] { { ApacheHttpClient4Executor.class }, {URLConnectionClientExecutor.class} };
        return Arrays.asList(data);
    }

   public static class MyResourceImpl implements MyResource
   {
      public String get()
      {
         return "hello world";
      }

      public String error()
      {
         throw new NoLogWebApplicationException(404);
      }

      @Override
      public String getData(String data)
      {
         return "Here is your string:"+data;
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

      @POST
      @Path("data")
      @Produces("text/plain")
      @Consumes("text/plain")
      public String getData(String data);
   }

   @BeforeClass
   public static void setUp() throws Exception
   {
      addPerRequestResource(MyResourceImpl.class);
   }

   private AtomicLong counter = new AtomicLong();

   @Test
   public void testConnectionCleanupGC() throws Exception
   {
      final ClientExecutor executor = createClient();
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
      final ClientExecutor executor = createClient();
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
    public void testConnectionWithRequestBody() throws InterruptedException {
        final ClientExecutor executor = createClient();
        final MyResource proxy = ProxyFactory.create(MyResource.class, "http://localhost:8081", executor);
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
                        String res = proxy.getData(String.valueOf(j));
                        Assert.assertNotNull(res);
                        counter.incrementAndGet();
                        System.out.println("returned:"+res);
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
      final ClientExecutor executor = createClient();
      final MyResource proxy = ProxyFactory.create(MyResource.class, "http://localhost:8081", executor);
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
      final ClientExecutor executor = createClient();
      final MyResource proxy = ProxyFactory.create(MyResource.class, "http://localhost:8081", executor);
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
      final ClientExecutor executor = createClient();
      final MyResource proxy = ProxyFactory.create(MyResource.class, "http://localhost:8081", executor);
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
                  String str = null;
                  try
                  {
                     str = proxy.error();
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
      String str = null;
      try
      {
         str = proxy.error();
      }
      catch (ClientResponseFailure e)
      {
         Assert.assertEquals(e.getResponse().getStatus(), 404);
         counter.incrementAndGet();
      }
   }


   private ClientExecutor createClient()
   {
      HttpParams params = new BasicHttpParams();
      ConnManagerParams.setMaxTotalConnections(params, 3);
      ConnManagerParams.setTimeout(params, 1000);

      // Create and initialize scheme registry
      SchemeRegistry schemeRegistry = new SchemeRegistry();
      schemeRegistry.register(
              new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));

      // Create an HttpClient with the ThreadSafeClientConnManager.
      // This connection manager must be used if more than one thread will
      // be using the HttpClient.
      ClientConnectionManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);
      HttpClient httpClient = new DefaultHttpClient(cm, params);

      final ClientExecutor executor;

      if (clazz.isAssignableFrom(ApacheHttpClient4Executor.class)) {
          executor = new ApacheHttpClient4Executor(httpClient);
      } else {
          executor = new URLConnectionClientExecutor();
      }

      return executor;
   }

   private void runit(ClientExecutor executor, boolean release)
   {
      ClientRequest request = executor.createRequest("http://localhost:8081/test");
      ClientResponse response = null;
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
