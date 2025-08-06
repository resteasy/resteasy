/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2022 Red Hat, Inc., and individual contributors
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

/**
 * Loads the {@link ConfigurationFactory} lazily during instantiation.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
class SingletonConfigurationFactory {

    /**
     * Returns the lazily created {@link ConfigurationFactory}.
     *
     * @return the configuration factory found
     */
    static ConfigurationFactory getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        static final ConfigurationFactory INSTANCE;

        static {
            // We must use this class loader for environments where the TCCL might pick up an instance from a source
            // that is not meant to be shared.
            final ServiceLoader<ConfigurationFactory> loader = ServiceLoader.load(ConfigurationFactory.class,
                    Holder.class.getClassLoader());
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
}
