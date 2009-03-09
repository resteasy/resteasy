package org.jboss.resteasy.test.cache;

import org.jboss.resteasy.annotations.cache.Cache;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.plugins.cache.server.JBossCache;
import org.jboss.resteasy.test.BaseResourceTest;
import static org.jboss.resteasy.test.TestPortProvider.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ServerCacheTest extends BaseResourceTest
{
   private static int count = 0;
   private static int plainCount = 0;
   private static int htmlCount = 0;

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
      JBossCache cache = new JBossCache();
      cache.setProviderFactory(getProviderFactory());
      cache.start();
      addPerRequestResource(MyService.class);
   }

   @Test
   public void testNoCacheHitValidation() throws Exception
   {
      // test that after a cache expiration NOT MODIFIED is still returned if matching etags

      count = 0;
      String etag = null;
      {
         ClientRequest request = new ClientRequest(generateURL("/cache/stuff"));
         ClientResponse<String> response = request.get(String.class);
         Assert.assertEquals(200, response.getStatus());
         String cc = response.getHeaders().getFirst(HttpHeaders.CACHE_CONTROL);
         Assert.assertNotNull(cc);
         etag = response.getHeaders().getFirst(HttpHeaders.ETAG);
         Assert.assertNotNull(etag);
         Assert.assertEquals(response.getEntity(), "stuff");
      }


      Thread.sleep(2000);

      {
         ClientRequest request = new ClientRequest(generateURL("/cache/stuff"));
         request.header(HttpHeaders.IF_NONE_MATCH, etag);
         ClientResponse<String> response = request.get(String.class);
         Assert.assertEquals(Response.Status.NOT_MODIFIED.getStatusCode(), response.getStatus());
         Assert.assertEquals(2, count);
      }
   }


   @Test
   public void testCache() throws Exception
   {
      count = 0;
      String etag = null;
      {
         ClientRequest request = new ClientRequest(generateURL("/cache"));
         ClientResponse<String> response = request.get(String.class);
         Assert.assertEquals(200, response.getStatus());
         String cc = response.getHeaders().getFirst(HttpHeaders.CACHE_CONTROL);
         Assert.assertNotNull(cc);
         etag = response.getHeaders().getFirst(HttpHeaders.ETAG);
         Assert.assertNotNull(etag);
         Assert.assertEquals(response.getEntity(), "hello world" + 1);
      }


      {
         ClientRequest request = new ClientRequest(generateURL("/cache"));
         ClientResponse<String> response = request.get(String.class);
         Assert.assertEquals(200, response.getStatus());
         String cc = response.getHeaders().getFirst(HttpHeaders.CACHE_CONTROL);
         Assert.assertNotNull(cc);
         etag = response.getHeaders().getFirst(HttpHeaders.ETAG);
         Assert.assertNotNull(etag);
         Assert.assertEquals(response.getEntity(), "hello world" + 1);
      }
      // test if-not-match
      {
         ClientRequest request = new ClientRequest(generateURL("/cache"));
         request.header(HttpHeaders.IF_NONE_MATCH, etag);
         ClientResponse<String> response = request.get(String.class);
         Assert.assertEquals(Response.Status.NOT_MODIFIED.getStatusCode(), response.getStatus());
      }


      Thread.sleep(2000);

      {
         ClientRequest request = new ClientRequest(generateURL("/cache"));
         ClientResponse<String> response = request.get(String.class);
         Assert.assertEquals(200, response.getStatus());
         String cc = response.getHeaders().getFirst(HttpHeaders.CACHE_CONTROL);
         Assert.assertNotNull(cc);
         etag = response.getHeaders().getFirst(HttpHeaders.ETAG);
         Assert.assertNotNull(etag);
         Assert.assertEquals(response.getEntity(), "hello world" + 2);
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
         ClientRequest request = new ClientRequest(generateURL("/cache/accepts"));
         request.accept("text/plain");
         ClientResponse<String> response = request.get(String.class);
         Assert.assertEquals(200, response.getStatus());
         String cc = response.getHeaders().getFirst(HttpHeaders.CACHE_CONTROL);
         Assert.assertNotNull(cc);
         etag = response.getHeaders().getFirst(HttpHeaders.ETAG);
         Assert.assertNotNull(etag);
         Assert.assertEquals(response.getEntity(), "plain" + 1);
      }

      {
         ClientRequest request = new ClientRequest(generateURL("/cache/accepts"));
         request.accept("text/plain");
         ClientResponse<String> response = request.get(String.class);
         Assert.assertEquals(200, response.getStatus());
         String cc = response.getHeaders().getFirst(HttpHeaders.CACHE_CONTROL);
         Assert.assertNotNull(cc);
         etag = response.getHeaders().getFirst(HttpHeaders.ETAG);
         Assert.assertNotNull(etag);
         Assert.assertEquals(response.getEntity(), "plain" + 1);
      }

      {
         ClientRequest request = new ClientRequest(generateURL("/cache/accepts"));
         request.accept("text/html");
         ClientResponse<String> response = request.get(String.class);
         Assert.assertEquals(200, response.getStatus());
         String cc = response.getHeaders().getFirst(HttpHeaders.CACHE_CONTROL);
         Assert.assertNotNull(cc);
         etag = response.getHeaders().getFirst(HttpHeaders.ETAG);
         Assert.assertNotNull(etag);
         Assert.assertEquals(response.getEntity(), "html" + 1);
      }
      {
         ClientRequest request = new ClientRequest(generateURL("/cache/accepts"));
         request.accept("text/html");
         ClientResponse<String> response = request.get(String.class);
         Assert.assertEquals(200, response.getStatus());
         String cc = response.getHeaders().getFirst(HttpHeaders.CACHE_CONTROL);
         Assert.assertNotNull(cc);
         etag = response.getHeaders().getFirst(HttpHeaders.ETAG);
         Assert.assertNotNull(etag);
         Assert.assertEquals(response.getEntity(), "html" + 1);
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
         ClientRequest request = new ClientRequest(generateURL("/cache/accepts"));
         request.accept("text/plain");
         ClientResponse<String> response = request.get(String.class);
         Assert.assertEquals(200, response.getStatus());
         String cc = response.getHeaders().getFirst(HttpHeaders.CACHE_CONTROL);
         Assert.assertNotNull(cc);
         etag = response.getHeaders().getFirst(HttpHeaders.ETAG);
         Assert.assertNotNull(etag);
         Assert.assertEquals(response.getEntity(), "plain" + 1);
      }

      {
         ClientRequest request = new ClientRequest(generateURL("/cache/accepts"));
         request.accept("text/html");
         ClientResponse<String> response = request.get(String.class);
         Assert.assertEquals(200, response.getStatus());
         String cc = response.getHeaders().getFirst(HttpHeaders.CACHE_CONTROL);
         Assert.assertNotNull(cc);
         etag = response.getHeaders().getFirst(HttpHeaders.ETAG);
         Assert.assertNotNull(etag);
         Assert.assertEquals(response.getEntity(), "html" + 1);
      }

      {
         ClientRequest request = new ClientRequest(generateURL("/cache/accepts"));
         request.header(HttpHeaders.ACCEPT, "text/html;q=0.5, text/plain");
         ClientResponse<String> response = request.get(String.class);
         Assert.assertEquals(200, response.getStatus());
         String cc = response.getHeaders().getFirst(HttpHeaders.CACHE_CONTROL);
         Assert.assertNotNull(cc);
         etag = response.getHeaders().getFirst(HttpHeaders.ETAG);
         Assert.assertNotNull(etag);
         Assert.assertEquals(response.getEntity(), "plain" + 1);
      }
      {
         ClientRequest request = new ClientRequest(generateURL("/cache/accepts"));
         request.header(HttpHeaders.ACCEPT, "text/plain;q=0.5, text/html");
         ClientResponse<String> response = request.get(String.class);
         Assert.assertEquals(200, response.getStatus());
         String cc = response.getHeaders().getFirst(HttpHeaders.CACHE_CONTROL);
         Assert.assertNotNull(cc);
         etag = response.getHeaders().getFirst(HttpHeaders.ETAG);
         Assert.assertNotNull(etag);
         Assert.assertEquals(response.getEntity(), "html" + 1);
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
         ClientRequest request = new ClientRequest(generateURL("/cache/accepts"));
         request.accept("text/plain");
         ClientResponse<String> response = request.get(String.class);
         Assert.assertEquals(200, response.getStatus());
         String cc = response.getHeaders().getFirst(HttpHeaders.CACHE_CONTROL);
         Assert.assertNotNull(cc);
         etag = response.getHeaders().getFirst(HttpHeaders.ETAG);
         Assert.assertNotNull(etag);
         Assert.assertEquals(response.getEntity(), "plain" + 1);
      }

      // we test that the preferred can be handled
      {
         ClientRequest request = new ClientRequest(generateURL("/cache/accepts"));
         request.header(HttpHeaders.ACCEPT, "text/plain;q=0.5, text/html");
         ClientResponse<String> response = request.get(String.class);
         Assert.assertEquals(200, response.getStatus());
         String cc = response.getHeaders().getFirst(HttpHeaders.CACHE_CONTROL);
         Assert.assertNotNull(cc);
         etag = response.getHeaders().getFirst(HttpHeaders.ETAG);
         Assert.assertNotNull(etag);
         Assert.assertEquals(response.getEntity(), "html" + 1);
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