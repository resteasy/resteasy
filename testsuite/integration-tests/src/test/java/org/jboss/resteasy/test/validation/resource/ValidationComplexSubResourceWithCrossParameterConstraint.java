package org.jboss.resteasy.test.validation.resource;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/{s}/{t}")
public class ValidationComplexSubResourceWithCrossParameterConstraint {
   @POST
   @ValidationComplexCrossParameterConstraint(7)
   public void test(@PathParam("s") int s, @PathParam("t") int t) {
   }
}
