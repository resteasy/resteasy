/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.resteasy.embedded.test.cdi;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import dev.resteasy.junit.extension.annotations.RequestPath;
import dev.resteasy.junit.extension.annotations.RestBootstrap;
import dev.resteasy.junit.extension.annotations.RestResource;

/**
 * Tests for CDI integration with an embedded server started via {@link jakarta.ws.rs.SeBootstrap}.
 */
@RestBootstrap(value = { SeBootstrapCdiTest.CdiResource.class })
@Tag("cdi")
class SeBootstrapCdiTest {

    @Test
    void cdiInjection(@RestResource @RequestPath("cdi/greeting") final WebTarget target) {
        final String result = target.request().get(String.class);
        Assertions.assertEquals("Hello from CDI", result);
    }

    @Test
    void requestScopedIsolation(@RestResource @RequestPath("cdi/request-scoped") final WebTarget target) {
        // Each request gets a fresh @RequestScoped bean, so counter always starts at 0
        final int first = target.request().get(int.class);
        Assertions.assertEquals(1, first);

        final int second = target.request().get(int.class);
        Assertions.assertEquals(1, second, "RequestScoped bean should be fresh per request");
    }

    @Test
    void responseStatus(@RestResource @RequestPath("cdi/greeting") final WebTarget target) {
        try (Response response = target.request().get()) {
            Assertions.assertEquals(200, response.getStatus());
        }
    }

    @ApplicationScoped
    public static class GreetingService {
        public String greet() {
            return "Hello from CDI";
        }
    }

    @RequestScoped
    public static class RequestScopedCounter {
        private int count;

        public int increment() {
            return ++count;
        }
    }

    @Path("/cdi")
    public static class CdiResource {

        @Inject
        GreetingService greetingService;

        @Inject
        RequestScopedCounter counter;

        @GET
        @Path("/greeting")
        @Produces(MediaType.TEXT_PLAIN)
        public String greeting() {
            return greetingService.greet();
        }

        @GET
        @Path("/request-scoped")
        @Produces(MediaType.TEXT_PLAIN)
        public int requestScoped() {
            return counter.increment();
        }
    }
}
