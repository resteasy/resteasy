package org.jboss.resteasy.test.core.interceptors.resource;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

@Provider
public class PreProcessorExceptionMapperPreProcessSecurityInterceptor implements ContainerRequestFilter {

   @Override
   public void filter(ContainerRequestContext requestContext) throws IOException
   {
      throw new PreProcessorExceptionMapperCandlepinUnauthorizedException();
   }
}
