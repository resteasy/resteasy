package org.jboss.resteasy.test.resource.param.resource;

import org.junit.Assert;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

@Path("/")
public class SpecialCharsInUrlResource {

    private static final String decodedPart = "foo+bar gee@foo.com";
    private static final String queryDecodedPart = "foo bar gee@foo.com";

    @Path("/simple/{bar}")
    @GET
    public String get(@PathParam("bar") String pathParam, @QueryParam("foo") String queryParam) {
        Assert.assertEquals(decodedPart, pathParam);
        Assert.assertEquals(queryDecodedPart, queryParam);
        return pathParam;
    }
}
