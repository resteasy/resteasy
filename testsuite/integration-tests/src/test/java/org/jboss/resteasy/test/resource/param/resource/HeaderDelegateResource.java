package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.test.resource.param.HeaderDelegateTest;

@Path("/last")
public class HeaderDelegateResource {
    @GET
    @Produces("text/plain")
    public Response last() {
        return Response.ok().lastModified(HeaderDelegateTest.RIGHT_AFTER_BIG_BANG).build();
    }
}
