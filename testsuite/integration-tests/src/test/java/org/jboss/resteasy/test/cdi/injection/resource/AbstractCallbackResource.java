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
 * Abstract resource class with a class-level @Path annotation.
 * This class is intentionally abstract and has no concrete implementations in the deployment.
 *
 * Prior to the fix for RESTEASY-3832, the presence of this class in a deployment with
 * bean-discovery-mode="all" would cause the entire application to fail during deployment.
 *
 * The fix ensures that abstract classes annotated with @Path are skipped during CDI
 * bean discovery, preventing deployment failures.
 */
@Path("/callback")
@Produces(MediaType.TEXT_PLAIN)
public abstract class AbstractCallbackResource {

    @GET
    public abstract String hello();
}
