package org.resteasy.test.finegrain.resource;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.resteasy.plugins.client.httpclient.ProxyFactory;
import org.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.resteasy.test.EmbeddedServletContainer;
import org.resteasy.util.HttpResponseCodes;

import javax.ws.rs.CookieParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpContext;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class CookieTest
{
   private static HttpServletDispatcher dispatcher;

   public static class CookieResource
   {
      @Path("/set")
      @GET
      public Response set()
      {
         return Response.ok("content").cookie(new NewCookie("meaning", "42")).build();
      }

      @Path("/headers")
      @GET
      public String headers(@HttpContext HttpHeaders headers)
      {
         String value = headers.getCookies().get("meaning").getValue();
         Assert.assertEquals(value, "42");
         return value;
      }

      @Path("/param")
      @GET
      public int param(@CookieParam("meaning")int value)
      {
         Assert.assertEquals(value, 42);
         return value;
      }

      @Path("/cookieparam")
      @GET
      public String param(@CookieParam("meaning")Cookie value)
      {
         Assert.assertEquals(value.getValue(), "42");
         return value.getValue();
      }

      @Path("/default")
      @GET
      public int defaultValue(@CookieParam("defaulted") @DefaultValue("24")int value)
      {
         Assert.assertEquals(value, 24);
         return value;
      }
   }

   @BeforeClass
   public static void before() throws Exception
   {
      dispatcher = EmbeddedServletContainer.start();
      dispatcher.getRegistry().addResource(CookieResource.class);
   }

   @AfterClass
   public static void after() throws Exception
   {
      EmbeddedServletContainer.stop();
   }

   private void _test(HttpClient client, String uri)
   {
      {
         GetMethod method = new GetMethod(uri);
         try
         {
            int status = client.executeMethod(method);
            Assert.assertEquals(status, HttpResponseCodes.SC_OK);
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }
      }

   }


   @Test
   public void testIt()
   {
      HttpClient client = new HttpClient();
      _test(client, "http://localhost:8081/set");
      _test(client, "http://localhost:8081/headers");
      _test(client, "http://localhost:8081/cookieparam");
      _test(client, "http://localhost:8081/param");
      _test(client, "http://localhost:8081/default");
   }

   public static interface CookieProxy
   {
      @Path("/param")
      @GET
      public int param(@CookieParam("meaning")int value);

      @Path("/param")
      @GET
      public int param(Cookie cookie);
   }

   @Test
   public void testProxy()
   {
      {
         CookieProxy proxy = ProxyFactory.create(CookieProxy.class, "http://localhost:8081");
         proxy.param(42);
      }
      {
         CookieProxy proxy = ProxyFactory.create(CookieProxy.class, "http://localhost:8081");
         Cookie cookie = new Cookie("meaning", "42");
         proxy.param(cookie);
      }


   }

}