package org.jboss.resteasy.test.providers.custom.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("")
@Produces("text/plain")
public class CustomContextProviderPreferenceResource {

    @GET
    @Path("test")
    public Response test() {
        return Response.status(CustomContextProviderPreferenceResolver.entered ? 200 : 444).build();
    }
}
