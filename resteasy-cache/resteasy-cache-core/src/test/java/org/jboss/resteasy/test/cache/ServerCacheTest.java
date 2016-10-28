package org.jboss.resteasy.test.cache;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.cache.Cache;
import org.jboss.resteasy.plugins.cache.server.ServerCacheFeature;
import org.jboss.resteasy.plugins.server.netty.NettyJaxrsServer;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ServerCacheTest
{
   private static NettyJaxrsServer server;
   private static ResteasyDeployment deployment;
   private static int count = 0;
   private static int plainCount = 0;
   private static int htmlCount = 0;
   private static Client client;


   @BeforeClass
   public static void beforeClass() throws Exception
   {
      server = new NettyJaxrsServer();
      server.setPort(TestPortProvider.getPort());
      server.setRootResourcePath("/");
      server.start();
      deployment = server.getDeployment();
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void afterClass() throws Exception
   {
      server.stop();
      server = null;
      deployment = null;
      client.close();
   }

   public Registry getRegistry()
   {
      return deployment.getRegistry();
   }

   public ResteasyProviderFactory getProviderFactory()
   {
      return deployment.getProviderFactory();
   }

   /**
    * @param resource
    */
   public static void addPerRequestResource(Class<?> resource)
   {
      deployment.getRegistry().addPerRequestResource(resource);
   }

   public String readString(InputStream in) throws IOException
   {
      char[] buffer = new char[1024];
      StringBuilder builder = new StringBuilder();
      BufferedReader reader = new BufferedReader(new InputStreamReader(in));
      int wasRead = 0;
      do
      {
         wasRead = reader.read(buffer, 0, 1024);
         if (wasRead > 0)
         {
            builder.append(buffer, 0, wasRead);
         }
      }
      while (wasRead > -1);

      return builder.toString();
   }
   
   @Path("/cache")
   public static class MyService
   {
      @GET
      @Produces("text/plain")
      @Cache(maxAge = 2)
      public String get()
      {
         count++;
         return "hello world" + count;
      }

      @PUT
      @Consumes("text/plain")
      public void put(String val)
      {
      }

      @GET
      @Produces("text/plain")
      @Path("accepts")
      @Cache(maxAge = 2)
      public String getPlain()
      {
         plainCount++;
         return "plain" + plainCount;
      }

      @GET
      @Produces("text/html")
      @Path("accepts")
      @Cache(maxAge = 2)
      public String getHtml()
      {
         htmlCount++;
         return "html" + htmlCount;
      }

      @GET
      @Produces("text/plain")
      @Path("stuff")
      @Cache(maxAge = 2)
      public String getStuff()
      {
         count++;
         return "stuff";
      }

      @GET
      @Produces("text/plain")
      @Path("vary")
      @Cache(maxAge = 2)
      public Response getVary(@HeaderParam("X-Test-Vary") @DefaultValue("default") String testVary)
      {
         count++;
         return Response.ok(testVary).header(HttpHeaders.VARY, "X-Test-Vary").header("X-Count", count).build();
      }
   }

   @Path("/cache")
   public static interface MyProxy
   {
      @GET
      @Produces("text/plain")
      public String get();

   }

   @Before
   public void setUp() throws Exception
   {
      getProviderFactory().register(ServerCacheFeature.class);
      addPerRequestResource(MyService.class);
   }

   @Test
   public void testNoCacheHitValidation() throws Exception
   {
      // test that after a cache expiration NOT MODIFIED is still returned if matching etags

      count = 0;
      String etag = null;
      {
         Builder request = client.target(generateURL("/cache/stuff")).request();
         Response response = request.get();
         Assert.assertEquals(200, response.getStatus());
         String cc = response.getHeaderString(HttpHeaders.CACHE_CONTROL);
         Assert.assertNotNull(cc);
         etag = response.getHeaderString(HttpHeaders.ETAG);
         Assert.assertNotNull(etag);
         Assert.assertEquals(response.readEntity(String.class), "stuff");
      }


      Thread.sleep(2000);

      {
         Builder request = client.target(generateURL("/cache/stuff")).request();
         request.header(HttpHeaders.IF_NONE_MATCH, etag);
         Response response = request.get();
         Assert.assertEquals(Response.Status.NOT_MODIFIED.getStatusCode(), response.getStatus());
         Assert.assertEquals(2, count);
         response.close();
      }
   }


   @Test
   public void testCache() throws Exception
   {
      count = 0;
      String etag = null;
      {
         Builder request = client.target(generateURL("/cache")).request();
         Response response = request.get();
         Assert.assertEquals(200, response.getStatus());
         String cc = response.getHeaderString(HttpHeaders.CACHE_CONTROL);
         Assert.assertNotNull(cc);
         etag = response.getHeaderString(HttpHeaders.ETAG);
         Assert.assertNotNull(etag);
         Assert.assertEquals(response.readEntity(String.class), "hello world" + 1);
      }


      {
         Builder request = client.target(generateURL("/cache")).request();
         Response response = request.get();
         Assert.assertEquals(200, response.getStatus());
         String cc = response.getHeaderString(HttpHeaders.CACHE_CONTROL);
         Assert.assertNotNull(cc);
         etag = response.getHeaderString(HttpHeaders.ETAG);
         Assert.assertNotNull(etag);
         Assert.assertEquals(response.readEntity(String.class), "hello world" + 1);
      }
      // test if-not-match
      {
         Builder request = client.target(generateURL("/cache")).request();
         request.header(HttpHeaders.IF_NONE_MATCH, etag);
         Response response = request.get();
         Assert.assertEquals(Response.Status.NOT_MODIFIED.getStatusCode(), response.getStatus());
         response.close();
      }


      Thread.sleep(2000);

      {
         Builder request = client.target(generateURL("/cache")).request();
         Response response = request.get();
         Assert.assertEquals(200, response.getStatus());
         String cc = response.getHeaderString(HttpHeaders.CACHE_CONTROL);
         Assert.assertNotNull(cc);
         etag = response.getHeaderString(HttpHeaders.ETAG);
         Assert.assertNotNull(etag);
         Assert.assertEquals(response.readEntity(String.class), "hello world" + 2);
      }

      {
         Builder request = client.target(generateURL("/cache")).request();
         Response response = request.get();
         Assert.assertEquals(200, response.getStatus());
         String cc = response.getHeaderString(HttpHeaders.CACHE_CONTROL);
         Assert.assertNotNull(cc);
         etag = response.getHeaderString(HttpHeaders.ETAG);
         Assert.assertNotNull(etag);
         Assert.assertEquals(response.readEntity(String.class), "hello world" + 2);
      }

      {
         Builder request = client.target(generateURL("/cache")).request();
         Response response = request.put(Entity.entity("yo", "text/plain"));
         Assert.assertEquals(204, response.getStatus());
         response.close();
      }
      {
         Builder request = client.target(generateURL("/cache")).request();
         Response response = request.get();
         Assert.assertEquals(200, response.getStatus());
         String cc = response.getHeaderString(HttpHeaders.CACHE_CONTROL);
         Assert.assertNotNull(cc);
         etag = response.getHeaderString(HttpHeaders.ETAG);
         Assert.assertNotNull(etag);
         Assert.assertEquals(response.readEntity(String.class), "hello world" + 3);
      }
   }


   @Test
   public void testAccepts() throws Exception
   {
      count = 0;
      plainCount = 0;
      htmlCount = 0;
      String etag = null;
      {
         Builder request = client.target(generateURL("/cache/accepts")).request();
         Response response = request.accept("text/plain").get();
         Assert.assertEquals(200, response.getStatus());
         String cc = response.getHeaderString(HttpHeaders.CACHE_CONTROL);
         Assert.assertNotNull(cc);
         etag = response.getHeaderString(HttpHeaders.ETAG);
         Assert.assertNotNull(etag);
         Assert.assertEquals(response.readEntity(String.class), "plain" + 1);
      }

      {
         Builder request = client.target(generateURL("/cache/accepts")).request();
         Response response = request.accept("text/plain").get();
         Assert.assertEquals(200, response.getStatus());
         String cc = response.getHeaderString(HttpHeaders.CACHE_CONTROL);
         Assert.assertNotNull(cc);
         etag = response.getHeaderString(HttpHeaders.ETAG);
         Assert.assertNotNull(etag);
         Assert.assertEquals(response.readEntity(String.class), "plain" + 1);
      }

      {
         Builder request = client.target(generateURL("/cache/accepts")).request();
         Response response = request.accept("text/html").get();
         Assert.assertEquals(200, response.getStatus());
         String cc = response.getHeaderString(HttpHeaders.CACHE_CONTROL);
         Assert.assertNotNull(cc);
         etag = response.getHeaderString(HttpHeaders.ETAG);
         Assert.assertNotNull(etag);
         Assert.assertEquals(response.readEntity(String.class), "html" + 1);
      }
      {
         Builder request = client.target(generateURL("/cache/accepts")).request();
         Response response = request.accept("text/html").get();
         Assert.assertEquals(200, response.getStatus());
         String cc = response.getHeaderString(HttpHeaders.CACHE_CONTROL);
         Assert.assertNotNull(cc);
         etag = response.getHeaderString(HttpHeaders.ETAG);
         Assert.assertNotNull(etag);
         Assert.assertEquals(response.readEntity(String.class), "html" + 1);
      }
   }

   @Test
   public void testPreferredAccepts() throws Exception
   {
      count = 0;
      plainCount = 0;
      htmlCount = 0;
      String etag = null;
      {
         Builder request = client.target(generateURL("/cache/accepts")).request();
         Response response = request.accept("text/plain").get();
         Assert.assertEquals(200, response.getStatus());
         String cc = response.getHeaderString(HttpHeaders.CACHE_CONTROL);
         Assert.assertNotNull(cc);
         etag = response.getHeaderString(HttpHeaders.ETAG);
         Assert.assertNotNull(etag);
         Assert.assertEquals(response.readEntity(String.class), "plain" + 1);
      }

      {
         Builder request = client.target(generateURL("/cache/accepts")).request();
         Response response = request.accept("text/html").get();
         Assert.assertEquals(200, response.getStatus());
         String cc = response.getHeaderString(HttpHeaders.CACHE_CONTROL);
         Assert.assertNotNull(cc);
         etag = response.getHeaderString(HttpHeaders.ETAG);
         Assert.assertNotNull(etag);
         Assert.assertEquals(response.readEntity(String.class), "html" + 1);
      }

      {
         Builder request = client.target(generateURL("/cache/accepts")).request();
         request.header(HttpHeaders.ACCEPT, "text/html;q=0.5, text/plain");
         Response response = request.get();
         Assert.assertEquals(200, response.getStatus());
         String cc = response.getHeaderString(HttpHeaders.CACHE_CONTROL);
         Assert.assertNotNull(cc);
         etag = response.getHeaderString(HttpHeaders.ETAG);
         Assert.assertNotNull(etag);
         Assert.assertEquals(response.readEntity(String.class), "plain" + 1);
      }
      {
         Builder request = client.target(generateURL("/cache/accepts")).request();
         request.header(HttpHeaders.ACCEPT, "text/plain;q=0.5, text/html");
         Response response = request.get();
         Assert.assertEquals(200, response.getStatus());
         String cc = response.getHeaderString(HttpHeaders.CACHE_CONTROL);
         Assert.assertNotNull(cc);
         etag = response.getHeaderString(HttpHeaders.ETAG);
         Assert.assertNotNull(etag);
         Assert.assertEquals(response.readEntity(String.class), "html" + 1);
      }
   }

   @Test
   public void testPreferredButNotCachedAccepts() throws Exception
   {
      count = 0;
      plainCount = 0;
      htmlCount = 0;
      String etag = null;
      {
         Builder request = client.target(generateURL("/cache/accepts")).request();
         Response response = request.accept("text/plain").get();
         Assert.assertEquals(200, response.getStatus());
         String cc = response.getHeaderString(HttpHeaders.CACHE_CONTROL);
         Assert.assertNotNull(cc);
         etag = response.getHeaderString(HttpHeaders.ETAG);
         Assert.assertNotNull(etag);
         Assert.assertEquals(response.readEntity(String.class), "plain" + 1);
      }

      // we test that the preferred can be handled
      {
         Builder request = client.target(generateURL("/cache/accepts")).request();
         request.header(HttpHeaders.ACCEPT, "text/plain;q=0.5, text/html");
         Response response = request.get();
         Assert.assertEquals(200, response.getStatus());
         String cc = response.getHeaderString(HttpHeaders.CACHE_CONTROL);
         Assert.assertNotNull(cc);
         etag = response.getHeaderString(HttpHeaders.ETAG);
         Assert.assertNotNull(etag);
         Assert.assertEquals(response.readEntity(String.class), "html" + 1);
      }
   }

   @Test
   public void testVary() throws Exception {
       int cachedCount;
       {
           Builder request = client.target(generateURL("/cache/vary")).request();
           Response foo = request.accept("text/plain").header("X-Test-Vary", "foo").get();
           Assert.assertEquals("foo", foo.readEntity(String.class));
           cachedCount = Integer.parseInt(foo.getHeaderString("X-Count"));
       }
       {
           Builder request = client.target(generateURL("/cache/vary")).request();
           Response bar = request.accept("text/plain").header("X-Test-Vary", "bar").get();
           Assert.assertEquals("bar", bar.readEntity(String.class));
       }
       {
           Builder request = client.target(generateURL("/cache/vary")).request();
           Response foo = request.accept("text/plain").header("X-Test-Vary", "foo").get();
           Assert.assertEquals("foo", foo.readEntity(String.class));
           int currentCount = Integer.parseInt(foo.getHeaderString("X-Count"));
           Assert.assertEquals(cachedCount, currentCount);
       }
   }

   @Test
   public void testProxy() throws Exception
   {

      /*
      MyProxy proxy = ProxyFactory.create(MyProxy.class, generateBaseUrl());
      CacheFactory.makeCacheable(proxy);
      String rtn = null;
      rtn = proxy.get();
      Assert.assertEquals("hello world" + 1, rtn);
      Assert.assertEquals(1, count);
      rtn = proxy.get();
      Assert.assertEquals("hello world" + 1, rtn);
      Assert.assertEquals(1, count);
      Thread.sleep(2000);
      rtn = proxy.get();
      Assert.assertEquals("hello world" + 2, rtn);
      Assert.assertEquals(2, count);
      rtn = proxy.get();
      Assert.assertEquals("hello world" + 2, rtn);
      Assert.assertEquals(2, count);
      */

   }

}