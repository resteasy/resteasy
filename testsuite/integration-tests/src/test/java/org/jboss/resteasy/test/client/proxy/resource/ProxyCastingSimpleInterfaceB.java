package org.jboss.resteasy.test.client.proxy.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

public interface ProxyCastingSimpleInterfaceB extends ProxyCastingSimpleInterfaceAorB {
   @GET
   @Path("bar")
   @Produces("text/plain")
   String getBar();
}
