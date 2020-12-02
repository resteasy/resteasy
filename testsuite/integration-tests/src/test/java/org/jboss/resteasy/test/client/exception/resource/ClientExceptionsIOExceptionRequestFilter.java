package org.jboss.resteasy.test.client.exception.resource;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import java.io.IOException;

public class ClientExceptionsIOExceptionRequestFilter implements ClientRequestFilter {
   @Override
   public void filter(ClientRequestContext requestContext) throws IOException {
      throw new IOException("client io");
   }
}
