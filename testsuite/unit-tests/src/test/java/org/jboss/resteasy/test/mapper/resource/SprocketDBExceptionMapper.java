package org.jboss.resteasy.test.mapper.resource;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class SprocketDBExceptionMapper implements ExceptionMapper<SprocketDBException> {
   public static int STATUS_CODE = 100102;

   public Response toResponse(SprocketDBException e) {
      return Response.status(STATUS_CODE).entity(e.getMessage()).build();
   }
}
