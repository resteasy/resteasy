/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2024 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.resteasy.spi.model;

import java.util.Set;

import jakarta.ws.rs.core.MediaType;

/**
 * Represents an entity provider like a {@link jakarta.ws.rs.ext.MessageBodyReader} or
 * {@link jakarta.ws.rs.ext.MessageBodyWriter}.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public interface EntityProvider<T> {

    /**
     * Returns the provider instance.
     *
     * @return the provider instance
     */
    T provider();

    // TODO (jrp) is this really what we want to do?
    default boolean isAssignableTo(final Class<?> type) {
        return providerType().isAssignableFrom(type);
    }

    // TODO (jrp) do we want both consumes and produces or should it just be supportedMediaTypes() and a MBW uses Produces and a MBR uses Consumes?
    Set<MediaType> consumes();

    Set<MediaType> produces();

    /**
     * Indicates whether this is a build-in provider.
     *
     * @return {@code true} if this is a build-in provider, otherwise {@code false}
     */
    boolean isBuiltIn();

    /**
     * The priority of the provider.
     *
     * @return the priority
     */
    int priority();

    /**
     * The type for the provider. This is the generic type of the provider interface implemented.
     *
     * @return the provider type
     */
    Class<?> providerType();
}
