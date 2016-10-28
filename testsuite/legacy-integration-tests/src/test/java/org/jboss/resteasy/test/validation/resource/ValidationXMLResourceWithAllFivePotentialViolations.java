package org.jboss.resteasy.test.validation.resource;

import javax.validation.constraints.Size;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/{s}/{t}/{u}")
@ValidationXMLClassConstraint(5)
public class ValidationXMLResourceWithAllFivePotentialViolations {
    @Size(min = 2, max = 4)
    @PathParam("s")
    public String s;

    @Size(min = 2, max = 4)
    @PathParam("t")
    public String t;

    public String u;

    @Size(min = 3, max = 5)
    public String getU() {
        return u;
    }

    @PathParam("u")
    public void setU(String u) {
        this.u = u;
    }

    @POST
    @ValidationXMLFooConstraint(min = 4, max = 5)
    public ValidationXMLFoo post(@ValidationXMLFooConstraint(min = 3, max = 5) ValidationXMLFoo foo) {
        return foo;
    }
}
