package org.jboss.resteasy.test.response.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/b")
public class DuplicitePathDupliciteResourceOne {
    public static final String DUPLICITE_RESPONSE = "response4";

    @Path("/c")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String duplicite() {
        return DUPLICITE_RESPONSE;
    }
}
