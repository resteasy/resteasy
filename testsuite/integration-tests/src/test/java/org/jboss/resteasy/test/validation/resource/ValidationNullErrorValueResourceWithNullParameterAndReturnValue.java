package org.jboss.resteasy.test.validation.resource;

import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@Path("")
public class ValidationNullErrorValueResourceWithNullParameterAndReturnValue {
    @Path("post")
    @POST
    public void doPost(@NotNull @QueryParam("q") String q) {
    }

    @Path("get")
    @GET
    @NotNull
    public String doGet() {
        return null;
    }
}
