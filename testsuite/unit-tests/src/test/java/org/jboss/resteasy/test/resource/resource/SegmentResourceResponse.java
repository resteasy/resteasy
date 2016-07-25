package org.jboss.resteasy.test.resource.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("resource")
public class SegmentResourceResponse {
    @GET
    @Path("responseok")
    public String responseOk() {
        return "ok";
    }

    @Path("{id}")
    public Object locate(@PathParam("id") int id) {
        return new SegmentLocatorSimple();
    }
}
