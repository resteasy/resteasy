/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.cdi.injection.resource;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/library-test")
@RequestScoped
public class LibraryProducerResource {

    @Inject
    HttpServletResponse injectedResponse;

    @Inject
    Client injectedClient;

    @Context
    HttpServletResponse contextResponse;

    @GET
    @Path("/response")
    @Produces(MediaType.TEXT_PLAIN)
    public Response testResponse() {
        return Response.ok(injectedResponse.getHeader("X-Library-Producer")).build();
    }

    @GET
    @Path("/client")
    @Produces(MediaType.TEXT_PLAIN)
    public Response testClient() {
        return Response.ok(injectedClient.getConfiguration().getProperty("test.library.producer")).build();
    }

    @GET
    @Path("/context-response")
    @Produces(MediaType.TEXT_PLAIN)
    public Response testContextResponse() {
        return Response.ok(contextResponse != null ? "ok" : "null").build();
    }
}
