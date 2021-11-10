package org.jboss.resteasy.test.providers.custom.resource;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class ProviderContextInjectionAnyExceptionExceptionMapper implements ExceptionMapper<Exception> {

   @Override
   public Response toResponse(Exception arg0) {
      Response.Status status = Response.Status.NO_CONTENT;
      if (arg0 instanceof WebApplicationException) {
         return ((WebApplicationException) arg0).getResponse();
      } else if (arg0 instanceof RuntimeException) {
         throw new RuntimeException("CTS Test RuntimeException", arg0);
      } else if (arg0 instanceof IOException) {
         status = Response.Status.SERVICE_UNAVAILABLE;
      } else if (arg0 != null) {
         status = Response.Status.NOT_ACCEPTABLE;
      }
      return Response.status(status).build();
   }

}
