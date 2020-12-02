package org.jboss.resteasy.test.interceptor.resource;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.client.ClientResponseFilter;


public class ThrowCustomExceptionResponseFilter implements ClientResponseFilter {
   @Override
   public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) {
      throw new CustomException();
   }
}
