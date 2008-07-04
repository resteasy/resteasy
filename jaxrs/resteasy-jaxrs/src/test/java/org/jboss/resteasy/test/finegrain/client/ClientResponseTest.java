package org.jboss.resteasy.test.finegrain.client;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.plugins.client.httpclient.ProxyFactory;
import org.jboss.resteasy.spi.ClientResponse;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.jboss.resteasy.test.smoke.SimpleResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.ConsumeMime;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.ProduceMime;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 * Simple smoke test
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ClientResponseTest
{

   private static Dispatcher dispatcher;

   @Path("/")
   public interface Client
   {
      @GET
      @Path("basic")
      @ProduceMime("text/plain")
      ClientResponse<String> getBasic();

      @PUT
      @Path("basic")
      @ConsumeMime("text/plain")
      void putBasic(String body);

      @PUT
      @Path("basic")
      @ConsumeMime("text/plain")
      Response.Status putBasicReturnCode(String body);

      @GET
      @Path("queryParam")
      @ProduceMime("text/plain")
      ClientResponse<String> getQueryParam(@QueryParam("param")String param);

      @GET
      @Path("uriParam/{param}")
      @ProduceMime("text/plain")
      ClientResponse<Integer> getUriParam(@PathParam("param")int param);
   }


   @BeforeClass
   public static void before() throws Exception
   {
      dispatcher = EmbeddedContainer.start();
      dispatcher.getRegistry().addPerRequestResource(SimpleResource.class);
   }

   @AfterClass
   public static void after() throws Exception
   {
      EmbeddedContainer.stop();
   }

   @Test
   public void testClientResponse() throws Exception
   {
      Client client = ProxyFactory.create(Client.class, "http://localhost:8081");

      Assert.assertEquals("basic", client.getBasic().getEntity());
      client.putBasic("hello world");
      Assert.assertEquals("hello world", client.getQueryParam("hello world").getEntity());
      Assert.assertEquals(1234, client.getUriParam(1234).getEntity().intValue());
      Assert.assertEquals(Response.Status.OK, client.putBasicReturnCode("hello world"));
   }

   @Test
   public void testErrorResponse() throws Exception
   {
      Client client = ProxyFactory.create(Client.class, "http://localhost:8081/shite");
      Assert.assertEquals(HttpResponseCodes.SC_NOT_FOUND, client.getBasic().getStatus());

      try
      {
         System.out.println(client.getUriParam(3333).getEntity());
      }
      catch (Exception e)
      {
         Assert.assertTrue(e.getMessage().startsWith("Unable to find a MessageBodyReader of content-type"));
      }

   }


}