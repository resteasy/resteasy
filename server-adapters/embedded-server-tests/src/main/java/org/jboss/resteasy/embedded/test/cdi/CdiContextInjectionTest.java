/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.resteasy.embedded.test.cdi;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriInfo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import dev.resteasy.junit.extension.annotations.RequestPath;
import dev.resteasy.junit.extension.annotations.RestBootstrap;
import dev.resteasy.junit.extension.annotations.RestResource;

/**
 * Tests that standard Jakarta REST context types can be injected via {@code @Inject} when CDI is active.
 * These are produced by RESTEasy CDI's {@code ContextProducers}.
 */
@RestBootstrap(value = CdiContextInjectionTest.ContextResource.class)
@Tag("cdi")
class CdiContextInjectionTest {

    @Test
    void uriInfo(@RestResource @RequestPath("context/uriInfo") final WebTarget target) {
        try (Response response = target.request().get()) {
            final String value = response.readEntity(String.class);
            Assertions.assertEquals(200, response.getStatus(), () -> "Expected a status of 200: %s".formatted(value));
            Assertions.assertEquals("/context/uriInfo", value);
        }
    }

    @Test
    void httpHeaders(@RestResource @RequestPath("context/httpHeaders") final WebTarget target) {
        try (Response response = target.request().header("X-Test-Header", "test-value").get()) {
            final String value = response.readEntity(String.class);
            Assertions.assertEquals(200, response.getStatus(), () -> "Expected a status of 200: %s".formatted(value));
            Assertions.assertEquals("test-value", value);
        }
    }

    @Test
    void request(@RestResource @RequestPath("context/request") final WebTarget target) {
        try (Response response = target.request().get()) {
            final String value = response.readEntity(String.class);
            Assertions.assertEquals(200, response.getStatus(), () -> "Expected a status of 200: %s".formatted(value));
            Assertions.assertEquals("GET", value);
        }
    }

    @Test
    void securityContext(@RestResource @RequestPath("context/securityContext") final WebTarget target) {
        try (Response response = target.request().get()) {
            final String value = response.readEntity(String.class);
            Assertions.assertEquals(200, response.getStatus(), () -> "Expected a status of 200: %s".formatted(value));
            Assertions.assertEquals("false", value);
        }
    }

    @Test
    void configuration(@RestResource @RequestPath("context/configuration") final WebTarget target) {
        try (Response response = target.request().get()) {
            final String value = response.readEntity(String.class);
            Assertions.assertEquals(200, response.getStatus(), () -> "Expected a status of 200: %s".formatted(value));
            Assertions.assertEquals(RuntimeType.SERVER.name(), value);
        }
    }

    @Path("/context")
    @Produces(MediaType.TEXT_PLAIN)
    public static class ContextResource {

        @Inject
        UriInfo uriInfo;

        @Inject
        HttpHeaders httpHeaders;

        @Inject
        Request request;

        @Inject
        SecurityContext securityContext;

        @Inject
        Configuration configuration;

        @GET
        @Path("/uriInfo")
        public String uriInfo() {
            return uriInfo.getPath();
        }

        @GET
        @Path("/httpHeaders")
        public String httpHeaders() {
            return httpHeaders.getHeaderString("X-Test-Header");
        }

        @GET
        @Path("/request")
        public String request() {
            return request.getMethod();
        }

        @GET
        @Path("/securityContext")
        public String securityContext() {
            return String.valueOf(securityContext.isSecure());
        }

        @GET
        @Path("/configuration")
        public String configuration() {
            return configuration.getRuntimeType().name();
        }
    }
}
