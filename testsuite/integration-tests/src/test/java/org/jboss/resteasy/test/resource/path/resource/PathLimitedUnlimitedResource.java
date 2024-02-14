package org.jboss.resteasy.test.resource.path.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

import org.junit.jupiter.api.Assertions;

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
        Assertions.assertEquals(param, expected, "Wrong parameter");
        return "hello world";
    }
}
