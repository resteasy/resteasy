/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.resteasy.embedded.server;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.CompletionStage;
import java.util.function.BiConsumer;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.SeBootstrap;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.container.ResourceContext;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Providers;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;

import org.jboss.jandex.Index;
import org.jboss.resteasy.spi.HttpRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;

import dev.resteasy.junit.extension.annotations.RequestPath;
import dev.resteasy.junit.extension.annotations.RestBootstrap;
import dev.resteasy.junit.extension.annotations.RestResource;
import dev.resteasy.junit.extension.api.ConfigurationProvider;

/**
 * Tests fields annotated with {@link Inject @Inject} are injected with the expected values.
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
@SuppressWarnings("JUnitMalformedDeclaration")
@RestBootstrap(application = CdiContextInjectionTest.RootApplication.class, configFactory = CdiContextInjectionTest.InjectionConfiguration.class)
public class CdiContextInjectionTest extends AbstractContextInjectionTest {

    @Test
    public void client(@RestResource @RequestPath("inject/client/request") final WebTarget target) {
        try (Response response = target.request().get()) {
            Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
            Assertions.assertEquals("GET", response.readEntity(String.class));
        }
    }

    public static class InjectionConfiguration implements ConfigurationProvider {
        @Override
        public SeBootstrap.Configuration getConfiguration(final ExtensionContext context) {
            try {
                final Index index = Index.of(InjectionResource.class, RootApplication.class, TestExceptionMapper.class);
                return TestEnvironment.createConfig(index);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
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

        // Servlet types given we're in a Jakarta Servlet Container
        @Inject
        HttpServletRequest httpServletRequest;
        @Inject
        HttpServletResponse httpServletResponse;
        @Inject
        ServletConfig servletConfig;
        @Inject
        ServletContext servletContext;

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
            final Object resource = resourceContext.getResource(getClass());
            if (resource == null) {
                throw new WebApplicationException(
                        String.format("Failed to find resource %s in %s", getClass(), resourceContext));
            }
            return Response.ok("ok").build();
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
        public CompletionStage<?> sse(@Context final SseEventSink eventSink) throws IOException {
            if (eventSink == null) {
                throw new WebApplicationException("No client connected.");
            }
            return eventSink.send(sse.newEvent("test"))
                    .whenComplete((BiConsumer<Object, Throwable>) (unused, throwable) -> {
                        try {
                            eventSink.close();
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
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

        @GET
        @Path("/servletRequest")
        public Response servletRequest() {
            return Response.ok(httpServletRequest.getMethod()).build();
        }

        @GET
        @Path("/servletResponse")
        public Response servletResponse() {
            return Response.ok(httpServletResponse.getStatus()).build();
        }

        @GET
        @Path("servletContext")
        public Response servletContext() {
            return Response.ok(servletContext.getContextPath()).build();
        }

        @GET
        @Path("servletConfig")
        public Response servletConfig() {
            return Response.ok(servletConfig.getServletName()).build();
        }
    }
}
