package org.jboss.resteasy.test.resource.resource;

import jakarta.ws.rs.Path;

@Path("locator")
public class SegmentLocatorComplex {
    @Path("responseok")
    public SegmentResourceResponse responseOk() {
        return new SegmentResourceResponse();
    }
}
