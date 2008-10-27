package org.jboss.resteasy.springmvc;

import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

public class ResteasyIntializer {

    public ResteasyIntializer(ResteasyProviderFactory providerFactory) {
        RegisterBuiltin.register(providerFactory);
    }
}
