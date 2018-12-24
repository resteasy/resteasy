package org.jboss.resteasy.test.resource.constructor.resource;

import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.core.Response;

/**
 * Force this resource constructor to throw an error so the server can
 * demonstrate it reports the proper status as required by the JAX-RS
 * specification.
 *
 * Any constructor with input params annotated with @MatrixParam, @QueryParam
 * or @PathParam, that fails with an exception other than WebApplicationException
 * must report an HTTP status 404 not found exception.
 */
@Path("/params404")
public class ConstructorParams404Resource {

    public ConstructorParams404Resource(@QueryParam("queryP") final String queryP,
                                        @PathParam("pathP") final String pathP,
                                        @MatrixParam("matrixP") final String matrixP) {
        throw new RuntimeException("force an constructor exception");
    }

    @GET
    @Path("get")
    public Response getIt() {
        return Response.ok("successful call").build();
    }
}
