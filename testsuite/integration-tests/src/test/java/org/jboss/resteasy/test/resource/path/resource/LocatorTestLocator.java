package org.jboss.resteasy.test.resource.path.resource;

import javax.ws.rs.Path;

@Path("locator")
public class LocatorTestLocator {
    @Path("responseok")
    public LocatorResource responseOk() {
        return new LocatorResource();
    }
}
