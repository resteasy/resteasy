package org.jboss.resteasy.test.validation.cdi.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.Path;

@Path("/")
public class SubresourceValidationResource {
   @Inject
   private SubresourceValidationSubResource subResource;

   @Path("/sub")
   public SubresourceValidationSubResource getSubResouce() {
      return subResource;
   }
}
