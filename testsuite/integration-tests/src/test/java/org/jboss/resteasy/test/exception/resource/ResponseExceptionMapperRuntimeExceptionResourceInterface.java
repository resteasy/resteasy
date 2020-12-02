package org.jboss.resteasy.test.exception.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Produces;

public interface ResponseExceptionMapperRuntimeExceptionResourceInterface {

   @GET
   @Produces("text/plain")
   String get();
}
