package org.jboss.resteasy.test.client.proxy.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/regression")
public class UnauthorizedHttpCodeResource {
    @GET
    public Response get() {
        return Response.status(401).entity("hello").type("application/error").build();
    }
}
