/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2024 Red Hat, Inc., and individual contributors
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

package org.jboss.resteasy.client.jaxrs.internal;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Optional;

import org.jboss.resteasy.spi.PriorityServiceLoader;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@SuppressWarnings("removal")
class SecurityActions {

    /**
     * Returns an optional service found with an optional {@link jakarta.annotation.Priority} annotation.
     *
     * @param type the type to load
     * @param <T>  the return type
     *
     * @return the optional resource, which may be empty
     */
    static <T> Optional<T> findFirstService(final Class<T> type) {
        if (System.getSecurityManager() == null) {
            return PriorityServiceLoader.load(type).first();
        }
        return AccessController.doPrivileged((PrivilegedAction<Optional<T>>) () -> PriorityServiceLoader.load(type)
                .first());
    }
}
