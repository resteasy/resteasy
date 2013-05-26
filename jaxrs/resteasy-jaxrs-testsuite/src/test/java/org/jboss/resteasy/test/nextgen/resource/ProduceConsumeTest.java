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
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ProduceConsumeTest extends BaseResourceTest
{
   @Path("resource")
   public static class Resource {

      @POST
      @Path("plain")
      @Produces(MediaType.TEXT_PLAIN)
      public String postPlain()
      {
         return MediaType.TEXT_PLAIN;
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
   public void testNullContent()
   {
      Response response = client.target(generateURL("/resource/plain")).request("text/plain").post(null);
      Assert.assertEquals(response.getStatus(), 200);
      response.close();
   }

}
