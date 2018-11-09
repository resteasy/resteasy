package org.jboss.resteasy.test.providers.html.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

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