package org.jboss.resteasy.test.resource.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/resource")
public class SegmentResource {
    @GET
    @Path("sub")
    public String get() {
        return null;
    }

    @Path("{id}")
    public SegmentLocator locator() {
        return new SegmentLocator();
    }

}
