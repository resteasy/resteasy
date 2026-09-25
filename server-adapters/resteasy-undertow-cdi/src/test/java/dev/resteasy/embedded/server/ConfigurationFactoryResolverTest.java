/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.resteasy.embedded.server;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.spi.config.ConfigurationFactory;
import org.jboss.resteasy.spi.config.ConfigurationFactoryResolver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import dev.resteasy.junit.extension.annotations.RequestPath;
import dev.resteasy.junit.extension.annotations.RestBootstrap;
import dev.resteasy.junit.extension.annotations.RestResource;

/**
 * Validates that {@link ConfigurationFactoryResolver} works in an embedded server environment
 * where no container pre-populates the {@link org.jboss.resteasy.core.ResteasyContext}.
 * <p>
 * When the {@code dev.resteasy.configuration.factory.resolver.enabled} property is enabled,
 * {@link ConfigurationFactory#getInstance()} should delegate to the
 * {@link org.jboss.resteasy.core.config.ResteasyConfigurationFactoryResolver}, which discovers factories via TCCL
 * and caches them per-context.
 * </p>
 */
@RestBootstrap(ConfigurationFactoryResolverTest.FactoryResource.class)
public class ConfigurationFactoryResolverTest {

    private static String previousPropertyValue;

    @BeforeAll
    public static void enableResolver() {
        previousPropertyValue = System.getProperty(ConfigurationFactoryResolver.ENABLE_RESOLVER_PROPERTY);
        System.setProperty(ConfigurationFactoryResolver.ENABLE_RESOLVER_PROPERTY, "true");
    }

    @AfterAll
    public static void restoreResolver() {
        if (previousPropertyValue == null) {
            System.clearProperty(ConfigurationFactoryResolver.ENABLE_RESOLVER_PROPERTY);
        } else {
            System.setProperty(ConfigurationFactoryResolver.ENABLE_RESOLVER_PROPERTY, previousPropertyValue);
        }
    }

    /**
     * Verifies that when the {@code dev.resteasy.configuration.factory.resolver.enabled} is enabled and no
     * custom resolvers are registered, the {@link org.jboss.resteasy.core.config.ResteasyConfigurationFactoryResolver}
     * is loaded, and it is delegated to resolve the {@link ConfigurationFactory} instance that should be used.
     * Since {@link TestConfigurationFactory} is registered via SPI, it should be discovered by the resolver and its
     * FQDN should be returned by the test application resource.
     */
    @Test
    public void testResolverProvidesFactory(@RestResource @RequestPath("/config/factory-class") final WebTarget target) {
        try (Response response = target.request().get()) {
            Assertions.assertEquals(200, response.getStatus());
            Assertions.assertEquals(TestConfigurationFactory.class.getName(), response.readEntity(String.class),
                    "Resolver should discover TestConfigurationFactory via TCCL");
        }
    }

    /**
     * Verifies that the same {@link ConfigurationFactory} instance is returned if two subsequent calls to
     * {@link ConfigurationFactory#getInstance()} are performed within the same request.
     * Such behavior is implemented by {@link org.jboss.resteasy.core.config.ResteasyConfigurationFactoryResolver},
     * which caches discovered factories per-context (i.e. per request), when the
     * {@code dev.resteasy.configuration.factory.resolver.enabled} is enabled.
     */
    @Test
    public void testResolverCachesFactoryWithinRequest(
            @RestResource @RequestPath("/config/factory-cached") final WebTarget target) {
        try (Response response = target.request().get()) {
            Assertions.assertEquals(200, response.getStatus());
            Assertions.assertEquals(Boolean.TRUE, response.readEntity(Boolean.class),
                    "Two getInstance() calls within the same request should return the same instance");
        }
    }

    /**
     * Verifies that different {@link ConfigurationFactory} instances are returned if two calls to
     * {@link ConfigurationFactory#getInstance()} are performed by two different requests.
     * Such behavior is implemented by {@link org.jboss.resteasy.core.config.ResteasyConfigurationFactoryResolver},
     * which caches discovered factories per-context (i.e. per request), when the
     * {@code dev.resteasy.configuration.factory.resolver.enabled} is enabled.
     */
    @Test
    public void testRequestContextIsolation(
            @RestResource @RequestPath("/config/factory-identity") final WebTarget target) {
        final String firstIdentity;
        try (Response response = target.request().get()) {
            Assertions.assertEquals(200, response.getStatus());
            firstIdentity = response.readEntity(String.class);
        }
        final String secondIdentity;
        try (Response response = target.request().get()) {
            Assertions.assertEquals(200, response.getStatus());
            secondIdentity = response.readEntity(String.class);
        }
        Assertions.assertNotNull(firstIdentity);
        Assertions.assertNotNull(secondIdentity);
        Assertions.assertNotEquals(firstIdentity, secondIdentity,
                "Different requests should resolve independent factory instances (separate context levels)");
    }

    @Path("/config")
    public static class FactoryResource {

        @GET
        @Path("factory-class")
        @Produces("text/plain")
        public String getFactoryClass() {
            return ConfigurationFactory.getInstance().getClass().getName();
        }

        @GET
        @Path("factory-cached")
        @Produces("text/plain")
        public Boolean getFactoryCached() {
            final ConfigurationFactory first = ConfigurationFactory.getInstance();
            final ConfigurationFactory second = ConfigurationFactory.getInstance();
            return first == second;
        }

        @GET
        @Path("factory-identity")
        @Produces("text/plain")
        public String getFactoryIdentity() {
            return String.valueOf(System.identityHashCode(ConfigurationFactory.getInstance()));
        }
    }
}
