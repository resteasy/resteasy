package org.jboss.resteasy.test.nextgen.client;

import java.io.IOException;
import java.util.Iterator;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * RESTEASY-1266
 * 
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @date
 */
public class MultipleCookieTest
{
   private static final Logger log = Logger.getLogger(MultipleCookieTest.class);
   private static final String OLD_COOKIE_NAME = "old-cookie";
   private static final String NEW_COOKIE_NAME = "new-cookie";
   protected static ResteasyDeployment deployment;
   protected static Dispatcher dispatcher;

   @Path("test")
   public static class TestResource
   {
      private @Context HttpHeaders headers;

      @GET
      @Path("get")
      public Response getCookie()
      {
         NewCookie cookie = new NewCookie(OLD_COOKIE_NAME, "value");
         return Response.ok().cookie(cookie).build();
      }

      @GET
      @Path("return")
      public Response returnCookie()
      {
         Cookie oldCookie = headers.getCookies().get(OLD_COOKIE_NAME);
         Cookie newCookie = headers.getCookies().get(NEW_COOKIE_NAME);
         log.info("returnCookie(): cookies: " + oldCookie + " / " + newCookie);
         ResponseBuilder builder = Response.ok();
         builder.cookie(new NewCookie(oldCookie.getName(), oldCookie.getValue()));
         builder.cookie(new NewCookie(newCookie.getName(), newCookie.getValue()));
         return builder.build();
      }
   }

   @Provider
   @PreMatching
   public static class TestFilter implements ContainerRequestFilter
   {
      @Override
      public void filter(ContainerRequestContext requestContext) throws IOException
      {
         final Cookie cookie = requestContext.getCookies().get(OLD_COOKIE_NAME);
         log.info("TestFilter.filter(): cookie: " + cookie);
         if (cookie != null)
         {
            requestContext.getHeaders().add(HttpHeaders.COOKIE,
                  new Cookie(NEW_COOKIE_NAME, cookie.getValue()).toString());
         }
      }
   }

   @Provider
   public class ContextProvider implements ClientRequestFilter
   {

      protected void checkFilterContext(ClientRequestContext context) throws Throwable
      {
         throw new Throwable("this TCK method is not implemented yet");
      }

      @Override
      public void filter(ClientRequestContext context) throws IOException
      {
         try
         {
            checkFilterContext(context);
         }
         catch (Throwable e)
         {
            throw new IOException(e);
         }
      }

   }

   @Before
   public void before() throws Exception
   {
      deployment = EmbeddedContainer.start();
      dispatcher = deployment.getDispatcher();
      deployment.getRegistry().addPerRequestResource(TestResource.class);
      deployment.getProviderFactory().register(TestFilter.class);
   }

   @After
   public void after() throws Exception
   {
      EmbeddedContainer.stop();
      dispatcher = null;
      deployment = null;
   }

   @Test
   public void getCookiesTest() throws Throwable
   {
      Cookie cts = new Cookie("cts", "cts");
      Cookie tck = new Cookie("tck", "tck");
      Cookie jee = new Cookie("jee", "jee");

      ContextProvider provider = new ContextProvider()
      {
         @Override
         protected void checkFilterContext(ClientRequestContext context) throws Throwable
         {
            String cookies = iterableToString(";", context.getCookies().values());
            Response r = Response.ok(cookies).build();
            context.abortWith(r);
         }
      };
      Invocation invocation = buildBuilder(provider).cookie(cts).cookie(tck).cookie(jee).buildGet();
      Response response = invoke(invocation);

      String entity = response.readEntity(String.class);
      System.out.println("entity: " + entity);
      Assert.assertTrue(entity.contains("cts"));
      Assert.assertTrue(entity.contains("tck"));
      Assert.assertTrue(entity.contains("jee"));
   }

   public static final <T> //
   String iterableToString(String separator, Iterable<T> collection)
   {
      if (collection != null)
         return iterableToString(separator, collection.iterator());
      return "";
   }

   public static final <T> //
   String iterableToString(String separator, Iterator<T> iterator)
   {
      StringBuilder sb = new StringBuilder();
      while (iterator.hasNext())
      {
         T item = iterator.next();
         if (item != null)
         {
            String appendable = item.toString();
            sb.append(appendable);
            if (iterator.hasNext())
               sb.append(separator);
         }
      }
      return sb.toString();
   }

   protected static WebTarget buildTarget(ContextProvider... providers)
   {
      Client client = ClientBuilder.newClient();
      for (ContextProvider provider : providers)
         client.register(provider);
      WebTarget target = client.target(getUrl());
      return target;
   }

   protected static Invocation.Builder buildBuilder(ContextProvider... provider)
   {
      Invocation.Builder builder = buildTarget(provider).request();
      return builder;
   }

   protected static String getUrl()
   {
      return "http://localhost:8080/404URL/";
   }

   protected static Response invoke(Invocation i) throws Throwable
   {
      Response r = null;
      try
      {
         r = i.invoke();
      }
      catch (Exception e)
      {
         Object cause = e.getCause();
         if (cause instanceof Throwable)
            throw (Throwable) cause;
         else
            throw new Throwable(e);
      }
      return r;
   }
}
