package org.jboss.resteasy.test.resource.path.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

import org.junit.jupiter.api.Assertions;

public class UriParamsWithLocatorResource {
    @GET
    @Path("/{id}")
    public String get(@PathParam("id") String id) {
        Assertions.assertEquals("2", id);
        return id;
    }
}
