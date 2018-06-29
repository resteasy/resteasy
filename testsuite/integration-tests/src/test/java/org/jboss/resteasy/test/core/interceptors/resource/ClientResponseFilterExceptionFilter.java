package org.jboss.resteasy.test.core.interceptors.resource;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.ext.Provider;

@Provider
public class ClientResponseFilterExceptionFilter implements ClientResponseFilter {

   @Override
   public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) {
      throw new RuntimeException("ClientResponseFilterExceptionFilter");
   }
}