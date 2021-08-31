package org.jboss.resteasy.test.validation.resource;

import javax.validation.constraints.Size;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/{s}/{t}")
@ValidationComplexClassConstraint2(5)
public class ValidationComplexResourceWithAllFivePotentialViolations {
   @Size(min = 2, max = 4)
   @PathParam("s")
   public String s;

   public String t;

   @Size(min = 3, max = 5)
   public String getT() {
      return t;
   }

   @PathParam("t")
   public void setT(String t) {
      this.t = t;
   }

   @POST
   @Path("{unused}/{unused}")
   @ValidationComplexFooConstraint(min = 4, max = 5)
   public ValidationComplexFoo post(@ValidationComplexFooConstraint(min = 3, max = 5) ValidationComplexFoo foo) {
      return foo;
   }
}
