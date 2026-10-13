/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.cdi.injection.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Providers;

/**
 * The entries recorded by each provider which handles the entity, in the order the providers were invoked.
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
public record RuntimeTrace(List<String> entries) {
    public static final String MEDIA_TYPE = "application/x-runtime-trace";
    public static final String NAME_PROPERTY = "runtime.name";

    public RuntimeTrace add(final String entry) {
        final List<String> copy = new ArrayList<>(entries);
        copy.add(entry);
        return new RuntimeTrace(List.copyOf(copy));
    }

    /**
     * Describes the runtime the injected context objects resolve to. The name property is only set on the client and
     * the {@link ClientNameResolver} is only registered on the client.
     *
     * @param configuration the injected configuration
     * @param providers     the injected providers
     *
     * @return the runtime type, name property and resolved client name, each of which is the failure if it could not
     *         be resolved
     */
    public static String describe(final Configuration configuration, final Providers providers) {
        return "%s:%s:%s".formatted(configuration.getRuntimeType(),
                Objects.requireNonNullElse(configuration.getProperty(NAME_PROPERTY), "none"),
                resolve(() -> {
                    final ContextResolver<ClientNameResolver.Name> resolver = providers
                            .getContextResolver(ClientNameResolver.Name.class, MediaType.WILDCARD_TYPE);
                    final ClientNameResolver.Name name = resolver == null ? null : resolver.getContext(RuntimeTrace.class);
                    return name == null ? "none" : name.value();
                }));
    }

    private static Object resolve(final Supplier<Object> supplier) {
        try {
            return supplier.get();
        } catch (RuntimeException e) {
            return "failed(%s)".formatted(e.getMessage());
        }
    }
}
