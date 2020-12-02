package org.jboss.resteasy.test.cdi.interceptors.resource;

import javax.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.logging.Logger;

@Provider
@InterceptorFilterBinding
@InterceptorResponseFilterInterceptorBinding
public class InterceptorResponseFilter implements ContainerResponseFilter {
   @Inject
   private Logger log;

   @Override
   public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
      log.info("executing InterceptorResponseFilter.filter()");
   }
}
