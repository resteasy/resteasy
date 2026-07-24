/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.core.config;

import java.util.ServiceLoader;

import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.spi.config.ConfigurationFactory;
import org.jboss.resteasy.spi.config.ConfigurationFactoryResolver;

/**
 * A {@link ConfigurationFactoryResolver} implementation that discovers {@link ConfigurationFactory} instances
 * via a given deployment thread context classloader, and caches the selected instance in {@link ResteasyContext}.
 *
 * @author <a href="mailto:fburzigo@ibm.com">Fabio Burzigotti</a>
 */
public class ResteasyConfigurationFactoryResolver implements ConfigurationFactoryResolver {

    @Override
    public ConfigurationFactory resolve() {
        return ResteasyContext.computeIfAbsent(ConfigurationFactory.class,
                ResteasyConfigurationFactoryResolver::loadFactory);
    }

    private static ConfigurationFactory loadFactory() {
        // TCCL must be used here so each deployment discovers its own ConfigurationFactory implementations,
        // and ResteasyContext caches the result per-deployment context.
        final ServiceLoader<ConfigurationFactory> loader = ServiceLoader.load(ConfigurationFactory.class);
        ConfigurationFactory current = null;
        for (ConfigurationFactory factory : loader) {
            if (current == null) {
                current = factory;
            } else if (factory.priority() < current.priority()) {
                current = factory;
            }
        }
        return current == null ? () -> Integer.MAX_VALUE : current;
    }
}
