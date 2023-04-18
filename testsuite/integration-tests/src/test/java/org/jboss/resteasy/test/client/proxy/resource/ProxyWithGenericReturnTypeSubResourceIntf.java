package org.jboss.resteasy.test.client.proxy.resource;

import java.util.List;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

public interface ProxyWithGenericReturnTypeSubResourceIntf<T> {
    @GET
    @Path("list")
    @Produces("text/plain")
    List<T> resourceMethod();
}
