package org.jboss.resteasy.test.resource.constructor.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

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
