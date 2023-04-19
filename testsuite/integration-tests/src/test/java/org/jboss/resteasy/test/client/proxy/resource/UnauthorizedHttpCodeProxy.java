package org.jboss.resteasy.test.client.proxy.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/regression")
public interface UnauthorizedHttpCodeProxy {
    @GET
    @Produces("application/foo")
    UnauthorizedHttpCodeObject getFoo();
}
