package org.jboss.resteasy.test.cdi.injection.resource;

import javax.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("user")
public class UserResource {

   @Inject
   private UserManager userManager;

   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public ApplicationUser getUser() {
      return userManager.getUser();
   }

   @GET
   @Produces(MediaType.APPLICATION_XML)
   public ApplicationUser getUserJaxb() {
      return userManager.getUser();
   }

}
