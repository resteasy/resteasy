package org.jboss.resteasy.test.regression;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class OptionsTest extends BaseResourceTest
{
   @Path("params")
   public static class ParamsResource
   {
      @Path("/customers/{custid}/phonenumbers")
      @GET
      @Produces("text/plain")
      public String getPhoneNumbers()
      {
         return "912-111-1111";
      }

      @Path("/customers/{custid}/phonenumbers/{id}")
      @GET
      @Produces("text/plain")
      public String getPhoneIds()
      {
         return "1111";
      }
   }

   // RESTEASY-363

   @Path("/users")
   public static class Users
   {
      @GET
      @Produces("text/plain")
      public String get()
      {
         return "users";
      }

      @POST
      @Consumes("text/plain")
      public void post(String users)
      {

      }

      @GET
      @Path("{user-id}")
      @Produces("text/plain")
      public String getUserId(@PathParam("user-id") String userId)
      {
         return userId;
      }

      @DELETE
      @Path("{user-id}")
      @Produces("text/plain")
      public String deleteUserId(@PathParam("user-id") String userId)
      {
         return userId;
      }

      @PUT
      @Path("{user-id}")
      @Consumes("text/plain")
      public void postUserId(@PathParam("user-id") String userId, String user)
      {

      }

      @GET
      @Path("{user-id}/contacts")
      @Produces("text/plain")
      public String getContacts(@PathParam("user-id") String userId)
      {
         return userId;
      }

      @POST
      @Path("{user-id}/contacts")
      @Consumes("text/plain")
      public void postContacts(@PathParam("user-id") String userId, String user)
      {

      }

      @GET
      @Path("{user-id}/contacts/{contact-id}")
      @Produces("text/plain")
      public String getContactId(@PathParam("user-id") String userId)
      {
         return userId;
      }

      @DELETE
      @Path("{user-id}/contacts/{contact-id}")
      @Produces("text/plain")
      public String deleteCotactId(@PathParam("user-id") String userId)
      {
         return userId;
      }

      @PUT
      @Path("{user-id}/contacts/{contact-id}")
      @Consumes("text/plain")
      public void postContactId(@PathParam("user-id") String userId, String user)
      {

      }

   }

   @BeforeClass
   public static void init() throws Exception
   {
      addPerRequestResource(ParamsResource.class);
      addPerRequestResource(Users.class);

   }

   @Test
   public void testOptions() throws Exception
   {
      ClientRequest request = new ClientRequest(TestPortProvider.generateURL("/params/customers/333/phonenumbers"));
      ClientResponse response = request.options();
      Assert.assertEquals(200, response.getStatus());

   }

   @Test
   public void testMethodNotAllowed() throws Exception
   {
      ClientRequest request = new ClientRequest(TestPortProvider.generateURL("/params/customers/333/phonenumbers"));
      ClientResponse response = request.post();
      Assert.assertEquals(405, response.getStatus());

      // RESTEasy-363

      request = new ClientRequest(TestPortProvider.generateURL("/users"));
      response = request.delete();
      Assert.assertEquals(405, response.getStatus());

      request = new ClientRequest(TestPortProvider.generateURL("/users/53"));
      response = request.post();
      Assert.assertEquals(405, response.getStatus());

      request = new ClientRequest(TestPortProvider.generateURL("/users/53/contacts"));
      response = request.get();
      Assert.assertEquals(200, response.getStatus());

      request = new ClientRequest(TestPortProvider.generateURL("/users/53/contacts"));
      response = request.delete();
      Assert.assertEquals(405, response.getStatus());

      request = new ClientRequest(TestPortProvider.generateURL("/users/53/contacts/carl"));
      response = request.get();
      Assert.assertEquals(200, response.getStatus());

      request = new ClientRequest(TestPortProvider.generateURL("/users/53/contacts/carl"));
      response = request.post();
      Assert.assertEquals(405, response.getStatus());


   }
}
