package org.jboss.resteasy.test.resource.path.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("users/{userID}/memberships")
public class ResourceMatchingMultipleUserMembershipResource {
   @GET
   public String findUserMemberships(
         @PathParam("userID") String userID) {
      return "users/{id}/memberships " + userID;
   }
}
