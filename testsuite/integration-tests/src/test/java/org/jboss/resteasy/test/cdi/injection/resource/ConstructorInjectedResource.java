/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.resteasy.test.cdi.injection.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;

@Path("/constructor-injected")
public class ConstructorInjectedResource {
    private final UriInfo uriInfo;

    ConstructorInjectedResource() {
        this.uriInfo = null;
    }

    @Inject
    public ConstructorInjectedResource(final UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String path() {
        return uriInfo.getPath();
    }
}
