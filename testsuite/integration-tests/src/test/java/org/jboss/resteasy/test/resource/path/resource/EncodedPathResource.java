package org.jboss.resteasy.test.resource.path.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import org.jboss.logging.Logger;

@Path("/")
public class EncodedPathResource {

    private static final Logger LOG = Logger.getLogger(EncodedPathResource.class);

    @Path("/hello world")
    @GET
    public String get() {
        LOG.info("Hello");
        return "HELLO";
    }

    @Path("/goodbye%7Bworld")
    @GET
    public String goodbye() {
        LOG.info("Goodbye");
        return "GOODBYE";
    }
}
