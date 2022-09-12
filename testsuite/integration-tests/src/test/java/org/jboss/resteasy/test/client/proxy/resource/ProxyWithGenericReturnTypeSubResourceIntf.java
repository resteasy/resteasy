package org.jboss.resteasy.test.client.proxy.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import java.util.List;

public interface ProxyWithGenericReturnTypeSubResourceIntf<T> {
   @GET
   @Path("list")
   @Produces("text/plain")
   List<T> resourceMethod();
}
