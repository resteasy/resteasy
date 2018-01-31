package org.jboss.resteasy.test.providers.injection.resource;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

@Provider
public class ApplicationInjectionDynamicFeature implements DynamicFeature {

   @Context
   ApplicationInjectionApplicationParent application;
   
   @Override
   public void configure(ResourceInfo resourceInfo, FeatureContext context) {
      context.register(new ApplicationInjectionDynamicFeatureFilter(application));
   }
}
