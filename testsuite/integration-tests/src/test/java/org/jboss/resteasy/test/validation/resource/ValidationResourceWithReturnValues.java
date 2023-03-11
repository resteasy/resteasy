package org.jboss.resteasy.test.validation.resource;

import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("/return")
public class ValidationResourceWithReturnValues {
    @POST
    @Path("/native")
    @Valid
    public ValidationFoo postNative(ValidationFoo validationFoo) {
        return validationFoo;
    }

    @POST
    @Path("/imposed")
    @ValidationFooConstraint(min = 3, max = 5)
    public ValidationFoo postImposed(ValidationFoo validationFoo) {
        return validationFoo;
    }

    @POST
    @Path("nativeAndImposed")
    @Valid
    @ValidationFooConstraint(min = 3, max = 5)
    public ValidationFoo postNativeAndImposed(ValidationFoo validationFoo) {
        return validationFoo;
    }
}
