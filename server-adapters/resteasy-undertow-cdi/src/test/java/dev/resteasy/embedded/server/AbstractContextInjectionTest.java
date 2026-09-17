/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.resteasy.embedded.server;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.sse.SseEventSource;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import dev.resteasy.junit.extension.annotations.RequestPath;
import dev.resteasy.junit.extension.annotations.RestResource;

/**
 * Tests injection of required injection types.
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
@SuppressWarnings("JUnitMalformedDeclaration")
abstract class AbstractContextInjectionTest {

    @Test
    public void application(@RestResource @RequestPath("inject/application/test.property") final WebTarget target) {
        try (Response response = get(target)) {
            Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
            Assertions.assertEquals("test value", response.readEntity(String.class));
        }
    }

    @Test
    public void configuration(@RestResource @RequestPath("inject/configuration") final WebTarget target) {
        try (Response response = get(target)) {
            Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
            Assertions.assertEquals(RuntimeType.SERVER.name(), response.readEntity(String.class));
        }
    }

    @Test
    public void httpHeader(@RestResource @RequestPath("inject/httpHeaders/test-header") final WebTarget target) {
        try (Response response = get(target)) {
            Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
            Assertions.assertEquals("test-value", response.readEntity(String.class));
        }
    }

    @Test
    public void httpRequest(@RestResource @RequestPath("inject/httpRequest") final WebTarget target) {
        try (Response response = get(target)) {
            Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
            Assertions.assertEquals("GET", response.readEntity(String.class));
        }
    }

    @Test
    public void provider(@RestResource @RequestPath("inject/providers") final WebTarget target) {
        try (Response response = get(target)) {
            Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
            final String value = response.readEntity(String.class);
            Assertions.assertTrue(value.contains(TestExceptionMapper.class.getSimpleName()),
                    String.format("Value expected to contain %s but was %s", TestExceptionMapper.class.getSimpleName(), value));
        }
    }

    @Test
    public void request(@RestResource @RequestPath("inject/request") final WebTarget target) {
        try (Response response = get(target)) {
            Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
            Assertions.assertEquals("GET", response.readEntity(String.class));
        }
    }

    @Test
    public void resourceContext(@RestResource @RequestPath("inject/resourceContext") final WebTarget target) {
        try (Response response = get(target)) {
            Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
            Assertions.assertEquals("ok", response.readEntity(String.class));
        }
    }

    @Test
    public void resourceInfo(@RestResource @RequestPath("inject/resourceInfo") final WebTarget target) {
        try (Response response = get(target)) {
            Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
            Assertions.assertEquals("resourceInfo", response.readEntity(String.class));
        }
    }

    @Test
    public void securityContext(@RestResource @RequestPath("inject/securityContext") final WebTarget target) {
        try (Response response = get(target)) {
            Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
            Assertions.assertEquals("false", response.readEntity(String.class));
        }
    }

    @Test
    public void uriInfo(@RestResource @RequestPath("inject/uriInfo") final WebTarget target) {
        try (Response response = get(target)) {
            Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
            Assertions.assertEquals("/inject/uriInfo", response.readEntity(String.class));
        }
    }

    @Test
    public void sse(@RestResource @RequestPath("inject/sse") final WebTarget target) throws Exception {
        final CompletableFuture<String> cf = new CompletableFuture<>();
        try (SseEventSource source = SseEventSource.target(target).build()) {
            source.register(event -> {
                try {
                    cf.complete(event.readData());
                } catch (Throwable t) {
                    cf.completeExceptionally(t);
                }
            });
            source.open();
            Thread.sleep(500L);
        }
        Assertions.assertEquals("test", cf.get(5, TimeUnit.SECONDS));
    }

    @Test
    public void servletRequest(@RestResource @RequestPath("inject/servletRequest") final WebTarget target) {
        try (Response response = target.request().get()) {
            Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
            Assertions.assertEquals("GET", response.readEntity(String.class));
        }
    }

    @Test
    public void servletResponse(@RestResource @RequestPath("inject/servletResponse") final WebTarget target) {
        try (Response response = target.request().get()) {
            Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
            Assertions.assertEquals("200", response.readEntity(String.class));
        }
    }

    @Test
    public void servletContext(@RestResource @RequestPath("inject/servletContext") final WebTarget target) {
        try (Response response = target.request().get()) {
            Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
            Assertions.assertEquals("", response.readEntity(String.class));
        }
    }

    @Test
    public void servletConfig(@RestResource @RequestPath("inject/servletConfig") final WebTarget target) {
        try (Response response = target.request().get()) {
            Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
            Assertions.assertEquals("ResteasyServlet", response.readEntity(String.class));
        }
    }

    private Response get(final WebTarget target) {
        return target
                .request()
                .header("test-header", "test-value")
                .get();
    }

    @Provider
    public static class TestExceptionMapper implements ExceptionMapper<IllegalStateException> {

        @Override
        public Response toResponse(final IllegalStateException exception) {
            final StringWriter writer = new StringWriter();
            exception.printStackTrace(new PrintWriter(writer));
            return Response.serverError()
                    .entity(writer.toString())
                    .build();
        }
    }

    @ApplicationPath("/")
    @ApplicationScoped
    public static class RootApplication extends Application {
        @Override
        public Map<String, Object> getProperties() {
            return Collections.singletonMap("test.property", "test value");
        }
    }
}
