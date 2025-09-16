/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.resteasy.client.util.authentication.basic;

import java.io.IOException;

import jakarta.annotation.Priority;
import jakarta.ws.rs.ConstrainedTo;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;

import dev.resteasy.client.util.authentication.UserCredentials;

/**
 * A simple {@linkplain ClientRequestFilter filter} which always adds a {@code Authorization} header with a Basic
 * authorization value.
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
@ConstrainedTo(RuntimeType.CLIENT)
@Priority(Priorities.AUTHORIZATION)
public class BasicAuthorizationFilter implements ClientRequestFilter {
    private final BasicAuthorizationProcessor processor;

    /**
     * Creates a new filter.
     *
     * @param credentials the credentials to use for authorization
     */
    public BasicAuthorizationFilter(final UserCredentials credentials) {
        this.processor = new BasicAuthorizationProcessor(credentials);
    }

    /**
     * Creates a new filter.
     *
     * @param credentials the credentials to use for authorization
     *
     * @return a new filter
     */
    public static BasicAuthorizationFilter create(final UserCredentials credentials) {
        return new BasicAuthorizationFilter(credentials);
    }

    @Override
    public void filter(final ClientRequestContext requestContext) throws IOException {
        requestContext.getHeaders().putSingle(HttpHeaders.AUTHORIZATION, processor.createRequestHeader(requestContext));
    }
}
