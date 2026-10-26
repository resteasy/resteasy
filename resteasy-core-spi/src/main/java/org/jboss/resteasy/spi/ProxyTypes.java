/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.spi;

/**
 * Resolves the type a CDI proxy class delegates to.
 * <p>
 * CDI implementations, e.g. Weld and OpenWebBeans, mark the client proxy and intercepted subclass classes they
 * generate as {@linkplain Class#isSynthetic() synthetic}. This avoids depending on implementation specific marker
 * interfaces.
 * </p>
 */
class ProxyTypes {

    private ProxyTypes() {
    }

    /**
     * Returns the bean class of a CDI proxy class, e.g. a client proxy or an intercepted subclass. If the type is not
     * a synthetic class, it is returned as is.
     * <p>
     * The proxy class extends the most specific class of the bean types. If the bean types only include interfaces,
     * the proxy class extends {@link Object} and the type is returned as is. This also covers other synthetic classes,
     * e.g. lambdas.
     * </p>
     *
     * @param type the type, which may be a proxy class
     * @param <T>  the type
     *
     * @return the bean class of the proxy class, or the type if it is not a proxy class
     */
    static <T> Class<? super T> resolve(final Class<T> type) {
        if (type.isSynthetic()) {
            final Class<? super T> superclass = type.getSuperclass();
            return superclass == null || superclass == Object.class ? type : superclass;
        }
        return type;
    }
}
