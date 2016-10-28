package org.jboss.resteasy.test.resource.path.resource;

import org.junit.Assert;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

@Path("/")
public class PathLimitedLocatorUriResource {
    @Path("/locator3/unlimited")
    public Object get() {
        return new PathLimitedUnlimitedResource();
    }

    @Path("/locator3/uriparam/{param}")
    public Object uriParam(@PathParam("param") String param, @QueryParam("firstExpected") String expected) {
        Assert.assertEquals("Wrong parameter", param, expected);
        return new PathLimitedUnlimitedResource();
    }
}
