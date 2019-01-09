package org.jboss.resteasy.test.resource.constructor.resource;

import javax.ws.rs.Path;
import javax.ws.rs.CookieParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.GET;
import javax.ws.rs.core.Response;

/**
 * Force this resource constructor to throw an error so the server can
 * demonstrate it reports the proper status as required by the JAX-RS
 * specification.
 *
 * Any constructor with input params annotated with @HeaderParam or
 * @CookieParam, that fails with an exception other than WebApplicationException
 * must report an HTTP status 400 bad request exception.
 */
@Path("/params400")
public class ConstructorParams400Resource {

    public ConstructorParams400Resource(@HeaderParam("headerP") final String headerP,
                                        @CookieParam("cookieP") final String cookieP) {
        throw new RuntimeException("force an constructor exception");
    }

    @GET
    @Path("get")
    public Response getIt() {
        return Response.ok("successful call").build();
    }
}
