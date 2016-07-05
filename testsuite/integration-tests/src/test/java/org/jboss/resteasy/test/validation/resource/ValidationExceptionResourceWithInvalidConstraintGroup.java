package org.jboss.resteasy.test.validation.resource;

import javax.validation.GroupSequence;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
@GroupSequence({ValidationExceptionTestGroup1.class, ValidationExceptionTestGroup2.class})
public class ValidationExceptionResourceWithInvalidConstraintGroup {
    private String s;

    @GET
    public String test() {
        return s;
    }
}
