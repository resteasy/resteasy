package org.jboss.resteasy.test.asynch.resource;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class AsyncInjectionExceptionMapper implements ExceptionMapper<AsyncInjectionException>
{

   @Override
   public Response toResponse(AsyncInjectionException exception)
   {
      return Response.ok("exception was mapped").status(Status.ACCEPTED).build();
   }

}
