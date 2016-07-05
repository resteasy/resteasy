package org.jboss.resteasy.test.resource.path.resource;

import org.junit.Assert;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

@Path("/")
public class PathLimitedUnlimitedResource {
    @Path("/unlimited2/{p:.*}")
    @GET
    public String hello() {
        return "hello world";
    }

    @Path(value = "/uriparam/{param:.*}")
    @GET
    public String get(@PathParam("param") String param, @QueryParam("expected") String expected) {
        Assert.assertEquals("Wrong parameter", param, expected);
        return "hello world";
    }
}
