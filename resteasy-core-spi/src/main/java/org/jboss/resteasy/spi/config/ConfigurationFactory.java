/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2021 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.resteasy.spi.config;

import java.util.ServiceLoader;

import org.jboss.resteasy.spi.ResteasyConfiguration;

/**
 * A factory which returns the {@link Configuration}.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public interface ConfigurationFactory {

    /**
     * Returns the factory for the environment. If the system property
     * {@link ConfigurationFactoryResolver#ENABLE_RESOLVER_PROPERTY} is set to {@code true} and a
     * {@link ConfigurationFactoryResolver} is available via {@link ServiceLoader}, it is used to resolve the factory.
     * Otherwise, the factory with the lowest {@linkplain #priority() priority} discovered via {@link ServiceLoader}
     * will be selected and stored statically.
     *
     * @return the factory for the current environment
     */
    static ConfigurationFactory getInstance() {
        if (Boolean.getBoolean(ConfigurationFactoryResolver.ENABLE_RESOLVER_PROPERTY)) {
            // pick up a resolver
            final ServiceLoader<ConfigurationFactoryResolver> loader = ServiceLoader.load(
                    ConfigurationFactoryResolver.class);
            ConfigurationFactoryResolver resolver = null;
            for (ConfigurationFactoryResolver loaded : loader) {
                if (resolver == null || loaded.priority() < resolver.priority()) {
                    resolver = loaded;
                }
            }
            // if at least one resolver is picked up, use it
            if (resolver != null) {
                final ConfigurationFactory factory = resolver.resolve();
                if (factory != null) {
                    return factory;
                }
            }
        }
        // fall back to singleton behavior: select one of the registered ConfigurationFactory instances,
        // and cache it statically
        return SingletonConfigurationFactoryHolder.INSTANCE;
    }

    /**
     * Returns the configuration for the current context.
     *
     * @return the configuration
     */
    default Configuration getConfiguration() {
        return new DefaultConfiguration();
    }

    /**
     * Returns the configuration for the current context.
     *
     * @param config a {@linkplain ResteasyConfiguration configuration} used to resolve default values, if {@code null}
     *               a default resolver will be used
     *
     * @return the configuration
     */
    default Configuration getConfiguration(final ResteasyConfiguration config) {
        return new DefaultConfiguration(config);
    }

    /**
     * The ranking priority for the factory. The lowest priority will be the one selected.
     *
     * @return the priority
     */
    int priority();
}
