package org.jboss.resteasy.test.validation.resource;

import javax.validation.Valid;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

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
