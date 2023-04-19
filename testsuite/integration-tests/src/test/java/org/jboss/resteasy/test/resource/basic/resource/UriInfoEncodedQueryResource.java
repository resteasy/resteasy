package org.jboss.resteasy.test.resource.basic.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

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
