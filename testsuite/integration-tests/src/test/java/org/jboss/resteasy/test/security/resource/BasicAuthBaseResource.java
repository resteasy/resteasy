package org.jboss.resteasy.test.security.resource;

import org.jboss.logging.Logger;

import javax.annotation.security.DenyAll;
import javax.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;
import java.util.List;

@Path("/secured")
public class BasicAuthBaseResource {
   private static Logger logger = Logger.getLogger(BasicAuthBaseResource.class);

   @GET
   @Path("/failure")
   @RolesAllowed("admin")
   public List<String> getFailure() {
      return null;
   }

   @GET
   public String get(@Context SecurityContext ctx) {
      logger.info("********* IN SECURE CLIENT");
      if (!ctx.isUserInRole("admin")) {
         logger.info("NOT IN ROLE!!!!");
         throw new WebApplicationException(403);
      }
      return "hello";
   }

   @GET
   @Path("/authorized")
   @RolesAllowed("admin")
   public String getAuthorized() {
      return "authorized";
   }

   @GET
   @Path("/deny")
   @DenyAll
   public String deny() {
      return "SHOULD NOT BE REACHED";
   }

   @GET
   @Path("/denyWithContentType")
   @Produces("application/xml")
   @RolesAllowed("admin")
   public String getWithContentType() {
      return "string";
   }
}
