package org.jboss.resteasy.test.validation.resource;

import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("/")
public class ValidationCounterResource {
    @POST
    @Path("/count")
    public void postNative(@Valid ValidationCounter foo) {
    }
}
