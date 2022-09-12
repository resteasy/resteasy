package org.jboss.resteasy.test.client.exception.resource;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.client.ClientResponseFilter;

public class ClientExceptionsRuntimeExceptionResponseFilter implements ClientResponseFilter {

   @Override
   public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) {
      throw new RuntimeException("runtime exception");
   }
}
