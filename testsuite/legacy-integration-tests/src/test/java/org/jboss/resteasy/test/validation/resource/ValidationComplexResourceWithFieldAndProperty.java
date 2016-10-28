package org.jboss.resteasy.test.validation.resource;

import javax.validation.constraints.Size;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/{s}/{t}")
public class ValidationComplexResourceWithFieldAndProperty {
    @Size(min = 2, max = 4)
    @PathParam("s")
    private String s;

    private String t;

    @Size(min = 3, max = 5)
    public String getT() {
        return t;
    }

    @PathParam("t")
    public void setT(String t) {
        this.t = t;
    }

    @POST
    public void post() {
    }
}
