package org.jboss.resteasy.test.nextgen.client;

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
import org.jboss.resteasy.client.jaxrs.ClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;
import org.jboss.resteasy.client.jaxrs.engines.URLConnectionEngine;
import org.jboss.resteasy.spi.NoLogWebApplicationException;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.runners.Parameterized.Parameters;

/**
 * Test connection cleanup
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@RunWith(value = Parameterized.class)
public class ApacheHttpClient4Test extends BaseResourceTest
{

    private Class<ClientHttpEngine> clazz;

    public ApacheHttpClient4Test(Class<ClientHttpEngine> clazz) {
        this.clazz = clazz;
    }

    @Parameters
    public static Collection<Object[]> data() {
        Object[][] data = new Object[][] { { ApacheHttpClient4Engine.class }, {URLConnectionEngine.class} };
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

       public String getData(String data) {
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
      final Client client = createEngine();
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
                  runit(client, false);
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
      final Client client = createEngine();
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
                  runit(client, true);
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
      final ResteasyClient client = createEngine();
      final MyResource proxy = client.target("http://localhost:8081").proxy(MyResource.class);
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
                  String str = proxy.get();
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
      final ResteasyClient client = createEngine();
      final MyResource proxy = client.target("http://localhost:8081").proxy(MyResource.class);
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
                  callProxy(proxy);
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
   public void testConnectionCleanupErrorNoGC() throws Exception
   {
      final ResteasyClient client = createEngine();
      final MyResource proxy = client.target("http://localhost:8081").proxy(MyResource.class);
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
                  String str = null;
                  try
                  {
                     str = proxy.error();
                  }
                  catch (NotFoundException e)
                  {
                     Assert.assertEquals(e.getResponse().getStatus(), 404);
                     e.getResponse().close();
                     counter.incrementAndGet();
                  }
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
       final ResteasyClient client = createEngine();
       final MyResource proxy = client.target("http://localhost:8081").proxy(MyResource.class);
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

   private void callProxy(MyResource proxy)
   {
      String str = null;
      try
      {
         str = proxy.error();
      }
      catch (NotFoundException e)
      {
         Assert.assertEquals(e.getResponse().getStatus(), 404);
         counter.incrementAndGet();
      }
   }


   private ResteasyClient createEngine()
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

       final ClientHttpEngine executor;

      if (clazz.isAssignableFrom(ApacheHttpClient4Engine.class)) {
          executor = new ApacheHttpClient4Engine(httpClient);
      } else {
          executor = new URLConnectionEngine();
      }


      ResteasyClient client = new ResteasyClientBuilder().httpEngine(executor).build();
      return client;
   }

   private void runit(Client client, boolean release)
   {
      WebTarget target = client.target("http://localhost:8081/test");
      try
      {
         Response response = target.request().get();
         Assert.assertEquals(200, response.getStatus());
         //Assert.assertEquals("hello world", response.getEntity(String.class));
         if (release) response.close();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
      counter.incrementAndGet();
   }
}
