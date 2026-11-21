/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.resteasy.test.cdi.injection.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * An abstract resource, which is never the bean class of a managed bean. If it is treated as a CDI bean, the
 * deployment fails.
 */
@Path("/unreachable/")
public abstract class InvalidAbstractCdiBeanContactResource {

    @GET
    @Path("details")
    @Produces(MediaType.TEXT_PLAIN)
    public abstract String details();
}
