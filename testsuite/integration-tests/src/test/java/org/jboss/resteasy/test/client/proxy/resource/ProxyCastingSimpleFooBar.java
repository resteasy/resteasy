package org.jboss.resteasy.test.client.proxy.resource;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

public interface ProxyCastingSimpleFooBar {
   @Path("{thing}")
   ProxyCastingSimpleInterfaceAorB getThing(@PathParam("thing") String thing);
}
