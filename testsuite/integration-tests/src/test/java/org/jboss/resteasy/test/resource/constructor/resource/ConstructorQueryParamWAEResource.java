package org.jboss.resteasy.test.resource.constructor.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

@Path("/paramsWAEQuery")
public class ConstructorQueryParamWAEResource {

    public ConstructorQueryParamWAEResource(@QueryParam("queryP") final Item2 queryP) {
        throw new RuntimeException("force an constructor exception");
    }
    @GET
    @Path("get")
    public Response getIt() {
        return Response.ok("successful call").build();
    }
}
