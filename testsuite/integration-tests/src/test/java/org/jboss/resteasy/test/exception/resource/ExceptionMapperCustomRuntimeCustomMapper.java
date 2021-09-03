package org.jboss.resteasy.test.exception.resource;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

public class ExceptionMapperCustomRuntimeCustomMapper implements ExceptionMapper<ExceptionMapperCustomRuntimeException> {
   public Response toResponse(ExceptionMapperCustomRuntimeException exception) {
      return Response.status(Response.Status.PRECONDITION_FAILED).header("custom", "header").entity("My custom message").build();
   }
}
