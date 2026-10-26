/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.cdi.injection.resource;

import jakarta.ws.rs.ext.ContextResolver;

/**
 * A resolver which is only registered on the client, so a {@link jakarta.ws.rs.ext.Providers} which resolves it
 * belongs to the client.
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
public class ClientNameResolver implements ContextResolver<ClientNameResolver.Name> {
    private final Name name;

    public ClientNameResolver(final String name) {
        this.name = new Name(name);
    }

    @Override
    public Name getContext(final Class<?> type) {
        return name;
    }

    public record Name(String value) {
    }
}
