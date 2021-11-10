package org.jboss.resteasy.test.resource.path.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("users")
public class ResourceMatchingMultipleUserResource {
   @GET
   @Path("{userID}")
   public String getUser(@PathParam("userID") String userID) {
      return "users/{id} " + userID;
   }
}
