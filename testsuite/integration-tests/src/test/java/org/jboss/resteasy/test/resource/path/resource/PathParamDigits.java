package org.jboss.resteasy.test.resource.path.resource;

import org.junit.Assert;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/digits")
public class PathParamDigits {
    @Path("{id:\\d+}")
    @GET
    public String get(@PathParam("id") int id) {
        Assert.assertEquals("Wrong request parameter", 5150, id);
        return Integer.toString(id);
    }
}
