package org.jboss.resteasy.test.security.resource;

import org.jboss.logging.Logger;
import javax.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;

@Path("/secured2")
public class BasicAuthBaseResourceMoreSecured {
   private static Logger logger = Logger.getLogger(BasicAuthBaseResourceMoreSecured.class);

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

}
