package org.jboss.resteasy.test.exception.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;

@Path("/test")
public class ExceptionMapperWebRuntimeExceptionResource {
    @GET
    @Produces("text/plain")
    public String get() {
        throw new WebApplicationException(401);
    }

    @GET
    @Path("failure")
    @Produces("text/plain")
    public String getFailure() {
        return "hello";
    }
}
