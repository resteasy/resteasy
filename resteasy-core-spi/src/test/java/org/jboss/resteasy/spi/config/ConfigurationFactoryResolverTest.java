/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.spi.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link ConfigurationFactoryResolver} selection logic in {@link ConfigurationFactory#getInstance()}.
 */
public class ConfigurationFactoryResolverTest {

    @AfterEach
    public void clearProperty() {
        System.clearProperty(ConfigurationFactoryResolver.ENABLE_RESOLVER_PROPERTY);
    }

    /**
     * Verifies that {@link ConfigurationFactory#getInstance()} uses the {@link SingletonConfigurationFactoryHolder}
     * fallback, ignoring the {@link TestConfigurationFactoryResolver} registered via META-INF/services,
     * when the {@code dev.resteasy.configuration.factory.resolver.enabled} property is set to {@code false} (default).
     */
    @Test
    public void testFallbackWhenPropertyDisabled() {
        Assertions.assertEquals(TestConfigurationFactory.class, ConfigurationFactory.getInstance().getClass());
    }

    /**
     * Verifies that {@link ConfigurationFactory#getInstance()} discovers the {@link TestConfigurationFactoryResolver}
     * when the {@code dev.resteasy.configuration.factory.resolver.enabled} property is set to {@code true}
     */
    @Test
    public void testResolverUsedWhenPropertyEnabled() {
        System.setProperty(ConfigurationFactoryResolver.ENABLE_RESOLVER_PROPERTY, "true");

        final ConfigurationFactory factory = ConfigurationFactory.getInstance();
        Assertions.assertNotNull(factory);
        Assertions.assertEquals(TestConfigurationFactoryResolver.RESOLVED_FACTORY_PRIORITY, factory.priority(),
                "Resolver should return a factory with priority = "
                        + TestConfigurationFactoryResolver.RESOLVED_FACTORY_PRIORITY);
    }
}
