package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

import org.junit.jupiter.api.Assertions;

@Path("/")
public class SpecialCharsInUrlResource {

    private static final String decodedPart = "foo+bar gee@foo.com";
    private static final String queryDecodedPart = "foo bar gee@foo.com";

    @Path("/simple/{bar}")
    @GET
    public String get(@PathParam("bar") String pathParam, @QueryParam("foo") String queryParam) {
        Assertions.assertEquals(decodedPart, pathParam);
        Assertions.assertEquals(queryDecodedPart, queryParam);
        return pathParam;
    }
}
