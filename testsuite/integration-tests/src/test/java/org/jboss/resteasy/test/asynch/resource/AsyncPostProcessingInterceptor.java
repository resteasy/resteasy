package org.jboss.resteasy.test.asynch.resource;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

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
