package org.jboss.resteasy.test.validation.resource;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/")
@ValidationExceptionIncorrectConstraint
public class ValidationExceptionResourceWithIncorrectConstraint {
    @POST
    public void test() {
    }
}
