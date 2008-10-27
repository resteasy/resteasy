package org.jboss.resteasy.springmvc;

import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.springframework.beans.factory.InitializingBean;

public class ResteasyIntializer implements InitializingBean {

    private ResteasyProviderFactory providerFactory;

    public ResteasyProviderFactory getProviderFactory() {
        return providerFactory;
    }

    public void setProviderFactory(ResteasyProviderFactory providerFactory) {
        this.providerFactory = providerFactory;
    }

    public void afterPropertiesSet() throws Exception {
        // NOTE: copied from ResteasyBootstrap.

        // Register this provider factory as the one and only "Singleton"
        // factory in both Resteasy and the JAX-RS common environment
        ResteasyProviderFactory.setInstance(providerFactory);

        // add default "built in" providers. Is "register" the right name for
        // this, or should it be "initialize?"
        RegisterBuiltin.register(providerFactory);

    }

}
