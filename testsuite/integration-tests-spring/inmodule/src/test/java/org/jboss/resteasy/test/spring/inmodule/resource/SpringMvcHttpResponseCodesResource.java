package org.jboss.resteasy.test.spring.inmodule.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;

import org.jboss.resteasy.core.ResteasyContext;

@Path("/")
public class SpringMvcHttpResponseCodesResource {
   @POST
   @Path("/test/json")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public SpringMvcHttpResponseCodesPerson postJson(SpringMvcHttpResponseCodesPerson person) {
      return person;
   }

   @POST
   @Path("/secured/json")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public SpringMvcHttpResponseCodesPerson postJsonSecured(SpringMvcHttpResponseCodesPerson person) {
      //Using the workaround below instead of @RolesAllowed("admin")
      //as I can't easily turn security on in the ResteasyDeployment built through the springmvc-resteasy.xml descriptor
      SecurityContext context = ResteasyContext.getContextData(SecurityContext.class);
      if (context != null) {
         if (!context.isUserInRole("admin")) {
            throw new ForbiddenException();
         }
      }
      return person;
   }
}
