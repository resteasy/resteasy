package org.jboss.resteasy.test.finegrain.resource;

import static org.jboss.resteasy.test.TestPortProvider.*;

import java.io.IOException;

import javax.ws.rs.CookieParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class CookieTest
{
   private static Dispatcher dispatcher;

   @Path("/")
   public static class CookieResource
   {
      @Path("/set")
      @GET
      public Response set()
      {
         return Response.ok("content").cookie(new NewCookie("meaning", "42")).build();
      }

      @Context
      HttpHeaders myHeaders;

      @Path("/headers")
      @GET
      public String headers(@Context HttpHeaders headers)
      {
         String value = headers.getCookies().get("meaning").getValue();
         Assert.assertEquals(value, "42");
         return value;
      }

      @Path("/headers/fromField")
      @GET
      public String headersFromField(@Context HttpHeaders headers)
      {
         String value = myHeaders.getCookies().get("meaning").getValue();
         Assert.assertEquals(value, "42");
         return value;
      }

      @Path("/param")
      @GET
      public int param(@CookieParam("meaning") int value)
      {
         Assert.assertEquals(value, 42);
         return value;
      }

      @Path("/cookieparam")
      @GET
      public String param(@CookieParam("meaning") Cookie value)
      {
         Assert.assertEquals(value.getValue(), "42");
         return value.getValue();
      }

      @Path("/default")
      @GET
      public int defaultValue(@CookieParam("defaulted") @DefaultValue("24") int value)
      {
         Assert.assertEquals(value, 24);
         return value;
      }
   }

   @BeforeClass
   public static void before() throws Exception
   {
      dispatcher = EmbeddedContainer.start();
      dispatcher.getRegistry().addPerRequestResource(CookieResource.class);
   }

   @AfterClass
   public static void after() throws Exception
   {
      EmbeddedContainer.stop();
   }

   private void _test(HttpClient client, String path)
   {
      {
         GetMethod method = createGetMethod(path);
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
      _test(client, "/set");
      _test(client, "/headers");
      _test(client, "/headers/fromField");
      _test(client, "/cookieparam");
      _test(client, "/param");
      _test(client, "/default");
   }

   public static interface CookieProxy
   {
      @Path("/param")
      @GET
      public int param(@CookieParam("meaning") int value);

      @Path("/param")
      @GET
      public int param(Cookie cookie);
   }

   @Test
   public void testProxy()
   {
      {
         CookieProxy proxy = ProxyFactory.create(CookieProxy.class, generateBaseUrl());
         proxy.param(42);
      }
      {
         CookieProxy proxy = ProxyFactory.create(CookieProxy.class, generateBaseUrl());
         Cookie cookie = new Cookie("meaning", "42");
         proxy.param(cookie);
      }

   }

}