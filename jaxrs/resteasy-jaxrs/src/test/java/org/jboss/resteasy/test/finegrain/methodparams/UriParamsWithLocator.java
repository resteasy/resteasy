package org.jboss.resteasy.test.finegrain.methodparams;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockDispatcherFactory;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.junit.Assert;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.PathSegment;

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
      public String get(@PathParam("id") String id)
      {
         Assert.assertEquals("2", id);
         return id;
      }
   }

   @Path("/")
   public static class Locator
   {
      @Path("/{id}")
      public Resource get(@PathParam("id") String id)
      {
         Assert.assertEquals("1", id);
         return new Resource();

      }
   }

   public static class Resource2
   {
      @GET
      @Path("/{id}")
      public String get(@PathParam("id") PathSegment id)
      {
         Assert.assertEquals("2", id.getPath());
         return id.getPath();
      }
   }

   @Path("/")
   public static class Locator2
   {
      @Path("/{id}")
      public Resource2 get(@PathParam("id") PathSegment id)
      {
         Assert.assertEquals("1", id.getPath());
         return new Resource2();

      }
   }

   @Test
   public void testDoubleId() throws Exception
   {
      Dispatcher dispatcher = MockDispatcherFactory.createDispatcher();
      dispatcher.getRegistry().addPerRequestResource(Locator.class);
      {
         MockHttpRequest request = MockHttpRequest.get("/1/2");
         MockHttpResponse response = new MockHttpResponse();
         dispatcher.invoke(request, response);

         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
      }

   }

   @Test
   public void testDoubleIdAsPathSegment() throws Exception
   {
      Dispatcher dispatcher = MockDispatcherFactory.createDispatcher();
      dispatcher.getRegistry().addPerRequestResource(Locator2.class);
      {
         MockHttpRequest request = MockHttpRequest.get("/1/2");
         MockHttpResponse response = new MockHttpResponse();
         dispatcher.invoke(request, response);

         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
      }

   }
}