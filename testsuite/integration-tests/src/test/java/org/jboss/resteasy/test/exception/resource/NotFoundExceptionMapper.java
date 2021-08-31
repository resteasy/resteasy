package org.jboss.resteasy.test.exception.resource;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException>
{
   public Response toResponse(NotFoundException exception)
   {
      return Response.status(410).build();
   }
}
