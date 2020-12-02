package org.jboss.resteasy.test.validation.resource;

import javax.validation.constraints.Size;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("/")
public class ValidationComplexResourceWithInvalidField {
   @Size(min = 2, max = 4)
   private String s = "abcde";

   @POST
   public void post() {
   }
}
