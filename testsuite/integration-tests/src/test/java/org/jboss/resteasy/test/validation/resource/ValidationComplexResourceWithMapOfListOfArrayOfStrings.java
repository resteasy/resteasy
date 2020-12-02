package org.jboss.resteasy.test.validation.resource;

import javax.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/{s}")
public class ValidationComplexResourceWithMapOfListOfArrayOfStrings {
   @Valid
   ValidationComplexMapOfListOfArrayOfStrings mlas;

   public ValidationComplexResourceWithMapOfListOfArrayOfStrings(@PathParam("s") final String s) {
      mlas = new ValidationComplexMapOfListOfArrayOfStrings(s);
   }

   @POST
   public void post() {
   }
}
