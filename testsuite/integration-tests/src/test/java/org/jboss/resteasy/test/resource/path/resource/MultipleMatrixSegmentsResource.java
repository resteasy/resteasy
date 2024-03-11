package org.jboss.resteasy.test.resource.path.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.PathSegment;

import org.junit.jupiter.api.Assertions;

@Path("/")
public class MultipleMatrixSegmentsResource {
    private static final String ERROR_MESSAGE = "Wrong request data";

    @GET
    @Path("/{parent:.*}/children/{child:.*}")
    public String get(@PathParam("parent") PathSegment parent, @PathParam("child") PathSegment child) {
        Assertions.assertEquals("bill", parent.getMatrixParameters().getFirst("name"),
                ERROR_MESSAGE);
        Assertions.assertEquals("111", parent.getMatrixParameters().getFirst("ssn"),
                ERROR_MESSAGE);
        Assertions.assertEquals("skippy", child.getMatrixParameters().getFirst("name"),
                ERROR_MESSAGE);
        Assertions.assertEquals("3344", child.getMatrixParameters().getFirst("ssn"),
                ERROR_MESSAGE);
        return "content";
    }

    @GET
    @Path("/stuff/{segments:.*}/first")
    public String getFirst(@PathParam("segments") PathSegment[] segments) {
        Assertions.assertNotNull(segments, ERROR_MESSAGE);
        Assertions.assertEquals(segments.length, 2, ERROR_MESSAGE);
        Assertions.assertEquals(segments[0].getMatrixParameters().getFirst("name"), "first",
                ERROR_MESSAGE);
        Assertions.assertEquals(segments[1].getMatrixParameters().getFirst("name"), "second",
                ERROR_MESSAGE);
        return "stuff";
    }
}
