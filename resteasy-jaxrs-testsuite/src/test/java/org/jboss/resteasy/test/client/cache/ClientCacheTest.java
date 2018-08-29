package org.jboss.resteasy.test.client.cache;

import org.jboss.resteasy.annotations.cache.Cache;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.cache.CacheFactory;
import org.jboss.resteasy.client.cache.LightweightBrowserCache;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import static org.jboss.resteasy.test.TestPortProvider.generateBaseUrl;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ClientCacheTest extends BaseResourceTest
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

      @Path("/etag/always/good")
      @GET
      @Produces("text/plain")
      public Response getEtagged(@Context Request request)
      {
         count++;
         Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag("42"));
         CacheControl cc = new CacheControl();
         cc.setMaxAge(2);
         if (builder != null)
         {
            return builder.cacheControl(cc).build();
         }
         return Response.ok("hello" + count).cacheControl(cc).tag("42").build();
      }

      @Path("/etag/never/good")
      @GET
      @Produces("text/plain")
      public Response getEtaggedNeverGood(@Context Request request)
      {
         count++;
         Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag("42"));
         if (builder != null)
         {
            return Response.serverError().build();
         }
         CacheControl cc = new CacheControl();
         cc.setMaxAge(2);
         return Response.ok("hello" + count).cacheControl(cc).tag("32").build();
      }

      @Path("/etag/always/validate")
      @GET
      @Produces("text/plain")
      public Response getValidateEtagged(@Context Request request)
      {
         count++;
         Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag("42"));
         if (builder != null)
         {
            return builder.build();
         }
         return Response.ok("hello" + count).tag("42").build();
      }

      @Path("/cacheit/{id}")
      @GET
      @Produces("text/plain")
      @Cache(maxAge = 3000)
      public String getCacheit(@PathParam("id") String id)
      {
         count++;
         return "cachecache" + count;
      }

   }

   @Path("/cache")
   public interface MyProxy
   {
      @GET
      @Produces("text/plain")
      @Cache(maxAge = 2)
      String get();

      @Path("/etag/always/good")
      @GET
      @Produces("text/plain")
      String getAlwaysGoodEtag();

      @Path("/etag/never/good")
      @GET
      @Produces("text/plain")
      String getNeverGoodEtag();

      @Path("/etag/always/validate")
      @GET
      @Produces("text/plain")
      String getValidateEtagged();

      @Path("/cacheit/{id}")
      @GET
      @Produces("text/plain")
      @Cache(maxAge = 3000)
      String getCacheit(@PathParam("id") String id);
   }


   @BeforeClass
   public static void setUp() throws Exception
   {
      addPerRequestResource(MyService.class);
   }

   @Before
   public void reset()
   {
      count = 0;
   }


   @Test
   public void testProxy() throws Exception
   {
      MyProxy proxy = ProxyFactory.create(MyProxy.class, generateBaseUrl());
      CacheFactory.makeCacheable(proxy);
      
      count = 0;
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

      // Test always good etag
      count = 0;
      rtn = proxy.getAlwaysGoodEtag();
      Assert.assertEquals("hello1", rtn);
      Assert.assertEquals(1, count);
      rtn = proxy.getAlwaysGoodEtag();
      Assert.assertEquals("hello1", rtn);
      Assert.assertEquals(1, count);
      Thread.sleep(2000);
      rtn = proxy.getAlwaysGoodEtag();
      Assert.assertEquals("hello1", rtn);
      Assert.assertEquals(2, count);
      rtn = proxy.getAlwaysGoodEtag();
      Assert.assertEquals("hello1", rtn);
      Assert.assertEquals(2, count);

      // Test never good etag
      count = 0;
      rtn = proxy.getNeverGoodEtag();
      Assert.assertEquals("hello1", rtn);
      Assert.assertEquals(1, count);
      rtn = proxy.getNeverGoodEtag();
      Assert.assertEquals("hello1", rtn);
      Assert.assertEquals(1, count);
      Thread.sleep(2000);
      rtn = proxy.getNeverGoodEtag();
      Assert.assertEquals("hello2", rtn);
      Assert.assertEquals(2, count);
      rtn = proxy.getNeverGoodEtag();
      Assert.assertEquals("hello2", rtn);
      Assert.assertEquals(2, count);


      // Test always validate etag
      count = 0;
      rtn = proxy.getValidateEtagged();
      Assert.assertEquals("hello1", rtn);
      Assert.assertEquals(1, count);
      rtn = proxy.getValidateEtagged();
      Assert.assertEquals("hello1", rtn);
      Assert.assertEquals(2, count);
      rtn = proxy.getValidateEtagged();
      Assert.assertEquals("hello1", rtn);
      Assert.assertEquals(3, count);
      rtn = proxy.getValidateEtagged();
      Assert.assertEquals("hello1", rtn);
      Assert.assertEquals(4, count);
   }

   @Test
   public void testMaxSize() throws Exception
   {
      MyProxy proxy = ProxyFactory.create(MyProxy.class, generateBaseUrl());
      LightweightBrowserCache cache = CacheFactory.makeCacheable(proxy);
      cache.setMaxBytes(20);

      count = 0;

      String rtn = proxy.getCacheit("1");
      Assert.assertEquals("cachecache" + 1, rtn);
      Assert.assertEquals(1, count);

      rtn = proxy.getCacheit("1");
      Assert.assertEquals("cachecache" + 1, rtn);
      Assert.assertEquals(1, count);

      rtn = proxy.getCacheit("2");
      Assert.assertEquals("cachecache" + 2, rtn);
      Assert.assertEquals(2, count);

      rtn = proxy.getCacheit("2");
      Assert.assertEquals("cachecache" + 2, rtn);
      Assert.assertEquals(2, count);

      rtn = proxy.getCacheit("1");
      Assert.assertEquals("cachecache" + 3, rtn);
      Assert.assertEquals(3, count);


   }

}
