package org.jboss.resteasy.test.exception.resource;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

public class ExceptionMapperCustomRuntimeMapper implements ExceptionMapper<RuntimeException> {
   public Response toResponse(RuntimeException exception) {
      return Response.serverError().build();
   }
}
