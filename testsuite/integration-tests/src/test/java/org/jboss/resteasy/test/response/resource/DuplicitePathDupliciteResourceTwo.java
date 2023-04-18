package org.jboss.resteasy.test.response.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/b")
public class DuplicitePathDupliciteResourceTwo {
    public static final String DUPLICITE_RESPONSE = "response5";

    @Path("/c")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String dupliciteOne() {
        return DUPLICITE_RESPONSE;
    }
}
