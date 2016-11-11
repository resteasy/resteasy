package org.jboss.resteasy.test.client.proxy.resource;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

public interface ProxyCastingSimpleFooBar {
    @Path("{thing}")
    ProxyCastingSimpleInterfaceAorB getThing(@PathParam("thing") String thing);
}
