package org.jboss.resteasy.test.finegrain.resource;

import static org.jboss.resteasy.test.TestPortProvider.*;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.CacheControl;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jboss.resteasy.annotations.cache.Cache;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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

      HttpClient client = new HttpClient();
      GetMethod method = createGetMethod("/maxage");
      try
      {
         int status = client.executeMethod(method);
         Assert.assertEquals(status, HttpServletResponse.SC_OK);
         System.out.println("Cache-Control: " + method.getResponseHeader("cache-control").getValue());
         CacheControl cc = CacheControl.valueOf(method.getResponseHeader("cache-control").getValue());
         Assert.assertFalse(cc.isPrivate());
         Assert.assertEquals(3600, cc.getMaxAge());
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
      method.releaseConnection();

   }

   @Test
   public void testResource2() throws Exception
   {

      HttpClient client = new HttpClient();
      GetMethod method = createGetMethod("/nocache");
      try
      {
         int status = client.executeMethod(method);
         Assert.assertEquals(status, HttpServletResponse.SC_OK);
         System.out.println("Cache-Control: " + method.getResponseHeader("cache-control").getValue());
         CacheControl cc = CacheControl.valueOf(method.getResponseHeader("cache-control").getValue());
         Assert.assertTrue(cc.isNoCache());
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
      method.releaseConnection();

   }

}
