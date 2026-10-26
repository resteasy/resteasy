/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.resteasy.test.injection;

import java.util.concurrent.atomic.AtomicInteger;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Provider;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import dev.resteasy.junit.extension.annotations.RequestPath;
import dev.resteasy.junit.extension.annotations.RestBootstrap;
import dev.resteasy.junit.extension.annotations.RestResource;

/**
 * Tests that a {@link Provider @Provider} which declares {@link Singleton @Singleton} is only injected by CDI and not
 * again by RESTEasy when it is registered.
 */
@RestBootstrap(value = { CdiSingletonProviderTest.SingletonProviderResource.class,
        CdiSingletonProviderTest.SingletonProvider.class })
@Tag("cdi")
class CdiSingletonProviderTest {

    @Test
    void contextSetterInjectedOnce(@RestResource @RequestPath("singleton-provider") final WebTarget target) {
        Assertions.assertEquals(1, target.request().get(int.class));
    }

    @Provider
    @Singleton
    public static class SingletonProvider implements ContainerRequestFilter {
        private final AtomicInteger uriInfoInjections = new AtomicInteger();

        @Override
        public void filter(final ContainerRequestContext requestContext) {
        }

        @Context
        public void setUriInfo(final UriInfo uriInfo) {
            uriInfoInjections.incrementAndGet();
        }

        int getUriInfoInjections() {
            return uriInfoInjections.get();
        }
    }

    @Path("/singleton-provider")
    @RequestScoped
    public static class SingletonProviderResource {

        @Inject
        SingletonProvider provider;

        @GET
        @Produces(MediaType.TEXT_PLAIN)
        public int uriInfoInjections() {
            return provider.getUriInfoInjections();
        }
    }
}
