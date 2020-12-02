package org.jboss.resteasy.test.client.resource;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.client.ClientResponseFilter;
import jakarta.ws.rs.core.Response;
import java.io.IOException;

public class ClientResponseFilterStatusOverride implements ClientResponseFilter {
   @Override
   public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
      responseContext.setStatus(Response.Status.FORBIDDEN.getStatusCode());
   }
}
