package org.jboss.resteasy.test.validation.resource;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("/")
public class ValidationExceptionResourceWithOther {
    @PathParam("s")
    @ValidationExceptionOtherConstraint
    String s;

    @POST
    @Path("parameter/{s}")
    public Response testParameter(@ValidationExceptionOtherConstraint String s) {
        return Response.ok().build();
    }

    @POST
    @Path("return/{s}")
    @ValidationExceptionOtherConstraint
    public String testReturnValue() {
        return "abc";
    }

    @GET
    @Path("execution/{s}")
    public void testExecution() {
        throw new ValidationExceptionOtherValidationException(new ValidationExceptionOtherValidationException2(new ValidationExceptionOtherValidationException3()));
    }
}
