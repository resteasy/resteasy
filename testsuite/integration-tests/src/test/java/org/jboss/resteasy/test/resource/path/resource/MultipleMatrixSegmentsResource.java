package org.jboss.resteasy.test.resource.path.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.PathSegment;

import org.junit.Assert;

@Path("/")
public class MultipleMatrixSegmentsResource {
    private static final String ERROR_MESSAGE = "Wrong request data";

    @GET
    @Path("/{parent:.*}/children/{child:.*}")
    public String get(@PathParam("parent") PathSegment parent, @PathParam("child") PathSegment child) {
        Assert.assertEquals(ERROR_MESSAGE, "bill", parent.getMatrixParameters().getFirst("name"));
        Assert.assertEquals(ERROR_MESSAGE, "111", parent.getMatrixParameters().getFirst("ssn"));
        Assert.assertEquals(ERROR_MESSAGE, "skippy", child.getMatrixParameters().getFirst("name"));
        Assert.assertEquals(ERROR_MESSAGE, "3344", child.getMatrixParameters().getFirst("ssn"));
        return "content";
    }

    @GET
    @Path("/stuff/{segments:.*}/first")
    public String getFirst(@PathParam("segments") PathSegment[] segments) {
        Assert.assertNotNull(ERROR_MESSAGE, segments);
        Assert.assertEquals(ERROR_MESSAGE, segments.length, 2);
        Assert.assertEquals(ERROR_MESSAGE, segments[0].getMatrixParameters().getFirst("name"), "first");
        Assert.assertEquals(ERROR_MESSAGE, segments[1].getMatrixParameters().getFirst("name"), "second");
        return "stuff";
    }
}
