package org.jboss.resteasy.test.validation.resource;

import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("/")
public class ValidationComplexResourceWithReturnValues {
    @POST
    @Path("/native")
    @Valid
    public ValidationComplexFoo postNative(ValidationComplexFoo foo) {
        return foo;
    }

    @POST
    @Path("/imposed")
    @ValidationComplexFooConstraint(min = 3, max = 5)
    public ValidationComplexFoo postImposed(ValidationComplexFoo foo) {
        return foo;
    }

    @POST
    @Path("nativeAndImposed")
    @Valid
    @ValidationComplexFooConstraint(min = 3, max = 5)
    public ValidationComplexFoo postNativeAndImposed(ValidationComplexFoo foo) {
        return foo;
    }
}
