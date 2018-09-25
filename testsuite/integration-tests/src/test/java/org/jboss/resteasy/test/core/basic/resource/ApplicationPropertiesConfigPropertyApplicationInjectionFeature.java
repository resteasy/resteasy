package org.jboss.resteasy.test.core.basic.resource;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

@Provider
public class ApplicationPropertiesConfigPropertyApplicationInjectionFeature implements DynamicFeature {

   @Override
   public void configure(ResourceInfo resourceInfo, FeatureContext context) {
      boolean propertyPresent = context.getConfiguration().getProperties().containsKey("Prop1");
   }
}
