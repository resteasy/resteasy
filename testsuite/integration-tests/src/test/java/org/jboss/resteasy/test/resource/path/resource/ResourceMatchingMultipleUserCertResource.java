package org.jboss.resteasy.test.resource.path.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("users/{userID}/certs")
public class ResourceMatchingMultipleUserCertResource {
   @GET
   public String findUserCerts(
         @PathParam("userID") String userID) {
      return "users/{id}/certs " + userID;

   }
}
