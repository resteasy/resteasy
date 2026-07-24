/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.spi.config;

/**
 * A concrete implementation of {@link ConfigurationFactoryResolver} that is SPI registered and that returns
 * a distinct {@link ConfigurationFactory} so tests can distinguish the resolver path from the fallback path.
 */
public class TestConfigurationFactoryResolver implements ConfigurationFactoryResolver {

    public static final int RESOLVED_FACTORY_PRIORITY = 200;

    @Override
    public ConfigurationFactory resolve() {
        return () -> RESOLVED_FACTORY_PRIORITY;
    }
}
