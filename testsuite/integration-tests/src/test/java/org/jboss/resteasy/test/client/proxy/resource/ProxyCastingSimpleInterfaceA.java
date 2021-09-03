package org.jboss.resteasy.test.client.proxy.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

public interface ProxyCastingSimpleInterfaceA extends ProxyCastingSimpleInterfaceAorB {
   @GET
   @Path("foo")
   @Produces("text/plain")
   String getFoo();
}
