package org.jboss.resteasy.test.client.resource;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.core.Response;
import java.io.IOException;

public class RequestFilterAbortWith implements ClientRequestFilter {
   @Override
   public void filter(ClientRequestContext requestContext) throws IOException {
      requestContext.abortWith(Response.ok(new Integer(42)).build());
   }
}
