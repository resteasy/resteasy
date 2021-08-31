package org.jboss.resteasy.test.resource.constructor.resource;

import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/paramsWAECookie")
public class ConstructorCookieParamWAEResource {

    public ConstructorCookieParamWAEResource(@CookieParam("cookieP") final Item2 cookieP) {
        throw new RuntimeException("force an constructor exception");
    }
    @GET
    @Path("get")
    public Response getIt() {
        return Response.ok("successful call").build();
    }
}
