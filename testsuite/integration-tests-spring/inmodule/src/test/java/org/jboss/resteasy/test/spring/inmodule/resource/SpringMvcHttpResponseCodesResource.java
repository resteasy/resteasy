package org.jboss.resteasy.test.spring.inmodule.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

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
