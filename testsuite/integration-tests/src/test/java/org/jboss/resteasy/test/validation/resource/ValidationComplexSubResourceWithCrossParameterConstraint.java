package org.jboss.resteasy.test.validation.resource;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/{s}/{t}")
public class ValidationComplexSubResourceWithCrossParameterConstraint {
    @POST
    @ValidationComplexCrossParameterConstraint(7)
    public void test(@PathParam("s") int s, @PathParam("t") int t) {
    }
}
