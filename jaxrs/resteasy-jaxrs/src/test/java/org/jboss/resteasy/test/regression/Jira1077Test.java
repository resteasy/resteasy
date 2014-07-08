package org.jboss.resteasy.test.regression;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockDispatcherFactory;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.plugins.server.resourcefactory.POJOResourceFactory;
import org.jboss.resteasy.spi.Registry;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * Created by Simon Ström on 7/1/14.
 */
public class Jira1077Test {
   @Path("/")
   public static class TestResource
   {
      @Path("/ping/")
      @GET
      @Produces(MediaType.TEXT_PLAIN)
      public Response getRoot(@Context UriInfo uriInfo)
      {
         if (uriInfo.getPath().endsWith("/")) {
            return ping();

         } else {
            // Redirect with trailing slash; this is important when resolving relative URLs.
            return Response
                    .status(Response.Status.MOVED_PERMANENTLY)
                    .location(uriInfo.getBaseUriBuilder().path("/ping/").build())
                    .build();
         }
      }

      @GET
      @Path("/ping/me")
      @Produces(MediaType.TEXT_PLAIN)
      public Response ping()
      {
         return Response
                 .status(Response.Status.OK)
                 .entity("PONG")
                 .build();
      }
   }

   /**
    * Ensure that UriInfo of the method contains the slash
    * @throws Exception
    */
   @Test
   public void testNoTrailingSlash() throws Exception {
      Dispatcher dispatcher = MockDispatcherFactory.createDispatcher();
      dispatcher.getRegistry().addResourceFactory(new POJOResourceFactory(TestResource.class));

      MockHttpRequest request = MockHttpRequest.get("/ping");
      MockHttpResponse response = new MockHttpResponse();

      dispatcher.invoke(request, response);

      Assert.assertEquals(Response.Status.MOVED_PERMANENTLY.getStatusCode(), response.getStatus());
   }

   @Test
   public void testTrailingSlash() throws Exception {
      Dispatcher dispatcher = MockDispatcherFactory.createDispatcher();
      Registry registry = dispatcher.getRegistry();
      POJOResourceFactory resource = new POJOResourceFactory(TestResource.class);
      registry.addResourceFactory(resource);

      MockHttpRequest request = MockHttpRequest.get("/ping/");
      MockHttpResponse response = new MockHttpResponse();

      dispatcher.invoke(request, response);

      Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
      Assert.assertEquals("PONG", response.getContentAsString());
   }

   @Test
   public void testSubPath() throws Exception {
      Dispatcher dispatcher = MockDispatcherFactory.createDispatcher();
      dispatcher.getRegistry().addResourceFactory(new POJOResourceFactory(TestResource.class));

      {
         MockHttpRequest request = MockHttpRequest.get("/ping/me");
         MockHttpResponse response = new MockHttpResponse();

         dispatcher.invoke(request, response);

         Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
         Assert.assertEquals("PONG", response.getContentAsString());
      }
   }

   @Test
   public void testSubPathWithSlashShouldBehaveTheSame() throws Exception {
      Dispatcher dispatcher = MockDispatcherFactory.createDispatcher();
      dispatcher.getRegistry().addResourceFactory(new POJOResourceFactory(TestResource.class));

      {
         MockHttpRequest request = MockHttpRequest.get("/ping/me/");
         MockHttpResponse response = new MockHttpResponse();

         dispatcher.invoke(request, response);

         Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
         Assert.assertEquals("PONG", response.getContentAsString());
      }
   }
}
