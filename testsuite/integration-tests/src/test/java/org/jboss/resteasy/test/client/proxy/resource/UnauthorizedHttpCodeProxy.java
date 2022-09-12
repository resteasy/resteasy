package org.jboss.resteasy.test.client.proxy.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/regression")
public interface UnauthorizedHttpCodeProxy {
   @GET
   @Produces("application/foo")
   UnauthorizedHttpCodeObject getFoo();
}
