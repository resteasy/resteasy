/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.resteasy.test.cdi.injection.resource;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * Annotated with {@link RequestScoped}, but without an {@code @Inject} or no-arg constructor, so CDI does not register
 * it as a bean and RESTEasy creates it.
 */
@Path("/contact/{id}")
@RequestScoped
public class InvalidCdiBeanContactResource {
    private final String id;

    public InvalidCdiBeanContactResource(@PathParam("id") final String id) {
        this.id = id;
    }

    @GET
    @Path("details")
    @Produces(MediaType.TEXT_PLAIN)
    public String details() {
        return String.format("ID: %s", id);
    }
}
