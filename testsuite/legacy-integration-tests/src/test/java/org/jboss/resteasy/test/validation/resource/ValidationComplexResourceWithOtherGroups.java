package org.jboss.resteasy.test.validation.resource;

import javax.validation.constraints.Size;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/")
@ValidationComplexOtherGroupConstraint(groups = ValidationComplexOtherGroup.class)
public class ValidationComplexResourceWithOtherGroups {
    @Size(min = 2, groups = ValidationComplexOtherGroup.class)
    String s = "abc";

    String t;

    @POST
    @Path("test/{t}/{u}")
    @Size(min = 2, groups = {ValidationComplexOtherGroup.class})
    public String test(@Size(min = 2, groups = ValidationComplexOtherGroup.class) @PathParam("u") String u) {
        return u;
    }

    @PathParam("t")
    public void setT(String t) {
        this.t = t;
    }

    @Size(min = 2, groups = ValidationComplexOtherGroup.class)
    public String getT() {
        return t;
    }
}
