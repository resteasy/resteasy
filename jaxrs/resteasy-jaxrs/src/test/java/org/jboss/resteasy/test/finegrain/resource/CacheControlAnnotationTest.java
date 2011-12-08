package org.jboss.resteasy.test.finegrain.resource;

import org.jboss.resteasy.annotations.cache.Cache;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.CacheControl;

import static org.jboss.resteasy.test.TestPortProvider.*;
/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class CacheControlAnnotationTest extends BaseResourceTest
{
   @Path("/")
   public static class Resource
   {
      @GET
      @Cache(maxAge = 3600)
      @Path("/maxage")
      public String getMaxAge()
      {
         return "maxage";
      }

      @GET
      @NoCache
      @Path("nocache")
      public String getNoCache()
      {
         return "nocache";
      }

   }

   @Before
   public void setUp() throws Exception
   {
      addPerRequestResource(Resource.class);
   }

   @Test
   public void testResource() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/maxage"));
      try
      {
         ClientResponse<?> response = request.get();
         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
         System.out.println("Cache-Control: " + response.getResponseHeaders().getFirst("cache-control"));
         CacheControl cc = CacheControl.valueOf(response.getResponseHeaders().getFirst("cache-control"));
         Assert.assertFalse(cc.isPrivate());
         Assert.assertEquals(3600, cc.getMaxAge());
         response.releaseConnection();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   @Test
   public void testResource2() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/nocache"));
      try
      {
         ClientResponse<?> response = request.get();
         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
         String value = response.getResponseHeaders().getFirst("cache-control");
         Assert.assertEquals("no-cache", value);
         System.out.println("Cache-Control: " + value);
         CacheControl cc = CacheControl.valueOf(value);
         Assert.assertTrue(cc.isNoCache());
         response.releaseConnection();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

}
