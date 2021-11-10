package org.jboss.resteasy.test.exception.resource;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

public class ExceptionMapperWebRuntimeExceptionMapper implements ExceptionMapper<WebApplicationException> {
   public Response toResponse(WebApplicationException exception) {
      return Response.status(Response.Status.PRECONDITION_FAILED).header("custom", "header").build();
   }
}
