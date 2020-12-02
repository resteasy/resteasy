package org.jboss.resteasy.test.validation.resource;

import javax.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/{s}")
public class ValidationComplexResourceWithList {
   @Valid
   ValidationComplexListOfStrings los;

   public ValidationComplexResourceWithList(@PathParam("s") final String s) {
      los = new ValidationComplexListOfStrings(s);
   }

   @POST
   public void post() {
   }
}
