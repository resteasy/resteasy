package org.jboss.resteasy.test.validation.resource;

import javax.validation.Valid;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/{s}")
public class ValidationComplexResourceWithArray {
    @Valid
    ValidationComplexArrayOfStrings aos;


    public ValidationComplexResourceWithArray(@PathParam("s") final String s) {
        aos = new ValidationComplexArrayOfStrings(s);
    }

    @POST
    public void post() {
    }
}
