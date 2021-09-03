package org.jboss.resteasy.test.resource.constructor.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

@Path("/params404")
public class ConstructorParams404Resource {

    public ConstructorParams404Resource(@QueryParam("queryP") final Item queryP) {
        throw new RuntimeException("force an constructor exception");
    }
    @GET
    @Path("get")
    public Response getIt() {
        return Response.ok("successful call").build();
    }
}
