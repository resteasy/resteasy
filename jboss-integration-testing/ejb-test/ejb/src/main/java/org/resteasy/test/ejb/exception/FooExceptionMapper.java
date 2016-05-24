package org.resteasy.test.ejb.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class FooExceptionMapper implements ExceptionMapper<FooException>
{
   public Response toResponse(FooException exception)
   {
      return Response.status(409).build();
   }

}
