package org.jboss.resteasy.test.resource.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/resource")
public class SegmentResourceSwitch {
    @GET
    @Path("{id}")
    public String get() {
        return null;
    }

    @Path("sub")
    public SegmentLocator locator() {
        return new SegmentLocator();
    }

}
