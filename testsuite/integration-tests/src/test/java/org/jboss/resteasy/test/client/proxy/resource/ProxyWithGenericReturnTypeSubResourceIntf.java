package org.jboss.resteasy.test.client.proxy.resource;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

public interface ProxyWithGenericReturnTypeSubResourceIntf<T> {
    @GET
    @Path("list")
    @Produces("text/plain")
    List<T> resourceMethod();
}
