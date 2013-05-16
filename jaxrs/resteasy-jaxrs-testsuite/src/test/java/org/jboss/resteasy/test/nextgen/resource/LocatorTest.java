package org.jboss.resteasy.test.nextgen.resource;

import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class LocatorTest extends BaseResourceTest
{
   @Path("resource")
   public static class Resource {
      @GET
      @Path("responseok")
      public String responseOk() {
         return "ok";
      }
   }

   @Path("locator")
   public static class Locator {
      @Path("responseok")
      public Resource responseOk() {
         return new Resource();
      }
   }


   static Client client;

   @BeforeClass
   public static void setup()
   {
      addPerRequestResource(Resource.class);
      addPerRequestResource(Locator.class);
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void cleanup()
   {
      client.close();
   }


   @Test
   public void testLocatorWithSubWithPathAnnotation()
   {
      Response response = client.target(generateURL("/locator/responseok/responseok")).request().get();
      Assert.assertEquals(response.getStatus(), 200);
   }


}
