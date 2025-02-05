/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2025 Red Hat, Inc., and individual contributors
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

package org.jboss.resteasy.core.se;

import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * This <strong>must</strong> not be used or shared outside of this package.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
final class SecurityActions {

    private SecurityActions() {
    }

    static void addShutdownHook(final Thread shutdownHook) {
        if (System.getSecurityManager() == null) {
            Runtime.getRuntime().addShutdownHook(shutdownHook);
        } else {
            AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
                Runtime.getRuntime().addShutdownHook(shutdownHook);
                return null;
            });
        }
    }

    static ClassLoader resolveClassLoader(final Class<?> c) {
        if (c == null) {
            return currentClassLoader();
        }
        if (System.getSecurityManager() == null) {
            return c.getClassLoader();
        }
        return AccessController.doPrivileged((PrivilegedAction<ClassLoader>) c::getClassLoader);
    }

    private static ClassLoader currentClassLoader() {
        if (System.getSecurityManager() == null) {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if (cl == null) {
                cl = ResteasySeInstance.class.getClassLoader();
            }
            if (cl == null) {
                cl = ClassLoader.getSystemClassLoader();
            }
            return cl;
        }
        return AccessController.doPrivileged((PrivilegedAction<ClassLoader>) () -> {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if (cl == null) {
                cl = ResteasySeInstance.class.getClassLoader();
            }
            if (cl == null) {
                cl = ClassLoader.getSystemClassLoader();
            }
            return cl;
        });
    }
}
