package org.jboss.resteasy.test.client.resource;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

public class ConfigurationInheritenceTestFeature5 implements Feature {
    @Override
    public boolean configure(FeatureContext context) {
        return true;
    }
}
