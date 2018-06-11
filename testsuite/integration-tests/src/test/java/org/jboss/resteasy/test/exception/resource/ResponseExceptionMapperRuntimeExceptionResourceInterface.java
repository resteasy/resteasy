package org.jboss.resteasy.test.exception.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;

public interface ResponseExceptionMapperRuntimeExceptionResourceInterface {

    @GET
    @Produces("text/plain")
    String get();
}
