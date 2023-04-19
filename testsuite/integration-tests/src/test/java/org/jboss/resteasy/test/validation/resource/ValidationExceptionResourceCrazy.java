package org.jboss.resteasy.test.validation.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
@ValidationExceptionCrazyConstraint
public class ValidationExceptionResourceCrazy {
    private String s;

    @GET
    public String test() {
        return s;
    }
}
