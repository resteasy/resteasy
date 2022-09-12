package org.jboss.resteasy.test.security.resource;

import org.jboss.resteasy.spi.HttpResponseCodes;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("test")
public class SecurityContextResource {
   @Context
   SecurityContext securityContext;

   @GET
   @Produces("text/plain")
   public String get() {
      if (!securityContext.isUserInRole("admin")) {
         throw new WebApplicationException(Response.serverError().status(HttpResponseCodes.SC_UNAUTHORIZED)
               .entity("User " + securityContext.getUserPrincipal().getName() + " is not authorized").build());
      }
      return "Good user " + securityContext.getUserPrincipal().getName();
   }
}
