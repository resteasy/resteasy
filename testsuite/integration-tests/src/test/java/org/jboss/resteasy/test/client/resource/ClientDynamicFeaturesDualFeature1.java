package org.jboss.resteasy.test.client.resource;

import jakarta.ws.rs.container.DynamicFeature;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.FeatureContext;

public class ClientDynamicFeaturesDualFeature1 implements DynamicFeature {
   @Override
   public void configure(ResourceInfo resourceInfo, FeatureContext context) {
   }
}
