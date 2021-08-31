package org.jboss.resteasy.test.client.proxy.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

public interface ProxyCastingInterfaceB {
   @GET
   @Path("bar")
   @Produces("text/plain")
   String getBar();
}
