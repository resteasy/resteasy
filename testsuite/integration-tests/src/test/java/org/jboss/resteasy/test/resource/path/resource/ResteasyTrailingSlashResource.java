package org.jboss.resteasy.test.resource.path.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public class ResteasyTrailingSlashResource {
    @GET
    @Path("/test/")
    @Produces(MediaType.TEXT_PLAIN)
    public String get() {
        return "hello world";
    }
}
