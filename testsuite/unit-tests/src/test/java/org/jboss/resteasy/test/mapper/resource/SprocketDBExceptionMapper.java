package org.jboss.resteasy.test.mapper.resource;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class SprocketDBExceptionMapper implements ExceptionMapper<SprocketDBException> {
   public static int STATUS_CODE = 100102;

   public Response toResponse(SprocketDBException e) {
      return Response.status(STATUS_CODE).entity(e.getMessage()).build();
   }
}
