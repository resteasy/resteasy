package org.jboss.resteasy.test.resource.path.resource;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("locator")
public class LocatorTestLocator {
    @Path("responseok")
    public LocatorResource responseOk() {
        return new LocatorResource();
    }

    @Path("{name: (?:resource2|RESOURCE2)}")
    public LocatorResource nonCapturingPath(@PathParam ("name") String name) {
        return new LocatorResource();
    }

    @Path("{name: (resource3|RESOURCE3)}")
    public LocatorResource capturingPath(@PathParam ("name") String name) {
        return new LocatorResource();
    }
}
