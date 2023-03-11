package org.jboss.resteasy.test.providers.custom.resource;

import java.util.List;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.PathSegment;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Path("resource")
public class ResponseContainerResource {

    @Context
    UriInfo info;

    @POST
    @Path("hasentity")
    public Response hasEntity(String entity) {
        Response.ResponseBuilder builder = createResponseWithHeader();
        if (entity != null && entity.length() != 0) {
            builder = builder.entity(entity);
        }
        Response response = builder.build();
        return response;
    }

    private Response.ResponseBuilder createResponseWithHeader() {
        // get value of @Path(value)
        List<PathSegment> segments = info.getPathSegments();
        PathSegment last = segments.get(segments.size() - 1);
        // convert the value to ContextOperation
        Response.ResponseBuilder builder = Response.ok();
        // set a header with ContextOperation so that the filter knows what to do
        builder = builder.header(ResponseContainerResponseFilter.OPERATION, last.getPath()
                .toUpperCase());
        return builder;
    }

}
