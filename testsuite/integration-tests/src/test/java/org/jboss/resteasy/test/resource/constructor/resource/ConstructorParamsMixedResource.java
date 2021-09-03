package org.jboss.resteasy.test.resource.constructor.resource;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.core.Response;

@Path("/mixed")
public class ConstructorParamsMixedResource {

    public ConstructorParamsMixedResource (
            @CookieParam("cookieP") final String cookieP,
            @QueryParam("queryP") final String queryP) {
        throw new RuntimeException("force an constructor exception");
    }

    @GET
    @Path("get")
    public Response getIt() {
        return Response.ok("successful call").build();
    }
}
