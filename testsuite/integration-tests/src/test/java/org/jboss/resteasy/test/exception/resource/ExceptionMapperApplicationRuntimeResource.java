package org.jboss.resteasy.test.exception.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/test")
public class ExceptionMapperApplicationRuntimeResource {
    @GET
    @Produces("text/plain")
    public String get() {
        throw new ExceptionMapperCustomRuntimeException();
    }
}
