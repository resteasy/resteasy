/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.embedded.test.interceptor.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Path("/")
public class ClientResource {
    @Context
    private UriInfo uriInfo;

    @GET
    @Path("testIt")
    public Response get() {
        // we need to create new client to verify that @Provider works
        Client client = ClientBuilder.newClient();
        try {
            WebTarget base = client.target(uriInfo.getBaseUriBuilder().path("clientInvoke").build());
            Response response = base.request().get();

            // return the client invocation response to make the verification in test class
            return response;
        } finally {
            client.close();
        }
    }

    @GET
    @Path("clientInvoke")
    public Response clientInvoke() {
        return Response.ok().build();
    }
}
