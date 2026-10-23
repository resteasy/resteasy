/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.resteasy.test.injection;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Alternative;
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
 * Tests that an enabled {@link Alternative @Alternative} for a CDI managed resource is the bean used to handle the
 * request.
 */
@RestBootstrap(CdiAlternativeResourceTest.OrderResource.class)
@Tag("cdi")
class CdiAlternativeResourceTest {

    @Test
    void alternativeResourceInvoked(@RestResource @RequestPath("orders") final WebTarget target) {
        Assertions.assertEquals("alternative", target.request().get(String.class));
    }

    @Path("/orders")
    @RequestScoped
    public static class OrderResource {

        @GET
        @Produces(MediaType.TEXT_PLAIN)
        public String get() {
            return "original";
        }
    }

    @Alternative
    @Priority(1)
    @RequestScoped
    public static class AlternativeOrderResource extends OrderResource {

        @Override
        public String get() {
            return "alternative";
        }
    }
}
