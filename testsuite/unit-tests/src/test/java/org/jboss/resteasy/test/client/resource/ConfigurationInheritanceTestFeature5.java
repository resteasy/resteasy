package org.jboss.resteasy.test.client.resource;

import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.FeatureContext;

public class ConfigurationInheritanceTestFeature5 implements Feature {
    @Override
    public boolean configure(FeatureContext context) {
        return true;
    }
}
