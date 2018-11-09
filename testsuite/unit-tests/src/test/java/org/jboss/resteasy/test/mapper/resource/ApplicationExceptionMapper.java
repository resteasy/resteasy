package org.jboss.resteasy.test.mapper.resource;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.jboss.resteasy.spi.ApplicationException;

@Provider
public class ApplicationExceptionMapper implements ExceptionMapper<ApplicationException> {
   public static int STATUS_CODE = 100202;

   public Response toResponse(ApplicationException e) {
      return Response.status(STATUS_CODE).entity(e.getMessage()).build();
   }
}

