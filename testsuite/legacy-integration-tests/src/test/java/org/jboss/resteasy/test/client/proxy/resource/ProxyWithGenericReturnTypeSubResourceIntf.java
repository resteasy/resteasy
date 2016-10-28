package org.jboss.resteasy.test.client.proxy.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.List;

public interface ProxyWithGenericReturnTypeSubResourceIntf<T> {
    @GET
    @Path("list")
    @Produces("text/plain")
    List<T> resourceMethod();
}
