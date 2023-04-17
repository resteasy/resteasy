package org.jboss.resteasy.test.resource.basic.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;

import org.junit.Assert;

@Path("/query")
public class UriInfoEncodedQueryResource {
    private static final String ERROR_MSG = "Wrong parameter";

    @GET
    public String doGet(@QueryParam("a") String a, @Context UriInfo info) {
        Assert.assertEquals(ERROR_MSG, "a b", a);
        Assert.assertEquals(ERROR_MSG, "a b", info.getQueryParameters().getFirst("a"));
        Assert.assertEquals(ERROR_MSG, "a%20b", info.getQueryParameters(false).getFirst("a"));
        return "content";
    }
}
