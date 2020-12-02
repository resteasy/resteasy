package org.jboss.resteasy.test.validation.resource;

import javax.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/{s}")
public class ValidationComplexResourceWithMap {
   @Valid
   ValidationComplexMapOfStrings mos;

   public ValidationComplexResourceWithMap(@PathParam("s") final String s) {
      mos = new ValidationComplexMapOfStrings(s);
   }

   @POST
   public void post() {
   }
}
