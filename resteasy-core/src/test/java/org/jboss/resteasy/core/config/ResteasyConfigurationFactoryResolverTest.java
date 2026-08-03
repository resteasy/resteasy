/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.core.config;

import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.spi.config.ConfigurationFactory;
import org.jboss.resteasy.spi.config.ConfigurationFactoryResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests that validate the logic of {@link ResteasyConfigurationFactoryResolver}, which loads registered
 * {@link ConfigurationFactory} instances and caches them per-(RESTEasy)context.
 */
public class ResteasyConfigurationFactoryResolverTest {

    public static final int CUSTOM_CONFIGURATION_FACTORY_PRIORITY = 42;

    @Test
    public void testResolveReturnsNonNullDefault() {
        final ResteasyConfigurationFactoryResolver resolver = new ResteasyConfigurationFactoryResolver();
        ResteasyContext.addContextDataLevel();
        try {
            // The factory should NOT be cached in ResteasyContext initially
            Assertions.assertFalse(ResteasyContext.hasContextData(ConfigurationFactory.class),
                    "Factory should NOT yet be cached in ResteasyContext");
            final ConfigurationFactory factory = resolver.resolve();
            Assertions.assertNotNull(factory);
            Assertions.assertEquals(Integer.MAX_VALUE, factory.priority(),
                    String.format("Priority should be %d when no configuration factories are cached",
                            Integer.MAX_VALUE));
            // The default factory implementation should now be cached in ResteasyContext
            Assertions.assertTrue(ResteasyContext.hasContextData(ConfigurationFactory.class),
                    "Factory should be cached in ResteasyContext");
        } finally {
            ResteasyContext.removeContextDataLevel();
        }
    }

    @Test
    public void testResolveCachesInContext() {
        final ResteasyConfigurationFactoryResolver resolver = new ResteasyConfigurationFactoryResolver();
        ResteasyContext.addContextDataLevel();
        try {
            // The factory should NOT be cached in ResteasyContext initially
            Assertions.assertFalse(ResteasyContext.hasContextData(ConfigurationFactory.class),
                    "Factory should NOT yet be cached in ResteasyContext");
            final ConfigurationFactory first = resolver.resolve();
            final ConfigurationFactory second = resolver.resolve();
            Assertions.assertSame(first, second, "Expected the same cached instance from ResteasyContext");
            Assertions.assertEquals(Integer.MAX_VALUE, first.priority(),
                    String.format("Priority should be %d when no configuration factories are cached",
                            Integer.MAX_VALUE));
            // The factory implementation should now be cached in ResteasyContext
            Assertions.assertTrue(ResteasyContext.hasContextData(ConfigurationFactory.class),
                    "Factory should be cached in ResteasyContext");
        } finally {
            ResteasyContext.removeContextDataLevel();
        }
    }

    @Test
    public void testContextIsolation() {
        final ResteasyConfigurationFactoryResolver resolver = new ResteasyConfigurationFactoryResolver();
        final ConfigurationFactory custom = () -> CUSTOM_CONFIGURATION_FACTORY_PRIORITY;

        // Push a custom factory into context 1
        ResteasyContext.addContextDataLevel();
        try {
            // The factory should NOT be cached in ResteasyContext initially
            Assertions.assertFalse(ResteasyContext.hasContextData(ConfigurationFactory.class),
                    "Factory should NOT yet be cached in ResteasyContext");
            ResteasyContext.pushContext(ConfigurationFactory.class, custom);
            Assertions.assertSame(custom, resolver.resolve(),
                    "Expected the custom factory from context 1");
            Assertions.assertEquals(CUSTOM_CONFIGURATION_FACTORY_PRIORITY, custom.priority(),
                    String.format("Priority should be %d for context 1 custom configuration factory",
                            CUSTOM_CONFIGURATION_FACTORY_PRIORITY));
            // The factory implementation should now be cached in ResteasyContext
            Assertions.assertTrue(ResteasyContext.hasContextData(ConfigurationFactory.class),
                    "Factory should be cached in ResteasyContext");
        } finally {
            ResteasyContext.removeContextDataLevel();
        }

        // Context 2 should not see the custom factory from context 1
        ResteasyContext.addContextDataLevel();
        try {
            // The factory should NOT be cached in ResteasyContext initially
            Assertions.assertFalse(ResteasyContext.hasContextData(ConfigurationFactory.class),
                    "Factory should NOT yet be cached in ResteasyContext");
            final ConfigurationFactory factory = resolver.resolve();
            Assertions.assertNotNull(factory);
            Assertions.assertNotSame(custom, factory,
                    "Context 2 should not see the custom factory from context 1");
            Assertions.assertEquals(Integer.MAX_VALUE, factory.priority(),
                    String.format("Priority should be %d for context 2 factory, as no configuration factories are cached",
                            Integer.MAX_VALUE));
            // The factory implementation should now be cached in ResteasyContext
            Assertions.assertTrue(ResteasyContext.hasContextData(ConfigurationFactory.class),
                    "Factory should be cached in ResteasyContext");
        } finally {
            ResteasyContext.removeContextDataLevel();
        }
    }

    @Test
    public void testGetInstanceUsesResolverWhenEnabled() {
        // Enable the resolver property
        System.setProperty(ConfigurationFactoryResolver.ENABLE_RESOLVER_PROPERTY, "true");
        ResteasyContext.addContextDataLevel();
        try {
            // The factory should NOT be cached in ResteasyContext initially
            Assertions.assertFalse(ResteasyContext.hasContextData(ConfigurationFactory.class),
                    "Factory should NOT yet be cached in ResteasyContext");

            // ConfigurationFactory.getInstance() should take the resolver path.
            // ResteasyConfigurationFactoryResolver is registered via META-INF/services
            // in resteasy-core, so ServiceLoader will find it.
            final ConfigurationFactory factory = ConfigurationFactory.getInstance();
            Assertions.assertNotNull(factory);

            // The factory should now be cached in ResteasyContext
            Assertions.assertTrue(ResteasyContext.hasContextData(ConfigurationFactory.class),
                    "Factory should be cached in ResteasyContext when the resolver is enabled");
            final ConfigurationFactory contextData = ResteasyContext.getContextData(ConfigurationFactory.class);
            // validate the cached factory
            Assertions.assertSame(factory, contextData,
                    "getInstance() should return the same instance cached in ResteasyContext");
            Assertions.assertEquals(Integer.MAX_VALUE, factory.priority(),
                    String.format("Priority should be %d when no configuration factories are cached",
                            Integer.MAX_VALUE));
        } finally {
            ResteasyContext.removeContextDataLevel();
            System.clearProperty(ConfigurationFactoryResolver.ENABLE_RESOLVER_PROPERTY);
        }
    }
}
