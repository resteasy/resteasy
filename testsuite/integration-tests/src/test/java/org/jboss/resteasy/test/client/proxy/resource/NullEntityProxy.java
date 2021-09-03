package org.jboss.resteasy.test.client.proxy.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("hello")
public interface NullEntityProxy {
   @POST
   @Path("entity")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   NullEntityProxyGreeting helloEntity(NullEntityProxyGreeter greeter);
}
