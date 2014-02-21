package org.jboss.resteasy.arquillian.extension;

import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;

public class DeploymentScenarioProviderExtension implements LoadableExtension {

    @Override
    public void register(ExtensionBuilder builder) {
        builder.service(ResourceProvider.class, DeploymentScenarioProvider.class);
    }

}



