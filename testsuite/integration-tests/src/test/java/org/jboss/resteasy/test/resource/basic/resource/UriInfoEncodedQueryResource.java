package org.jboss.resteasy.test.resource.basic.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;

import org.junit.jupiter.api.Assertions;

@Path("/query")
public class UriInfoEncodedQueryResource {
    private static final String ERROR_MSG = "Wrong parameter";

    @GET
    public String doGet(@QueryParam("a") String a, @Context UriInfo info) {
        Assertions.assertEquals("a b", a, ERROR_MSG);
        Assertions.assertEquals("a b", info.getQueryParameters().getFirst("a"), ERROR_MSG);
        Assertions.assertEquals("a%20b", info.getQueryParameters(false).getFirst("a"), ERROR_MSG);
        return "content";
    }
}
