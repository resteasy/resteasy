package org.jboss.resteasy.test.client.proxy.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/user")
@Produces("application/xml")
public interface ProxyJaxbResourceIntf {
    @Path("/{userId}/inventory/credits")
    @GET
    Response getCredits(@PathParam("userId") String userId);
}
