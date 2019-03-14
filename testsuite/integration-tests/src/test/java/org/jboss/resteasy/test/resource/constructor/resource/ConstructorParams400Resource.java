package org.jboss.resteasy.test.resource.constructor.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.CookieParam;
import javax.ws.rs.core.Response;

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
