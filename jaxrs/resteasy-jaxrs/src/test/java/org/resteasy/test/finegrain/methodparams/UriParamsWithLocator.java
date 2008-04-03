package org.resteasy.test.finegrain.methodparams;

import org.junit.Assert;
import org.junit.Test;
import org.resteasy.Dispatcher;
import org.resteasy.mock.MockHttpServletRequest;
import org.resteasy.mock.MockHttpServletResponse;
import org.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.resteasy.test.MockDispatcherFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.PathSegment;
import java.io.IOException;

/**
 * Test that a locator and resource with same path params work
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class UriParamsWithLocator
{
   public static class Resource
   {
      @GET
      @Path("/{id}")
      public String get(@PathParam("id")String id)
      {
         Assert.assertEquals("2", id);
         return id;
      }
   }

   @Path("/")
   public static class Locator
   {
      @Path("/{id}")
      public Resource get(@PathParam("id")String id)
      {
         Assert.assertEquals("1", id);
         return new Resource();

      }
   }

   public static class Resource2
   {
      @GET
      @Path("/{id}")
      public String get(@PathParam("id")PathSegment id)
      {
         Assert.assertEquals("2", id.getPath());
         return id.getPath();
      }
   }

   @Path("/")
   public static class Locator2
   {
      @Path("/{id}")
      public Resource2 get(@PathParam("id")PathSegment id)
      {
         Assert.assertEquals("1", id.getPath());
         return new Resource2();

      }
   }

   @Test
   public void testDoubleId() throws Exception
   {
      HttpServletDispatcher servlet = MockDispatcherFactory.createDispatcher();
      Dispatcher dispatcher = servlet.getDispatcher();
      dispatcher.getRegistry().addPerRequestResource(Locator.class);
      {
         MockHttpServletRequest request = new MockHttpServletRequest("GET", "/1/2");
         request.setPathInfo("/1/2");
         MockHttpServletResponse response = new MockHttpServletResponse();

         try
         {
            servlet.invoke(request, response);
         }
         catch (ServletException e)
         {
            throw new RuntimeException(e);
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }


         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
      }

   }

   @Test
   public void testDoubleIdAsPathSegment() throws Exception
   {
      HttpServletDispatcher servlet = MockDispatcherFactory.createDispatcher();
      Dispatcher dispatcher = servlet.getDispatcher();
      dispatcher.getRegistry().addPerRequestResource(Locator2.class);
      {
         MockHttpServletRequest request = new MockHttpServletRequest("GET", "/1/2");
         request.setPathInfo("/1/2");
         MockHttpServletResponse response = new MockHttpServletResponse();

         try
         {
            servlet.invoke(request, response);
         }
         catch (ServletException e)
         {
            throw new RuntimeException(e);
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }


         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
      }

   }
}