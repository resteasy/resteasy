package org.jboss.resteasy.test.asynch.resource;

import java.io.IOException;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

@Provider
public class AsyncPostProcessingInterceptor implements ContainerResponseFilter {
   public static volatile boolean called;

   @Override
   public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
         throws IOException
   {
      called = true;
   }
}
