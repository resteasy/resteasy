package org.jboss.resteasy.test.core.interceptors.resource;

import java.io.IOException;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;

@Provider
public class PreProcessorExceptionMapperPreProcessSecurityInterceptor implements ContainerRequestFilter {

   @Override
   public void filter(ContainerRequestContext requestContext) throws IOException
   {
      throw new PreProcessorExceptionMapperCandlepinUnauthorizedException();
   }
}
