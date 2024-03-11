package org.jboss.resteasy.test.resource.path.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

import org.junit.jupiter.api.Assertions;

@Path("/digits")
public class PathParamDigits {
    @Path("{id:\\d+}")
    @GET
    public String get(@PathParam("id") int id) {
        Assertions.assertEquals(5150, id, "Wrong request parameter");
        return Integer.toString(id);
    }
}
