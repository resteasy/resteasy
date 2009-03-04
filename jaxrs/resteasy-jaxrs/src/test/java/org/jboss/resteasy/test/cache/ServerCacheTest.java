package org.jboss.resteasy.test.cache;

import org.jboss.resteasy.annotations.cache.Cache;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.plugins.interceptors.cache.ServerCacheHitInterceptor;
import org.jboss.resteasy.plugins.interceptors.cache.ServerCacheInterceptor;
import org.jboss.resteasy.plugins.interceptors.cache.SimpleServerCache;
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
      SimpleServerCache cache = new SimpleServerCache();
      ServerCacheHitInterceptor hit = new ServerCacheHitInterceptor(cache);
      ServerCacheInterceptor interceptor = new ServerCacheInterceptor(cache);

      getProviderFactory().getServerPreProcessInterceptorRegistry().register(hit);
      getProviderFactory().getServerMessageBodyWriterInterceptorRegistry().register(interceptor);

      addPerRequestResource(MyService.class);
   }


   @Test
   public void testCache() throws Exception
   {
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