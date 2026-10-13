/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.cdi.injection.resource;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.client.ClientResponseFilter;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.ext.Providers;

/**
 * Records the runtime the injected context objects resolve to on the client. This is not annotated with
 * {@link jakarta.ws.rs.ext.Provider @Provider}, so it is only registered with the client.
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
@ApplicationScoped
public class ClientConfigurationFilter implements ClientResponseFilter {
    public static final String HEADER = "client-filter";

    @Inject
    @Context
    Configuration configuration;

    @Inject
    @Context
    Providers providers;

    @Override
    public void filter(final ClientRequestContext requestContext, final ClientResponseContext responseContext) {
        responseContext.getHeaders().add(HEADER, RuntimeTrace.describe(configuration, providers));
    }
}
