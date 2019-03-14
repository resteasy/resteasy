package org.jboss.resteasy.test.resource.constructor.resource;

import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.core.Response;

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
