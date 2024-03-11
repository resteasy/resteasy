package org.jboss.resteasy.test.resource.path.resource;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.PathSegment;

import org.junit.jupiter.api.Assertions;

@Path("/")
public class UriParamsWithLocatorLocator2 {
    @Path("/{id}")
    public UriParamsWithLocatorResource2 get(@PathParam("id") PathSegment id) {
        Assertions.assertEquals("1", id.getPath());
        return new UriParamsWithLocatorResource2();
    }
}
