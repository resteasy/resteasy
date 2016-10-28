package org.jboss.resteasy.test.exception.resource;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException>
{
   public Response toResponse(NotFoundException exception)
   {
      return Response.status(410).build();
   }
}
