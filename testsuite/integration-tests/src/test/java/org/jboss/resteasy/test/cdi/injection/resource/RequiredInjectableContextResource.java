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

package org.jboss.resteasy.test.cdi.injection.resource;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.CompletionStage;
import java.util.function.BiConsumer;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Client;
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
import jakarta.ws.rs.ext.Providers;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;

import org.jboss.resteasy.spi.HttpRequest;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@Path("/inject")
@Produces(MediaType.TEXT_PLAIN)
public class RequiredInjectableContextResource {

    @Inject
    Application application;
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
}
