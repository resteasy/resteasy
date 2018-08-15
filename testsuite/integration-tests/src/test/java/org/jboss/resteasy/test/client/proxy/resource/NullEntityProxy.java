package org.jboss.resteasy.test.client.proxy.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("hello")
public interface NullEntityProxy {
    @POST
    @Path("entity")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    NullEntityProxyGreeting helloEntity(NullEntityProxyGreeter greeter);
}
