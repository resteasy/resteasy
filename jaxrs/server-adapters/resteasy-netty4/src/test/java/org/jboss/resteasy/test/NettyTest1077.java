package org.jboss.resteasy.test;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockDispatcherFactory;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.plugins.server.resourcefactory.POJOResourceFactory;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * Created by Simon Ström on 7/1/14.
 */
public class NettyTest1077 {
   @Path("/")
   public static class TestResource
   {

      @Path("/ping/")
      @GET
      @Produces(MediaType.TEXT_PLAIN)
      public Response root(@Context UriInfo uriInfo)
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

   static Client client;
   @BeforeClass
   public static void setup() throws Exception
   {
      NettyContainer.start().getRegistry().addPerRequestResource(TestResource.class);
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void end() throws Exception
   {
      client.close();
      NettyContainer.stop();
   }

   @Test
   public void testNoTralingSlash() throws Exception {
      ResteasyClient client = new ResteasyClientBuilder().build();
      ResteasyWebTarget target = client.target(generateURL("/ping"));

      Response response = target.request().get();

      Assert.assertEquals(Response.Status.MOVED_PERMANENTLY.getStatusCode(), response.getStatus());
   }

   @Test
   public void testTrailingSlash() throws Exception {
      ResteasyClient client = new ResteasyClientBuilder().build();
      ResteasyWebTarget target = client.target(generateURL("/ping/"));

      Response response = target.request().get();

      Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
      Assert.assertEquals("PONG", response.readEntity(String.class));
   }

   @Test
   public void testSubPath() throws Exception {
      ResteasyClient client = new ResteasyClientBuilder().build();
      ResteasyWebTarget target = client.target(generateURL("/ping/me"));

      Response response = target.request().get();

      Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
      Assert.assertEquals("PONG", response.readEntity(String.class));
   }
}
