/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.spi.config;

import java.util.ServiceLoader;

/**
 * Fallback holder for environments where no {@link ConfigurationFactoryResolver} is available.
 * Loads the {@link ConfigurationFactory} via {@link ServiceLoader} and caches it statically.
 */
class SingletonConfigurationFactoryHolder {
    static final ConfigurationFactory INSTANCE;

    static {
        // We must use this class loader for environments where the TCCL might pick up an instance from a source
        // that is not meant to be shared.
        final ServiceLoader<ConfigurationFactory> loader = ServiceLoader.load(ConfigurationFactory.class,
                SingletonConfigurationFactoryHolder.class.getClassLoader());
        ConfigurationFactory current = null;
        for (ConfigurationFactory factory : loader) {
            if (current == null) {
                current = factory;
            } else if (factory.priority() < current.priority()) {
                current = factory;
            }
        }
        INSTANCE = current == null ? () -> Integer.MAX_VALUE : current;
    }
}
