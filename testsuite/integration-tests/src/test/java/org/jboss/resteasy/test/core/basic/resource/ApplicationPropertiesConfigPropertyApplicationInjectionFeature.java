package org.jboss.resteasy.test.core.basic.resource;

import jakarta.ws.rs.container.DynamicFeature;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.FeatureContext;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ApplicationPropertiesConfigPropertyApplicationInjectionFeature implements DynamicFeature {

   @Override
   public void configure(ResourceInfo resourceInfo, FeatureContext context) {
      boolean propertyPresent = context.getConfiguration().getProperties().containsKey("Prop1");
   }
}
