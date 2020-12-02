package org.jboss.resteasy.test.client.resource;

import jakarta.ws.rs.ConstrainedTo;
import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.container.DynamicFeature;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.FeatureContext;

@ConstrainedTo(RuntimeType.SERVER)
public class ClientDynamicFeaturesServerFeature1 implements DynamicFeature {
   @Override
   public void configure(ResourceInfo resourceInfo, FeatureContext context) {
   }
}
