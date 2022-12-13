package org.jboss.resteasy.test.resource.path.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

import org.junit.Assert;

@Path("/digits")
public class PathParamDigits {
    @Path("{id:\\d+}")
    @GET
    public String get(@PathParam("id") int id) {
        Assert.assertEquals("Wrong request parameter", 5150, id);
        return Integer.toString(id);
    }
}
