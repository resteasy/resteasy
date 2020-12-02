package org.jboss.resteasy.test.validation.resource;

import javax.validation.constraints.NotNull;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/{s}/{t}")
@ValidationComplexClassConstraint(5)
public class ValidationComplexResourceWithClassConstraint implements ValidationComplexResourceWithClassConstraintInterface {
   @NotNull
   public String s;
   @NotNull
   public String t;

   public ValidationComplexResourceWithClassConstraint(@PathParam("s") final String s, @PathParam("t") final String t) {
      this.s = s;
      this.t = t;
   }

   @POST
   public void post() {
   }

   public String toString() {
      return "ValidationComplexResourceWithClassConstraint(\"" + s + "\", \"" + t + "\")";
   }

   @Override
   public String getS() {
      return s;
   }

   @Override
   public String getT() {
      return t;
   }
}
