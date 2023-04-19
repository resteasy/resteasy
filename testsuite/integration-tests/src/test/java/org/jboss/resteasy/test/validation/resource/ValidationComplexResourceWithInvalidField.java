package org.jboss.resteasy.test.validation.resource;

import javax.validation.constraints.Size;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/")
public class ValidationComplexResourceWithInvalidField {
    @Size(min = 2, max = 4)
    private String s = "abcde";

    @POST
    public void post() {
    }
}
