/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2022 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.resteasy.embedded.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.SeBootstrap;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.container.ResourceContext;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.Providers;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;
import jakarta.ws.rs.sse.SseEventSource;

import org.jboss.jandex.Index;
import org.jboss.resteasy.spi.HttpRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import dev.resteasy.junit.extension.annotations.RequestPath;
import dev.resteasy.junit.extension.annotations.RestBootstrap;
import dev.resteasy.junit.extension.api.ConfigurationProvider;

/**
 * Tests injection of the known types from the {@link org.jboss.resteasy.cdi.ContextProducers} are injectable.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@RestBootstrap(value = ContextInjectionTest.RootApplication.class, configFactory = ContextInjectionTest.InjectionConfiguration.class)
public class ContextInjectionTest {
    public static class InjectionConfiguration implements ConfigurationProvider {
        @Override
        public SeBootstrap.Configuration getConfiguration() {
            try {
                final Index index = Index.of(InjectionResource.class, RootApplication.class, TestExceptionMapper.class);
                return TestEnvironment.createConfig(index);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    @Test
    public void application(@RequestPath("inject/application/test.property") final WebTarget target) throws Exception {
        final Response response = get(target);
        Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
        Assertions.assertEquals("test value", response.readEntity(String.class));
    }

    @Test
    public void client(@RequestPath("inject/client/request") final WebTarget target) throws Exception {
        final Response response = get(target);
        Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
        Assertions.assertEquals("GET", response.readEntity(String.class));
    }

    @Test
    public void configuration(@RequestPath("inject/configuration") final WebTarget target) {
        final Response response = get(target);
        Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
        Assertions.assertEquals(RuntimeType.SERVER.name(), response.readEntity(String.class));
    }

    @Test
    public void httpHeader(@RequestPath("inject/httpHeaders/test-header") final WebTarget target) {
        final Response response = get(target);
        Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
        Assertions.assertEquals("test-value", response.readEntity(String.class));
    }

    @Test
    public void httpRequest(@RequestPath("inject/httpRequest") final WebTarget target) {
        final Response response = get(target);
        Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
        Assertions.assertEquals("GET", response.readEntity(String.class));
    }

    @Test
    public void provider(@RequestPath("inject/providers") final WebTarget target) throws Exception {
        final Response response = get(target);
        Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
        final String value = response.readEntity(String.class);
        Assertions.assertTrue(value.contains(TestExceptionMapper.class.getSimpleName()),
                String.format("Value expected to contain %s but was %s", TestExceptionMapper.class.getSimpleName(), value));
    }

    @Test
    public void request(@RequestPath("inject/request") final WebTarget target) {
        final Response response = get(target);
        Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
        Assertions.assertEquals("GET", response.readEntity(String.class));
    }

    @Test
    public void resourceContext(@RequestPath("inject/resourceContext") final WebTarget target) {
        final Response response = get(target);
        Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
        Assertions.assertTrue(response.readEntity(String.class)
                .startsWith(InjectionResource.class.getName()));
    }

    @Test
    public void resourceInfo(@RequestPath("inject/resourceInfo") final WebTarget target) {
        final Response response = get(target);
        Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
        Assertions.assertEquals(response.readEntity(String.class), "resourceInfo");
    }

    @Test
    public void securityContext(@RequestPath("inject/securityContext") final WebTarget target) {
        final Response response = get(target);
        Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
        Assertions.assertEquals("false", response.readEntity(String.class));
    }

    @Test
    public void uriInfo(@RequestPath("inject/uriInfo") final WebTarget target) {
        final Response response = get(target);
        Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
        Assertions.assertEquals("/inject/uriInfo", response.readEntity(String.class));
    }

    @Test
    public void sse(@RequestPath("inject/sse") final WebTarget target) throws Exception {
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

    @Path("/inject")
    @Produces(MediaType.TEXT_PLAIN)
    @RequestScoped
    @SuppressWarnings("CdiInjectionPointsInspection")
    public static class InjectionResource {
        @Inject
        RootApplication application;
        @Inject
        Client client;
        @Inject
        Configuration configuration;
        @Inject
        HttpHeaders httpHeaders;
        @Inject
        HttpRequest httpRequest;
        @Inject
        Providers providers;
        @Inject
        Request request;
        @Inject
        ResourceContext resourceContext;
        @Inject
        ResourceInfo resourceInfo;
        @Inject
        SecurityContext securityContext;
        @Inject
        Sse sse;
        @Inject
        UriInfo uriInfo;

        @GET
        @Path("/application/{propertyName}")
        public Response application(@PathParam("propertyName") final String propertyName) {
            return Response.ok(application.getProperties().get(propertyName)).build();
        }

        @GET
        @Path("/configuration")
        public Response configuration() {
            return Response.ok(configuration.getRuntimeType()).build();
        }

        @GET
        @Path("/httpHeaders/{name}")
        public Response httpHeaders(@PathParam("name") final String name) {
            return Response.ok(httpHeaders.getHeaderString(name)).build();
        }

        @GET
        @Path("/httpRequest")
        public Response httpRequest() {
            return Response.ok(httpRequest.getHttpMethod()).build();
        }

        @GET
        @Path("/providers")
        public Response providers() {
            return Response.ok(providers.getExceptionMapper(IllegalStateException.class).getClass().getCanonicalName())
                    .build();
        }

        @GET
        @Path("/request")
        public Response request() {
            return Response.ok(request.getMethod()).build();
        }

        @GET
        @Path("resourceContext")
        public Response resourceContext() {
            return Response.ok(resourceContext.getResource(getClass()).getClass().getCanonicalName()).build();
        }

        @GET
        @Path("resourceInfo")
        public Response resourceInfo() {
            return Response.ok(resourceInfo.getResourceMethod().getName()).build();
        }

        @GET
        @Path("/securityContext")
        public Response securityContext() {
            return Response.ok(securityContext.isSecure()).build();
        }

        @GET
        @Path("/sse")
        @Produces(MediaType.SERVER_SENT_EVENTS)
        public CompletionStage<?> sse(@Context final SseEventSink eventSink) {
            if (eventSink == null) {
                throw new WebApplicationException("No client connected.");
            }
            return eventSink.send(sse.newEvent("test"))
                    .whenComplete((BiConsumer<Object, Throwable>) (unused, throwable) -> {
                        try {
                            eventSink.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }

        @GET
        @Path("/uriInfo")
        public Response uriInfo() {
            return Response.ok(uriInfo.getPath()).build();
        }

        @GET
        @Path("/client/{path}")
        public Response client(@PathParam("path") final String path) {
            return client.target(uriInfo.getBaseUriBuilder().path("inject/" + path))
                    .request()
                    .get();
        }
    }
}
