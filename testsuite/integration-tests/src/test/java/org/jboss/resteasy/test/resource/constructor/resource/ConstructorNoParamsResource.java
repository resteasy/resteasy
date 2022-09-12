package org.jboss.resteasy.test.resource.constructor.resource;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.core.Response;

@Path("/noparams")
public class ConstructorNoParamsResource {

    public ConstructorNoParamsResource () {
        throw new RuntimeException("force an constructor exception");
    }

    @GET
    @Path("get")
    public Response getIt() {
        return Response.ok("successful call").build();
    }
}
