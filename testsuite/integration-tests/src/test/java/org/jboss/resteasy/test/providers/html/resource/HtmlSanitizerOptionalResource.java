package org.jboss.resteasy.test.providers.html.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.providers.html.HtmlSanitizerOptionalTest;

@Path("")
public class HtmlSanitizerOptionalResource {

    @Path("test")
    @GET
    @Produces("text/html")
    public Response test() {
        return Response.status(HttpResponseCodes.SC_BAD_REQUEST).entity(HtmlSanitizerOptionalTest.input).build();
    }
}