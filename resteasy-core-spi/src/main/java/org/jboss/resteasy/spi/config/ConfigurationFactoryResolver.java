/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.spi.config;

/**
 * Defines a contract for a resolver that should provide a {@link ConfigurationFactory} instance.
 * Implementations are discovered via {@link java.util.ServiceLoader} and selected based on their
 * {@link ConfigurationFactoryResolver#priority()}.
 *
 * @author <a href="mailto:fburzigo@ibm.com">Fabio Burzigotti</a>
 */
public interface ConfigurationFactoryResolver {

    /**
     * System property to enable the {@link ConfigurationFactoryResolver} path in
     * {@link ConfigurationFactory#getInstance()}. When set to {@code true}, the factory resolution is delegated
     * to a {@link ConfigurationFactoryResolver} discovered via {@link java.util.ServiceLoader}. When {@code false}
     * (the default), the singleton behavior is used.
     */
    String ENABLE_RESOLVER_PROPERTY = "dev.resteasy.configuration.factory.resolver.enabled";

    int DEFAULT_RANKING_PRIORITY = 100;

    /**
     * Resolves the {@link ConfigurationFactory}.
     *
     * @return The configuration factory
     */
    ConfigurationFactory resolve();

    /**
     * The ranking priority for this resolver. The lowest priority will be the one selected.
     *
     * @return The priority
     */
    default int priority() {
        return DEFAULT_RANKING_PRIORITY;
    }
}
