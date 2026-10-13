/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.resteasy.test.injection;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.container.ResourceContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import dev.resteasy.junit.extension.annotations.RequestPath;
import dev.resteasy.junit.extension.annotations.RestBootstrap;
import dev.resteasy.junit.extension.annotations.RestResource;

/**
 * Tests that {@link ResourceContext#getResource(Class)} creates a CDI bean which only has an {@link Inject @Inject}
 * constructor with a parameter that is not annotated with {@link Context @Context}.
 */
@RestBootstrap(value = { ResourceContextConstructorTest.LookupResource.class,
        ResourceContextConstructorTest.ConstructorInjectedResource.class })
@Tag("cdi")
class ResourceContextConstructorTest {

    @Test
    void constructorInjected(@RestResource @RequestPath("lookup") final WebTarget target) {
        Assertions.assertEquals("/lookup", target.request().get(String.class));
    }

    @Path("/constructor-injected")
    public static class ConstructorInjectedResource {
        private final UriInfo uriInfo;

        @Inject
        public ConstructorInjectedResource(final UriInfo uriInfo) {
            this.uriInfo = uriInfo;
        }

        @GET
        @Produces(MediaType.TEXT_PLAIN)
        public String path() {
            return uriInfo.getPath();
        }
    }

    @Path("/lookup")
    @RequestScoped
    public static class LookupResource {

        @Context
        ResourceContext resourceContext;

        @GET
        @Produces(MediaType.TEXT_PLAIN)
        public String lookup() {
            return resourceContext.getResource(ConstructorInjectedResource.class).path();
        }
    }
}
