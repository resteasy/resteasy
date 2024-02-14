package org.jboss.resteasy.test.resource.basic.resource;

import java.util.List;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.PathSegment;
import jakarta.ws.rs.core.UriInfo;

import org.junit.jupiter.api.Assertions;

@Path("/{a}/{b}")
public class UriInfoEncodedTemplateResource {
    private static final String ERROR_MSG = "Wrong parameter";

    @GET
    public String doGet(@PathParam("a") String a, @PathParam("b") String b, @Context UriInfo info) {
        Assertions.assertEquals("a b", a, ERROR_MSG);
        Assertions.assertEquals("x y", b, ERROR_MSG);
        Assertions.assertEquals("a b", info.getPathParameters().getFirst("a"), ERROR_MSG);
        Assertions.assertEquals("x y", info.getPathParameters().getFirst("b"), ERROR_MSG);
        Assertions.assertEquals("a%20b", info.getPathParameters(false).getFirst("a"), ERROR_MSG);
        Assertions.assertEquals("x%20y", info.getPathParameters(false).getFirst("b"), ERROR_MSG);

        List<PathSegment> decoded = info.getPathSegments(true);
        Assertions.assertEquals(decoded.size(), 2, ERROR_MSG);
        Assertions.assertEquals("a b", decoded.get(0).getPath(), ERROR_MSG);
        Assertions.assertEquals("x y", decoded.get(1).getPath(), ERROR_MSG);

        List<PathSegment> encoded = info.getPathSegments(false);
        Assertions.assertEquals(encoded.size(), 2, ERROR_MSG);
        Assertions.assertEquals("a%20b", encoded.get(0).getPath(), ERROR_MSG);
        Assertions.assertEquals("x%20y", encoded.get(1).getPath(), ERROR_MSG);
        return "content";
    }
}
