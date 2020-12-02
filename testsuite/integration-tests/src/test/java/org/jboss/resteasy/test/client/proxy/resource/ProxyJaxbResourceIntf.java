package org.jboss.resteasy.test.client.proxy.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

@Path("/user")
@Produces("application/xml")
public interface ProxyJaxbResourceIntf {
   @Path("/{userId}/inventory/credits")
   @GET
   Response getCredits(@PathParam("userId") String userId);
}
