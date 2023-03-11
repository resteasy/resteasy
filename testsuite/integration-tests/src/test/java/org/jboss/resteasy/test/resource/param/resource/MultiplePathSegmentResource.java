package org.jboss.resteasy.test.resource.param.resource;

import java.util.ArrayList;
import java.util.List;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.PathSegment;
import jakarta.ws.rs.core.Response;

import org.jboss.logging.Logger;

@Path("")
public class MultiplePathSegmentResource {

    private static final Logger LOG = Logger.getLogger(MultiplePathSegmentResource.class);

    @GET
    @Path("{segments:.*}/array/{count}")
    public Response getWildcardArray(@PathParam("segments") PathSegment[] segments, @PathParam("count") int count) {
        return Response.status(segments.length == count ? 200 : 400).build();
    }

    @GET
    @Path("{segments:.*}/list/{count}")
    public Response getWildcardList(@PathParam("segments") List<PathSegment> segments, @PathParam("count") int count) {
        return Response.status(segments.size() == count ? 200 : 400).build();
    }

    @GET
    @Path("{segments:.*}/arraylist/{count}")
    public Response getWildcardArrayList(@PathParam("segments") ArrayList<PathSegment> segments,
            @PathParam("count") int count) {
        return Response.status(segments.size() == count ? 200 : 400).build();
    }

    @GET
    @Path("{segment}/{segment}/array")
    public Response getTwoSegmentsArray(@PathParam("segment") PathSegment[] segments) {
        LOG.info("array segments: " + segments.length);
        return Response.status(segments.length == 2 ? 200 : 400).build();
    }

    @GET
    @Path("{segment}/{segment}/list")
    public Response getTwoSegmentsList(@PathParam("segment") List<PathSegment> segments) {
        LOG.info("array segments: " + segments.size());
        return Response.status(segments.size() == 2 ? 200 : 400).build();
    }

    @GET
    @Path("{segment}/{segment}/arraylist")
    public Response getTwoSegmentsArrayList(@PathParam("segment") ArrayList<PathSegment> segments) {
        LOG.info("array segments: " + segments.size());
        return Response.status(segments.size() == 2 ? 200 : 400).build();
    }
}
