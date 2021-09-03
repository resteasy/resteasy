package org.jboss.resteasy.test.cdi.basic.resource;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class EjbExceptionUnwrapFooExceptionMapper implements ExceptionMapper<EjbExceptionUnwrapFooException> {
   public Response toResponse(EjbExceptionUnwrapFooException exception) {
      return Response.status(409).build();
   }

}
