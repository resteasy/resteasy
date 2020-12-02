package org.jboss.resteasy.test.asynch.resource;


import jakarta.ws.rs.ServiceUnavailableException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class JaxrsAsyncServletServiceUnavailableExceptionMapper implements
      ExceptionMapper<ServiceUnavailableException> {

   @Override
   public Response toResponse(ServiceUnavailableException exception) {
      String entity = new StringBuilder()
            .append(exception.getClass().getName()).append(";status=")
            .append(exception.getResponse().getStatus()).toString();
      return Response.status(Status.REQUEST_TIMEOUT).entity(entity).build();
   }

}
