package org.jboss.resteasy.test.response.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/test")
public class NullSetEntityTestResource {

    @GET
    @Path("/nonNull")
    public Integer getNonNull() {
        return 42;
    }

    @GET
    @Path("/null")
    public Integer getNull() {
        return null;
    }

    @GET
    @Path("/void")
    public void getVoid() {
    }
}

