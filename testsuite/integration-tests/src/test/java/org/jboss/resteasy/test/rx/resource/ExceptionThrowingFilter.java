package org.jboss.resteasy.test.rx.resource;

import java.io.IOException;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

@FilterException
@Provider
public class ExceptionThrowingFilter implements ContainerResponseFilter
{

   @Override
   public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
         throws IOException
   {
      throw new WebApplicationException(Response.ok("exception", MediaType.TEXT_PLAIN).build());
   }

}
