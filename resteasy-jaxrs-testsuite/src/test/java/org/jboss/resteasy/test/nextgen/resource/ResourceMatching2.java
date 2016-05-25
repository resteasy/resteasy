package org.jboss.resteasy.test.nextgen.resource;

import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ResourceMatching2 extends BaseResourceTest
{


   @Path("users")
   public static class UserResource
   {
      @GET
      @Path("{userID}")
      public String getUser(@PathParam("userID") String userID)
      {
         return "users/{id} " + userID;
      }
   }


   @Path("users/{userID}/certs")
   public static class UserCertResource
   {
      @GET
      public String findUserCerts(
              @PathParam("userID") String userID)
      {
         return "users/{id}/certs " + userID;

      }
   }


   @Path("users/{userID}/memberships")
   public static class UserMembershipResource
   {
      @GET
      public String findUserMemberships(
              @PathParam("userID") String userID)
      {
         return "users/{id}/memberships " + userID;
      }
   }

   static Client client;

   @BeforeClass
   public static void setup()
   {
      addPerRequestResource(UserCertResource.class);
      addPerRequestResource(UserResource.class);
      addPerRequestResource(UserMembershipResource.class);
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void cleanup()
   {
      client.close();
   }

   @Test
   public void testMatching() throws Exception
   {
      String answer = client.target(generateURL("/users/1")).request().get(String.class);
      Assert.assertEquals(answer, "users/{id} 1");

      answer = client.target(generateURL("/users/1/memberships")).request().get(String.class);
      Assert.assertEquals(answer, "users/{id}/memberships 1");

      answer = client.target(generateURL("/users/1/certs")).request().get(String.class);
      Assert.assertEquals(answer, "users/{id}/certs 1");
   }


}
