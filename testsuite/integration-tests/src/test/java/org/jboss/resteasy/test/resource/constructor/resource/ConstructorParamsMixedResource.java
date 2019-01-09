package org.jboss.resteasy.test.resource.constructor.resource;

import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.core.Response;

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
