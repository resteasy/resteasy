package org.jboss.resteasy.test.validation.resource;

import javax.validation.Valid;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

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
