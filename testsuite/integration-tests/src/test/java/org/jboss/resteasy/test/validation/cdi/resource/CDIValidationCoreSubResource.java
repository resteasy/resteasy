package org.jboss.resteasy.test.validation.cdi.resource;

import javax.validation.constraints.Min;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

public class CDIValidationCoreSubResource {
   @GET
   @Produces(MediaType.TEXT_PLAIN)
   @Path("{subparam}")
   @Min(17)
   public int submethod(@Min(13) @PathParam("subparam") int subparam) {
      return subparam;
   }
}
