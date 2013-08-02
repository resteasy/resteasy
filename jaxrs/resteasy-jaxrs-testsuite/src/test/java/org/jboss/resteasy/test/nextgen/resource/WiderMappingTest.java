package org.jboss.resteasy.test.nextgen.resource;

import org.jboss.resteasy.core.ResourceMethodRegistry;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class WiderMappingTest extends BaseResourceTest
{
   @Path("/hello")
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
   }

   @Path("{x:.*}")
   public static class DefaultOptions
   {
      @OPTIONS
      public String options()
      {
         return "hello";
      }
   }




   static Client client;

   @BeforeClass
   public static void setup()
   {
      ((ResourceMethodRegistry)deployment.getRegistry()).setWiderMatching(true);
      addPerRequestResource(Resource.class);
      addPerRequestResource(DefaultOptions.class);
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void cleanup()
   {
      client.close();
   }


   @Test
   public void testOptions()
   {
      Response response = client.target(generateURL("/hello/int")).request().options();
      Assert.assertEquals(response.getStatus(), 200);
      Assert.assertEquals(response.readEntity(String.class), "hello");
      response.close();
   }

}
