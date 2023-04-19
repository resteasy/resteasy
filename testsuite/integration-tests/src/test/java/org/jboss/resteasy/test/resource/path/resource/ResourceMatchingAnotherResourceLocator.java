package org.jboss.resteasy.test.resource.path.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

public class ResourceMatchingAnotherResourceLocator {

    @GET
    public String get() {
        return getClass().getSimpleName();
    }

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String post() {
        return get();
    }

    @DELETE
    public String delete() {
        return get();
    }
}
