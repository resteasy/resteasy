package org.jboss.resteasy.test.providers.injection.resource;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

@Provider
public class ApplicationInjectionContainerRequestFilter implements ContainerRequestFilter {

   @Context
   ApplicationInjectionApplicationParent application;

   @Override
   public void filter(ContainerRequestContext requestContext) throws IOException {
      requestContext.setProperty("requestFilterApplication", getClass() + ":" + application.getName());
   } 
}
