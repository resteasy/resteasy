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

package org.jboss.resteasy.client.exception;

import java.security.AccessController;
import java.security.PrivilegedAction;

import org.jboss.resteasy.spi.config.ConfigurationFactory;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
class SecurityActions {

    /**
     * Retrieves a property from the {@link org.jboss.resteasy.spi.config.Configuration}. If the security manager is
     * present, a privileged action is used to get the property.
     *
     * @param name the name of the property
     * @param type the type of the property
     * @param dft  the default value if not found
     *
     * @return the value, if found, or the default
     */
    @SuppressWarnings("SameParameterValue")
    static <T> T getConfigValue(final String name, final Class<T> type, final T dft) {
        if (System.getSecurityManager() == null) {
            return ConfigurationFactory.getInstance().getConfiguration()
                    .getOptionalValue(name, type)
                    .orElse(dft);
        }
        return AccessController.doPrivileged((PrivilegedAction<T>) () -> ConfigurationFactory.getInstance().getConfiguration()
                .getOptionalValue(name, type)
                .orElse(dft));
    }
}
