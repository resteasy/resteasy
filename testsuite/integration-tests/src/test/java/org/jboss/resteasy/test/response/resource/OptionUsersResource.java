package org.jboss.resteasy.test.response.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;

@Path("/users")
public class OptionUsersResource {
   @GET
   @Produces("text/plain")
   public String get() {
      return "users";
   }

   @POST
   @Consumes("text/plain")
   public void post(String users) {

   }

   @GET
   @Path("{user-id}")
   @Produces("text/plain")
   public String getUserId(@PathParam("user-id") String userId) {
      return userId;
   }

   @DELETE
   @Path("{user-id}")
   @Produces("text/plain")
   public String deleteUserId(@PathParam("user-id") String userId) {
      return userId;
   }

   @PUT
   @Path("{user-id}")
   @Consumes("text/plain")
   public void postUserId(@PathParam("user-id") String userId, String user) {

   }

   @GET
   @Path("{user-id}/contacts")
   @Produces("text/plain")
   public String getContacts(@PathParam("user-id") String userId) {
      return userId;
   }

   @POST
   @Path("{user-id}/contacts")
   @Consumes("text/plain")
   public void postContacts(@PathParam("user-id") String userId, String user) {

   }

   @GET
   @Path("{user-id}/contacts/{contact-id}")
   @Produces("text/plain")
   public String getContactId(@PathParam("user-id") String userId) {
      return userId;
   }

   @DELETE
   @Path("{user-id}/contacts/{contact-id}")
   @Produces("text/plain")
   public String deleteCotactId(@PathParam("user-id") String userId) {
      return userId;
   }

   @PUT
   @Path("{user-id}/contacts/{contact-id}")
   @Consumes("text/plain")
   public void postContactId(@PathParam("user-id") String userId, String user) {

   }

}
