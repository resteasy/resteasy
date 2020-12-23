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

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ServiceLoader;

import org.jboss.resteasy.spi.ResteasyConfiguration;

/**
 * A factory which returns the {@link Configuration}.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public interface ConfigurationFactory {

    /**
     * Returns a new factory. The factory with the lowest {@linkplain #priority() priority} will be selected.
     *
     * @return the new factory
     *
     * @throws RuntimeException if the service loader could not find a factory
     */
    static ConfigurationFactory getInstance() {
        if (System.getSecurityManager() == null) {
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
        return AccessController.doPrivileged((PrivilegedAction<ConfigurationFactory>) () -> {
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
        });
    }

    /**
     * Returns the configuration for the current context.
     *
     * @return the configuration
     */
    default Configuration getConfiguration() {
        return getConfiguration(null);
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
     * The ranking priority for the this factory. The lowest priority will be the one selected.
     *
     * @return the priority
     */
    int priority();
}
