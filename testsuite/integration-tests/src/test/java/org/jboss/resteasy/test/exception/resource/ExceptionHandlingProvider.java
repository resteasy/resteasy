package org.jboss.resteasy.test.exception.resource;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ExceptionHandlingProvider implements ExceptionMapper<Exception> {
   public Response toResponse(Exception exception) {
      return Response.serverError().entity("Expected exception raised").build();
   }
}
