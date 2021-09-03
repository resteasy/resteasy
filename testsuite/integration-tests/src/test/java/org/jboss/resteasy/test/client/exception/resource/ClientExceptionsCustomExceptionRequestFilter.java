package org.jboss.resteasy.test.client.exception.resource;


import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;

public class ClientExceptionsCustomExceptionRequestFilter implements ClientRequestFilter {
   @Override
   public void filter(ClientRequestContext requestContext) {
      WebApplicationException exc = new ClientExceptionsCustomException("custom message");
      throw exc;
   }
}
