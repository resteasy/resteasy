package org.jboss.resteasy.test.resource.path.resource;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.junit.Assert;

@Path("/")
public class PathLimitedLocatorResource {
    @Path(value = "/locator{p:.*}")
    public Object get() {
        return new PathLimitedBasicResource();
    }

    @Path(value = "/locator2/{param:.*}")
    public Object get(@PathParam("param") String param, @QueryParam("expected") String expected) {
        Assert.assertEquals("Wrong parameter", param, expected);
        return new PathLimitedBasicResource();
    }
}
