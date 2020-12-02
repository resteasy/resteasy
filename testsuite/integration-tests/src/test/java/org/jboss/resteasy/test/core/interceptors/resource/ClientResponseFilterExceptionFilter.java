package org.jboss.resteasy.test.core.interceptors.resource;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.client.ClientResponseFilter;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ClientResponseFilterExceptionFilter implements ClientResponseFilter {

   @Override
   public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) {
      throw new RuntimeException("ClientResponseFilterExceptionFilter");
   }
}
