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
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;
import org.jboss.resteasy.spi.NoLogWebApplicationException;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.concurrent.atomic.AtomicLong;

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
      public String get()
      {
         return "hello world";
      }

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
                  System.out.println("calling proxy");
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

      final ApacheHttpClient4Engine executor = new ApacheHttpClient4Engine(httpClient);
      ResteasyClient client = new ResteasyClientBuilder().httpEngine(executor).build();
      return client;
   }

   private void runit(Client client, boolean release)
   {
      WebTarget target = client.target("http://localhost:8081/test");
      try
      {
         System.out.println("get");
         Response response = target.request().get();
         Assert.assertEquals(200, response.getStatus());
         //Assert.assertEquals("hello world", response.getEntity(String.class));
         System.out.println("ok");
         if (release) response.close();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
      counter.incrementAndGet();
   }
}
