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
import java.io.UncheckedIOException;
import java.util.concurrent.CompletionStage;
import java.util.function.BiConsumer;

import jakarta.enterprise.context.RequestScoped;
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
import org.junit.jupiter.api.extension.ExtensionContext;

import dev.resteasy.junit.extension.annotations.RestBootstrap;
import dev.resteasy.junit.extension.api.ConfigurationProvider;

/**
 * Tests fields annotated with {@link Context @Context} are injected with the expected values.
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
@RestBootstrap(application = ContextInjectionTest.RootApplication.class, configFactory = ContextInjectionTest.InjectionConfiguration.class)
public class ContextInjectionTest extends AbstractContextInjectionTest {
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
    public static class InjectionResource {
        @Context
        RootApplication application;
        @Context
        Configuration configuration;
        @Context
        HttpHeaders httpHeaders;
        @Context
        HttpRequest httpRequest;
        @Context
        Providers providers;
        @Context
        Request request;
        @Context
        ResourceContext resourceContext;
        @Context
        ResourceInfo resourceInfo;
        @Context
        SecurityContext securityContext;
        @Context
        Sse sse;
        @Context
        UriInfo uriInfo;

        // Servlet types given we're in a Jakarta Servlet Container
        @Context
        HttpServletRequest httpServletRequest;
        @Context
        HttpServletResponse httpServletResponse;
        @Context
        ServletConfig servletConfig;
        @Context
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

        // Unannotated parameters of the context types

        @GET
        @Path("/parameter/httpRequest")
        public Response httpRequestParameter(final HttpRequest httpRequest) {
            return Response.ok(httpRequest.getHttpMethod()).build();
        }

        @GET
        @Path("/parameter/servletRequest")
        public Response servletRequestParameter(final HttpServletRequest httpServletRequest) {
            return Response.ok(httpServletRequest.getMethod()).build();
        }

        @GET
        @Path("/parameter/servletResponse")
        public Response servletResponseParameter(final HttpServletResponse httpServletResponse) {
            return Response.ok(httpServletResponse.getStatus()).build();
        }

        @GET
        @Path("/parameter/servletContext")
        public Response servletContextParameter(final ServletContext servletContext) {
            return Response.ok(servletContext.getContextPath()).build();
        }

        @GET
        @Path("/parameter/servletConfig")
        public Response servletConfigParameter(final ServletConfig servletConfig) {
            return Response.ok(servletConfig.getServletName()).build();
        }
    }
}
