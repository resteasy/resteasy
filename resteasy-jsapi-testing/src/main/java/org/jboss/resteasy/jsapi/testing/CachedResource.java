package org.jboss.resteasy.jsapi.testing;

import org.jboss.resteasy.annotations.GZIP;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.*;

/**
 * 10 02 2012
 *
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
@Path("cached")
public class CachedResource {

    @GET
    @GZIP
    @Path("{uuid}")
    public Response get(@Context HttpServletRequest servletRequest, @Context Request request, @PathParam("uuid") String uuid) throws Exception {
        EntityTag tag = new EntityTag(Integer.toString(Math.abs(uuid.hashCode())));
        Response.ResponseBuilder builder = request.evaluatePreconditions(tag);

        if (builder != null) {
            return builder.build();
        } else {
            builder = Response.ok(uuid, MediaType.TEXT_PLAIN);
            builder.tag(tag);
            return builder.build();
        }
    }
}
