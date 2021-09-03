package org.jboss.resteasy.test.response.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/b")
public class DuplicitePathDupliciteResourceOne {
   public static final String DUPLICITE_RESPONSE = "response4";

   @Path("/c")
   @GET
   @Produces(MediaType.TEXT_PLAIN)
   public String duplicite() {
      return DUPLICITE_RESPONSE;
   }
}
