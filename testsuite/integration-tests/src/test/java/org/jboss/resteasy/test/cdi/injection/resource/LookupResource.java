/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.resteasy.test.cdi.injection.resource;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.ResourceContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;

@Path("/lookup")
@RequestScoped
public class LookupResource {

    @Context
    ResourceContext resourceContext;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String lookup() {
        return resourceContext.getResource(ConstructorInjectedResource.class).path();
    }
}
