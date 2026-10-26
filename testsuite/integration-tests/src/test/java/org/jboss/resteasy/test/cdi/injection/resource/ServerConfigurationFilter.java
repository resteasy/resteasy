/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.cdi.injection.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.Providers;

/**
 * Records the runtime the injected context objects resolve to on the server.
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
@Provider
public class ServerConfigurationFilter implements ContainerResponseFilter {
    public static final String HEADER = "server-filter";

    @Inject
    @Context
    Configuration configuration;

    @Inject
    @Context
    Providers providers;

    @Override
    public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext) {
        responseContext.getHeaders().add(HEADER, RuntimeTrace.describe(configuration, providers));
    }
}
