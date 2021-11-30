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

package org.jboss.resteasy.plugins.providers.jaxb;

import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * <strong>Not for external usage.</strong>
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
class SecurityActions {

    private SecurityActions() {
    }

    /**
     * Returns the current context class loader.
     *
     * @return the current context class loader
     */
    static ClassLoader getContextClassLoader() {
        if (System.getSecurityManager() == null) {
            return Thread.currentThread().getContextClassLoader();
        }
        return AccessController.doPrivileged((PrivilegedAction<ClassLoader>) () -> Thread.currentThread()
                .getContextClassLoader());
    }

    /**
     * Sets the context class loader to the class loader provided.
     *
     * @param cl the class loader to set
     */
    static void setContextClassLoader(final ClassLoader cl) {
        if (System.getSecurityManager() == null) {
            Thread.currentThread().setContextClassLoader(cl);
        } else {
            AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
                Thread.currentThread().setContextClassLoader(cl);
                return null;
            });
        }
    }

    /**
     * Sets the class loader to the class loader from this type.
     */
    static void setContextClassLoader() {
        if (System.getSecurityManager() == null) {
            Thread.currentThread().setContextClassLoader(SecurityActions.class.getClassLoader());
        } else {
            AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
                Thread.currentThread().setContextClassLoader(SecurityActions.class.getClassLoader());
                return null;
            });
        }
    }
}
