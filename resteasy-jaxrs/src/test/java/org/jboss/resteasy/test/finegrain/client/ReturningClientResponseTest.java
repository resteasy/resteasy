package org.jboss.resteasy.test.finegrain.client;

import static org.jboss.resteasy.test.TestPortProvider.generateBaseUrl;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.ClientResponseType;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.InternalDispatch;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Simple smoke test
 * 
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ReturningClientResponseTest
{

   private static Dispatcher dispatcher;

   @Path("/")
   public interface Client
   {
      @GET
      @Path("basic")
      @Produces("text/plain")
      String getBasic();

      @PUT
      @Path("basic")
      @Consumes("text/plain")
      void putBasic(String body);

      @GET
      @Path("forward/basic")
      @Produces("text/plain")
      String getForwardBasic();

      @PUT
      @Path("forward/basic")
      @Consumes("text/plain")
      void putForwardBasic(String body);

      @POST
      @Path("forward/basic")
      @Consumes("text/plain")
      void postForwardBasic(String body);

      @DELETE
      @Path("/forward/basic")
      void deleteForwardBasic();

      @GET
      @Produces("text/plain")
      @Path("/forward/object/{id}")
      @ClientResponseType(entityType = String.class)
      Response getForwardedObject(@PathParam("id") Integer id);

      @GET
      @Produces("text/plain")
      @Path("/object/{id}")
      @ClientResponseType(entityType = String.class)
      Response getObject(@PathParam("id") Integer id);

   }

   @Path("/")
   public static class ForwardingResource
   {
      String basic = "basic";

      @GET
      @Produces("text/plain")
      @Path("/basic")
      public String getBasic()
      {
         return basic;
      }

      @GET
      @Produces("text/plain")
      @Path("/forward/basic")
      public String forwardBasic()
      {
         return (String) InternalDispatch.getInstance().getEntity("/basic");
      }

      @PUT
      @POST
      @Consumes("text/plain")
      @Path("/basic")
      public void putBasic(String basic)
      {
         this.basic = basic;
      }

      @DELETE
      @Path("/basic")
      public void deleteBasic()
      {
         this.basic = "basic";
      }

      @PUT
      @Consumes("text/plain")
      @Path("/forward/basic")
      public void putForwardBasic(String basic)
      {
         InternalDispatch.getInstance().putEntity("/basic", basic);
      }

      @POST
      @Consumes("text/plain")
      @Path("/forward/basic")
      public void postForwardBasic(String basic)
      {
         InternalDispatch.getInstance().postEntity("/basic", basic);
      }

      @DELETE
      @Consumes("text/plain")
      @Path("/forward/basic")
      public void deleteForwardBasic()
      {
         InternalDispatch.getInstance().delete("/basic");
      }

      @GET
      @Produces("text/plain")
      @Path("/object/{id}")
      public Response getObject(@PathParam("id") Integer id)
      {
         if (id == 0)
            return Response.noContent().build();
         else
            return Response.ok("object" + id).build();
      }

      @GET
      @Path("/forward/object/{id}")
      public Response forwardObject(@PathParam("id") Integer id)
      {
         return InternalDispatch.getInstance().getResponse("/object/" + id);
      }
   }

   @BeforeClass
   public static void before() throws Exception
   {
      dispatcher = EmbeddedContainer.start();
      dispatcher.getRegistry().addSingletonResource(new ForwardingResource());
   }

   @AfterClass
   public static void after() throws Exception
   {
      EmbeddedContainer.stop();
   }

   @Test
   public void testClientResponse() throws Exception
   {
      Client client = ProxyFactory.create(Client.class, generateBaseUrl());

      Assert.assertEquals("basic", client.getBasic());
      Assert.assertEquals("basic", client.getForwardBasic());
      Assert.assertEquals("object1", client.getObject(1).getEntity());
      Assert.assertEquals("object1", client.getForwardedObject(1).getEntity());
      Assert.assertEquals(Response.Status.NO_CONTENT.getStatusCode(), client
            .getObject(0).getStatus());
      Assert.assertEquals(Response.Status.NO_CONTENT.getStatusCode(), client
            .getForwardedObject(0).getStatus());
      client.putForwardBasic("testBasic");
      Assert.assertEquals("testBasic", client.getBasic());
      client.postForwardBasic("testBasic1");
      Assert.assertEquals("testBasic1", client.getBasic());
      client.deleteForwardBasic();
      Assert.assertEquals("basic", client.getBasic());
  }

}