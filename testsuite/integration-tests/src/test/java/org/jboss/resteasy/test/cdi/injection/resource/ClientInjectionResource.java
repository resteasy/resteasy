/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.cdi.injection.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.core.MediaType;

/**
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
@Path("/inject")
@Produces(MediaType.TEXT_PLAIN)
public class ClientInjectionResource {
    @Inject
    Client client;

    @GET
    @Path("/client/")
    public Object client() {
        return client.getConfiguration().getProperty("test.client.property");
    }
}
