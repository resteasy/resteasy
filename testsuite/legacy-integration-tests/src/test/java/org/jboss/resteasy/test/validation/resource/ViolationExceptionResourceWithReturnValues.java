package org.jboss.resteasy.test.validation.resource;

import javax.validation.Valid;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/")
public class ViolationExceptionResourceWithReturnValues {
    @POST
    @Path("/native")
    @Valid
    public ViolationExceptionObject postNative(ViolationExceptionObject foo) {
        return foo;
    }

    @POST
    @Path("/imposed")
    @ViolationExceptionConstraint(min = 3, max = 5)
    public ViolationExceptionObject postImposed(ViolationExceptionObject foo) {
        return foo;
    }

    @POST
    @Path("nativeAndImposed")
    @Valid
    @ViolationExceptionConstraint(min = 3, max = 5)
    public ViolationExceptionObject postNativeAndImposed(ViolationExceptionObject foo) {
        return foo;
    }
}
