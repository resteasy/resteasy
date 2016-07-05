package org.jboss.resteasy.test.resource.resource;

import javax.ws.rs.GET;

public class SegmentLocatorSimple {
    @GET
    public String ok() {
        return "ok";
    }
}
