package org.jboss.resteasy.test.exception.resource;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ExceptionMapperRuntimeExceptionMapper implements ExceptionMapper<RuntimeException> {

   @Override
   public Response toResponse(RuntimeException exception) {
      return Response.status(Response.Status.NOT_ACCEPTABLE).build();
   }
}
