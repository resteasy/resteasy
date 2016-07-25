package org.jboss.resteasy.test.validation.resource;

import javax.validation.constraints.Size;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("all")
@ValidationCoreClassConstraint(5)
public class ValidationCoreResourceWithAllViolationTypes {
    @Size(min = 2, max = 4)
    @PathParam("s")
    String s;

    private String t;

    @Size(min = 3, max = 5)
    public String getT() {
        return t;
    }

    public String retrieveS() {
        return s;
    }

    @PathParam("t")
    public void setT(String t) {
        this.t = t;
    }

    @POST
    @Path("{s}/{t}")
    @ValidationCoreFooConstraint(min = 4, max = 5)
    public ValidationCoreFoo post(@ValidationCoreFooConstraint(min = 3, max = 5) ValidationCoreFoo foo, @PathParam("s") String s) {
        return foo;
    }
}
