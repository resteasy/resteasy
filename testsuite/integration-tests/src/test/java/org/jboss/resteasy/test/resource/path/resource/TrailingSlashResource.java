package org.jboss.resteasy.test.resource.path.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("test")
public class TrailingSlashResource {
    @Context
    private UriInfo uriInfo;

    @GET
    @Produces("text/plain")
    public Response test() {
        return Response.ok(uriInfo.getPath()).build();
    }
}
