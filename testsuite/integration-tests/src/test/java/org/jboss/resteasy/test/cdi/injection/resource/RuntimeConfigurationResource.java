/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.cdi.injection.resource;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

/**
 * Sends a {@link RuntimeTrace} from a client in the container to the {@code echo} endpoint and returns the entries
 * recorded by each provider, one per line.
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
@Path("/runtime")
public class RuntimeConfigurationResource {

    @Inject
    RuntimeTraceProvider traceProvider;

    @Inject
    ClientConfigurationFilter clientFilter;

    @Inject
    @Context
    UriInfo uriInfo;

    @POST
    @Path("/echo")
    @Consumes(RuntimeTrace.MEDIA_TYPE)
    @Produces(RuntimeTrace.MEDIA_TYPE)
    public RuntimeTrace echo(final RuntimeTrace trace) {
        return trace;
    }

    @GET
    @Path("/sync")
    @Produces(MediaType.TEXT_PLAIN)
    public String sync() {
        try (Client client = createClient(); Response response = request(client).post(entity())) {
            return trace(response);
        }
    }

    @GET
    @Path("/async")
    @Produces(MediaType.TEXT_PLAIN)
    public String async() throws Exception {
        try (Client client = createClient();
                Response response = request(client).async().post(entity()).get(10, TimeUnit.SECONDS)) {
            return trace(response);
        }
    }

    private Client createClient() {
        return ClientBuilder.newBuilder()
                .property(RuntimeTrace.NAME_PROPERTY, "client")
                .register(traceProvider)
                .register(clientFilter)
                .register(new ClientNameResolver("client"))
                .build();
    }

    private Invocation.Builder request(final Client client) {
        final URI uri = uriInfo.getBaseUriBuilder().path(RuntimeConfigurationResource.class).path("echo").build();
        return client.target(uri).request(RuntimeTrace.MEDIA_TYPE);
    }

    private static Entity<RuntimeTrace> entity() {
        return Entity.entity(new RuntimeTrace(List.of()), RuntimeTrace.MEDIA_TYPE);
    }

    private static String trace(final Response response) {
        if (response.getStatus() != Response.Status.OK.getStatusCode()) {
            return "echo=%d:%s".formatted(response.getStatus(), response.readEntity(String.class));
        }
        final List<String> lines = new ArrayList<>(response.readEntity(RuntimeTrace.class).entries());
        lines.add("%s=%s".formatted(ServerConfigurationFilter.HEADER,
                response.getHeaderString(ServerConfigurationFilter.HEADER)));
        lines.add("%s=%s".formatted(ClientConfigurationFilter.HEADER,
                response.getHeaderString(ClientConfigurationFilter.HEADER)));
        return String.join("\n", lines);
    }
}
