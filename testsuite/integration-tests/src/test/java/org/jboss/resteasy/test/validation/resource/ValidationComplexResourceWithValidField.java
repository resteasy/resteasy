package org.jboss.resteasy.test.validation.resource;

import javax.validation.constraints.Size;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("/")
public class ValidationComplexResourceWithValidField {
   @Size(min = 2, max = 4)
   private String s = "abc";

   @POST
   public void post() {
   }
}
