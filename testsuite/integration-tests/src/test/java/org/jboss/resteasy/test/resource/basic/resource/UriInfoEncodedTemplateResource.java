package org.jboss.resteasy.test.resource.basic.resource;

import java.util.List;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.PathSegment;
import jakarta.ws.rs.core.UriInfo;

import org.junit.Assert;

@Path("/{a}/{b}")
public class UriInfoEncodedTemplateResource {
    private static final String ERROR_MSG = "Wrong parameter";

    @GET
    public String doGet(@PathParam("a") String a, @PathParam("b") String b, @Context UriInfo info) {
        Assert.assertEquals(ERROR_MSG, "a b", a);
        Assert.assertEquals(ERROR_MSG, "x y", b);
        Assert.assertEquals(ERROR_MSG, "a b", info.getPathParameters().getFirst("a"));
        Assert.assertEquals(ERROR_MSG, "x y", info.getPathParameters().getFirst("b"));
        Assert.assertEquals(ERROR_MSG, "a%20b", info.getPathParameters(false).getFirst("a"));
        Assert.assertEquals(ERROR_MSG, "x%20y", info.getPathParameters(false).getFirst("b"));

        List<PathSegment> decoded = info.getPathSegments(true);
        Assert.assertEquals(ERROR_MSG, decoded.size(), 2);
        Assert.assertEquals(ERROR_MSG, "a b", decoded.get(0).getPath());
        Assert.assertEquals(ERROR_MSG, "x y", decoded.get(1).getPath());

        List<PathSegment> encoded = info.getPathSegments(false);
        Assert.assertEquals(ERROR_MSG, encoded.size(), 2);
        Assert.assertEquals(ERROR_MSG, "a%20b", encoded.get(0).getPath());
        Assert.assertEquals(ERROR_MSG, "x%20y", encoded.get(1).getPath());
        return "content";
    }
}
