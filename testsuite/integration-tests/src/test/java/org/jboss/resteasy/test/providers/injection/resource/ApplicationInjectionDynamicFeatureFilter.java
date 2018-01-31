package org.jboss.resteasy.test.providers.injection.resource;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

@Provider
@Priority(200)
public class ApplicationInjectionDynamicFeatureFilter implements ContainerResponseFilter {

   private ApplicationInjectionApplicationParent application;
   
   public ApplicationInjectionDynamicFeatureFilter(ApplicationInjectionApplicationParent application) {
      this.application = application;
   }
   
   @Override
   public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
      responseContext.setEntity(responseContext.getEntity() + "|" + getClass() + ":" + application.getName(), null, MediaType.TEXT_PLAIN_TYPE);
   }

}
