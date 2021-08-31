package org.jboss.resteasy.test.security.resource;


import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/secured3")
@RolesAllowed("admin")
public class BasicAuthBaseResourceAnybody {
   @GET
   @Path("/authorized")
   public String getAuthorized() {
      return "authorized";
   }

   @GET
   @Path("/anybody")
   @PermitAll
   public String getAnybody() {
      return "anybody";
   }
}
