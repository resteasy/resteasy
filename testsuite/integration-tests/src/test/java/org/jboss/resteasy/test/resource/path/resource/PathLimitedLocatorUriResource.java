package org.jboss.resteasy.test.resource.path.resource;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

import org.junit.jupiter.api.Assertions;

@Path("/")
public class PathLimitedLocatorUriResource {
    @Path("/locator3/unlimited")
    public Object get() {
        return new PathLimitedUnlimitedResource();
    }

    @Path("/locator3/uriparam/{param}")
    public Object uriParam(@PathParam("param") String param, @QueryParam("firstExpected") String expected) {
        Assertions.assertEquals(param, expected, "Wrong parameter");
        return new PathLimitedUnlimitedResource();
    }
}
