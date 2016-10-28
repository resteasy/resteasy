package org.jboss.resteasy.test.providers.custom.resource;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

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
