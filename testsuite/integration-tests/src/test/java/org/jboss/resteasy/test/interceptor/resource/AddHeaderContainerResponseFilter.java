package org.jboss.resteasy.test.interceptor.resource;

import java.io.IOException;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

@Provider
public class AddHeaderContainerResponseFilter implements ContainerResponseFilter
{
   @Override
   public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException
   {
      responseContext.getHeaders().add("custom-header", "hello");
   }
}
