package org.jboss.resteasy.test.providers.custom.resource;

import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.FeatureContext;

public class DuplicateProviderRegistrationFeature implements Feature {
   @Override
   public boolean configure(FeatureContext featureContext) {
      // DuplicateProviderRegistrationFilter instance will be registered third on the same
      // featureContext even if
      // featureContext.getConfiguration().isRegistered(DuplicateProviderRegistrationFilter.class)==true
      featureContext.register(new DuplicateProviderRegistrationFilter());
      return true;
   }
}
