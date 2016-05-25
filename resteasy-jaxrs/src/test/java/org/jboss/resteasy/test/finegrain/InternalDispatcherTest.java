package org.jboss.resteasy.test.finegrain;

import org.junit.Assert;
import org.jboss.resteasy.annotations.ClientResponseType;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.core.MessageBodyParameterInjector;
import org.jboss.resteasy.spi.BadRequestException;
import org.jboss.resteasy.spi.InternalDispatcher;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Stack;

import static org.jboss.resteasy.test.TestPortProvider.generateBaseUrl;

/**
 * Test for InternalDispatcher
 *
 * @author <a href="mailto:sduskis@gmail.com">Solomon Duskis</a>
 * @version $Revision: 1 $
 */

public class InternalDispatcherTest
{

   private static Dispatcher dispatcher;
   private static ForwardingResource forwardingResource;

   private static final String PATH = "/foo/bar";

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

      @GET
      @Produces("text/plain")
      @Path("/infinite-forward")
      int infiniteForward();

      @GET
      @Path(PATH + "/basic")
      @Produces("text/plain")
      String getComplexBasic();

      @GET
      @Path(PATH + "/forward/basic")
      @Produces("text/plain")
      String getComplexForwardBasic();
   }

   @Path("/")
   public static class ForwardingResource
   {
      public Stack<String> uriStack = new Stack<String>();
      String basic = "basic";
      @Context
      UriInfo uriInfo;

      @GET
      @Produces("text/plain")
      @Path("/basic")
      public String getBasic()
      {
          String item = uriInfo.getAbsolutePath().toString();
          uriStack.push(item);
         return basic;
      }

      @GET
      @Produces("text/plain")
      @Path("/forward/basic")
      public String forwardBasic(@Context InternalDispatcher dispatcher)
      {
         uriStack.push(uriInfo.getAbsolutePath().toString());
         return (String) dispatcher.getEntity("/basic");
      }

      @PUT
      @POST
      @Consumes("text/plain")
      @Path("/basic")
      public void putBasic(String basic)
      {
         uriStack.push(uriInfo.getAbsolutePath().toString());
         this.basic = basic;
      }

      @DELETE
      @Path("/basic")
      public void deleteBasic()
      {
         uriStack.push(uriInfo.getAbsolutePath().toString());
         this.basic = "basic";
      }

      @PUT
      @Consumes("text/plain")
      @Path("/forward/basic")
      public void putForwardBasic(String basic,
                                  @Context InternalDispatcher dispatcher)
      {
         uriStack.push(uriInfo.getAbsolutePath().toString());
         dispatcher.putEntity("/basic", basic);
      }

      @POST
      @Consumes("text/plain")
      @Path("/forward/basic")
      public void postForwardBasic(String basic,
                                   @Context InternalDispatcher dispatcher)
      {
         uriStack.push(uriInfo.getAbsolutePath().toString());
         dispatcher.postEntity("/basic", basic);
      }

      @DELETE
      @Path("/forward/basic")
      public void deleteForwardBasic(@Context InternalDispatcher dispatcher)
      {
         uriStack.push(uriInfo.getAbsolutePath().toString());
         dispatcher.delete("/basic");
      }

      @GET
      @Produces("text/plain")
      @Path("/object/{id}")
      public Response getObject(@PathParam("id") Integer id)
      {
         uriStack.push(uriInfo.getAbsolutePath().toString());
         if (id == 0)
            return Response.noContent().build();
         else
            return Response.ok("object" + id).build();
      }

      @GET
      @Path("/forward/object/{id}")
      public Response forwardObject(@PathParam("id") Integer id,
                                    @Context InternalDispatcher dispatcher)
      {
         uriStack.push(uriInfo.getAbsolutePath().toString());
         return dispatcher.getResponse("/object/" + id);
      }

      @GET
      @Path(PATH + "/basic")
      @Produces("text/plain")
      public String getComplexBasic() {
         uriStack.push(uriInfo.getAbsolutePath().toString());
         return PATH + basic;
      }

      @GET
      @Produces("text/plain")
      @Path(PATH + "/forward/basic")
      public String complexForwardBasic(@Context InternalDispatcher dispatcher)
      {
         uriStack.push(uriInfo.getAbsolutePath().toString());
         return (String) dispatcher.getEntity(PATH + "/basic");
      }

      @GET
      @Path("/infinite-forward")
      @Produces("text/plain")
      public int infinitFoward(@Context InternalDispatcher dispatcher,
                               @QueryParam("count") @DefaultValue("0") int count)
      {
         uriStack.push(uriInfo.getAbsolutePath().toString());
         try
         {
            dispatcher.getEntity("/infinite-forward?count=" + (count + 1));
            // we'll never reach 20, since the max count of times through the
            // system is 20, and first time through is 0
            Assert.assertNotSame(20, count);
         }
         catch (BadRequestException e)
         {

         }
         finally
         {
            Assert
                    .assertEquals(count, MessageBodyParameterInjector.bodyCount());
            Assert.assertEquals(count + 1, ResteasyProviderFactory
                    .getContextDataLevelCount());
         }
         return ResteasyProviderFactory.getContextDataLevelCount();
      }
   }

   @BeforeClass
   public static void before() throws Exception
   {
      dispatcher = EmbeddedContainer.start().getDispatcher();
            forwardingResource = new ForwardingResource();
            dispatcher.getRegistry().addSingletonResource(forwardingResource);
   }

   @Before
   public void setup() {
       forwardingResource.uriStack.clear();
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
      ClientResponse<?> cr = (ClientResponse<?>) client.getObject(0);
      Assert.assertEquals(Response.Status.NO_CONTENT.getStatusCode(), cr.getStatus());
      cr.releaseConnection();
      cr = (ClientResponse<?>) client.getForwardedObject(0);
      Assert.assertEquals(Response.Status.NO_CONTENT.getStatusCode(), cr.getStatus());
      cr.releaseConnection();
      
      client.putForwardBasic("testBasic");
      Assert.assertEquals("testBasic", client.getBasic());
      client.postForwardBasic("testBasic1");
      Assert.assertEquals("testBasic1", client.getBasic());
      client.deleteForwardBasic();
      Assert.assertEquals("basic", client.getBasic());

   }

   @Test
   public void testInfinitForward()
   {
      Client client = ProxyFactory.create(Client.class, generateBaseUrl());
      // assert that even though there were infinite forwards, there still was
      // only 1 level of "context" data and that clean up occurred correctly.
      // This should not spin forever, since RESTEasy stops the recursive loop
      // after 20 internal dispatches
      Assert.assertEquals(1, client.infiniteForward());
   }

      @Test
   public void testUriInfoBasic() {
       String baseUrl = generateBaseUrl();
       Client client = ProxyFactory.create(Client.class, baseUrl);

       client.getBasic();
       Assert.assertEquals(baseUrl + "/basic", forwardingResource.uriStack.pop());
       Assert.assertTrue(forwardingResource.uriStack.isEmpty());

   }

   @Test
   public void testUriInfoForwardBasic() {
      String baseUrl = generateBaseUrl();
       Client client = ProxyFactory.create(Client.class, baseUrl);

       client.getForwardBasic();
       Assert.assertEquals(baseUrl + "/basic", forwardingResource.uriStack.pop());
       Assert.assertEquals(baseUrl + "/forward/basic", forwardingResource.uriStack.pop());
       Assert.assertTrue(forwardingResource.uriStack.isEmpty());
   }

   @Test
   public void testUriInfoForwardBasicComplexUri() {
      String baseUrl = generateBaseUrl();
      Client client = ProxyFactory.create(Client.class, baseUrl);

      client.getComplexForwardBasic();
      Assert.assertEquals(baseUrl + PATH + "/basic", forwardingResource.uriStack.pop());
      Assert.assertEquals(baseUrl + PATH + "/forward/basic", forwardingResource.uriStack.pop());
      Assert.assertTrue(forwardingResource.uriStack.isEmpty());
   }

}
