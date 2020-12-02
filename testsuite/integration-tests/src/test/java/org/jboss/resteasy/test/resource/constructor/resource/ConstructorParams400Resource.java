package org.jboss.resteasy.test.resource.constructor.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.core.Response;

@Path("/params400")
public class ConstructorParams400Resource {

    public ConstructorParams400Resource(@CookieParam("cookieP") final Item cookieP) {
        throw new RuntimeException("force an constructor exception");
    }
    @GET
    @Path("get")
    public Response getIt() {
        return Response.ok("successful call").build();
    }
}
