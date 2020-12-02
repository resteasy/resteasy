package org.jboss.resteasy.test.validation.resource;

import javax.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/{s}")
public class ValidationComplexResourceWithArray {
   @Valid
   ValidationComplexArrayOfStrings aos;


   public ValidationComplexResourceWithArray(@PathParam("s") final String s) {
      aos = new ValidationComplexArrayOfStrings(s);
   }

   @POST
   public void post() {
   }
}
