package org.jboss.resteasy.test.asynch.resource;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class AsyncInjectionExceptionMapper implements ExceptionMapper<AsyncInjectionException>
{

   @Override
   public Response toResponse(AsyncInjectionException exception)
   {
      return Response.ok("exception was mapped").status(Status.ACCEPTED).build();
   }

}
