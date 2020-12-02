package org.jboss.resteasy.test.response.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/b")
public class DuplicitePathDupliciteResourceTwo {
   public static final String DUPLICITE_RESPONSE = "response5";

   @Path("/c")
   @GET
   @Produces(MediaType.TEXT_PLAIN)
   public String dupliciteOne() {
      return DUPLICITE_RESPONSE;
   }
}
