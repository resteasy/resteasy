package org.jboss.resteasy.test.core.interceptors.resource;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class PreProcessorExceptionMapperRuntimeExceptionMapper implements ExceptionMapper<RuntimeException> {
   public Response toResponse(RuntimeException exception) {
      return Response.status(412).build();
   }
}
