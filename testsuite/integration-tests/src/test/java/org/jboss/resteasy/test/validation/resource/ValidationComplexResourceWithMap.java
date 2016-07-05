package org.jboss.resteasy.test.validation.resource;

import javax.validation.Valid;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

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
