package org.jboss.resteasy.test.client.exception.resource;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.client.ClientResponseFilter;
import java.io.IOException;

public class ClientExceptionsCustomExceptionResponseFilter implements ClientResponseFilter {

   @Override
   public void filter(ClientRequestContext clientRequestContext, ClientResponseContext clientResponseContext) throws IOException {
      WebApplicationException exc = new ClientExceptionsCustomException("custom message");
      throw exc;
   }
}
