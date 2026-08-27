/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.resteasy.embedded.test.cdi;

import java.util.concurrent.atomic.AtomicInteger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import dev.resteasy.junit.extension.annotations.RequestPath;
import dev.resteasy.junit.extension.annotations.RestBootstrap;
import dev.resteasy.junit.extension.annotations.RestResource;

/**
 * Tests that {@code @RequestScoped} and {@code @ApplicationScoped} resources behave correctly
 * when deployed together, verifying scope isolation between the two.
 */
@RestBootstrap(value = { CdiMixedScopeTest.RequestScopedResource.class, CdiMixedScopeTest.AppScopedResource.class })
@Tag("cdi")
class CdiMixedScopeTest {

    @Test
    void requestScoped(@RestResource @RequestPath("request-scoped/count") final WebTarget target) {
        final int first = target.request().get(int.class);
        Assertions.assertEquals(1, first);

        final int second = target.request().get(int.class);
        Assertions.assertEquals(1, second, "RequestScoped resource state should reset per request");
    }

    @Test
    void appScopedResource(@RestResource @RequestPath("app-scoped/count") final WebTarget target) {
        final int first = target.request().get(int.class);
        Assertions.assertEquals(1, first);
        final int second = target.request().get(int.class);
        Assertions.assertEquals(2, second);
    }

    @Test
    void requestScopedInjectsAppScoped(
            @RestResource @RequestPath("request-scoped/shared") final WebTarget target) {
        final String result = target.request().get(String.class);
        Assertions.assertEquals("shared-state", result);
    }

    @ApplicationScoped
    public static class SharedService {
        public String getValue() {
            return "shared-state";
        }
    }

    @Path("/request-scoped")
    @RequestScoped
    public static class RequestScopedResource {
        private int count;

        @Inject
        SharedService sharedService;

        @GET
        @Path("/count")
        @Produces(MediaType.TEXT_PLAIN)
        public int count() {
            return ++count;
        }

        @GET
        @Path("/shared")
        @Produces(MediaType.TEXT_PLAIN)
        public String shared() {
            return sharedService.getValue();
        }
    }

    @Path("/app-scoped")
    @ApplicationScoped
    public static class AppScopedResource {
        private final AtomicInteger count = new AtomicInteger();

        @GET
        @Path("/count")
        @Produces(MediaType.TEXT_PLAIN)
        public int count() {
            return count.incrementAndGet();
        }
    }
}
