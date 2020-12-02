package org.jboss.resteasy.test.validation.resource;

import javax.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/{s}/{t}")
public class ValidationComplexResourceWithGraph {
   @Valid
   ValidationComplexB b;

   public ValidationComplexResourceWithGraph(@PathParam("s") final String s, @PathParam("t") final String t) {
      b = new ValidationComplexB(new ValidationComplexA(s, t));
   }

   @POST
   public void post() {
   }
}
