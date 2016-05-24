package org.jboss.resteasy.test.nextgen.resource;

import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PrimitiveTest extends BaseResourceTest
{
   @Path("/")
   @Produces("text/plain")
   @Consumes("text/plain")
   public static class Resource {
      @POST
      @Path("int")
      public int postInt(int val) {
         return val;
      }

      @POST
      @Path("boolean")
      public boolean postInt(boolean val) {
         return val;
      }

      @GET
      @Path("nothing")
      public Response nothing()
      {
         return Response.ok().build();
      }
   }




   static Client client;

   @BeforeClass
   public static void setup()
   {
      addPerRequestResource(Resource.class);
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void cleanup()
   {
      client.close();
   }


   @Test
   public void testInt()
   {
      Response response = client.target(generateURL("/int")).request().post(Entity.text("5"));
      Assert.assertEquals(response.getStatus(), 200);
      Assert.assertEquals(response.readEntity(String.class), "5");
      response.close();
   }

   @Test
   public void testBoolean()
   {
      Response response = client.target(generateURL("/boolean")).request().post(Entity.text("true"));
      Assert.assertEquals(response.getStatus(), 200);
      Assert.assertEquals(response.readEntity(String.class), "true");
      response.close();
   }

   @Test
   public void testNothing()
   {
      Response response = client.target(generateURL("/nothing")).request().get();
      String str = response.readEntity(String.class);

   }





}
