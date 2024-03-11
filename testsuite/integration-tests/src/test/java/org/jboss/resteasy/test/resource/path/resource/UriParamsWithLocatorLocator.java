package org.jboss.resteasy.test.resource.path.resource;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

import org.junit.jupiter.api.Assertions;

@Path("/")
public class UriParamsWithLocatorLocator {
    @Path("/{id}")
    public UriParamsWithLocatorResource get(@PathParam("id") String id) {
        Assertions.assertEquals("1", id);
        return new UriParamsWithLocatorResource();

    }
}
