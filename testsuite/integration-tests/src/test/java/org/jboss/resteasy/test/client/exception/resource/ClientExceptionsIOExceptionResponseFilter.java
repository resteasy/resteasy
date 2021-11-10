package org.jboss.resteasy.test.client.exception.resource;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.client.ClientResponseFilter;
import java.io.IOException;

public class ClientExceptionsIOExceptionResponseFilter implements ClientResponseFilter {

   @Override
   public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
      throw new IOException("client io");
   }
}
