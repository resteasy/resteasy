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

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public abstract class AbstractEntityProvider<T> implements EntityProvider<T> {
    private final T provider;
    private final Class<?> genericType;
    private final Set<MediaType> consumes;
    private final Set<MediaType> produces;
    private final int priority;
    private final boolean builtIn;

    protected AbstractEntityProvider(final T provider, final Class<?> genericType, final int priority, final boolean builtIn) {
        this.provider = provider;
        this.genericType = genericType;
        this.priority = priority;
        this.builtIn = builtIn;
        this.consumes = consumes(provider.getClass());
        this.produces = produces(provider.getClass());
    }

    @Override
    public T provider() {
        return provider;
    }

    @Override
    public Set<MediaType> consumes() {
        return consumes;
    }

    @Override
    public Set<MediaType> produces() {
        return produces;
    }

    @Override
    public boolean isBuiltIn() {
        return builtIn;
    }

    @Override
    public int priority() {
        return priority;
    }

    @Override
    public Class<?> providerType() {
        return genericType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(provider, genericType, priority, builtIn);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AbstractEntityProvider)) {
            return false;
        }
        final AbstractEntityProvider<?> other = (AbstractEntityProvider<?>) obj;
        return Objects.equals(provider, other.provider)
                && Objects.equals(genericType, other.genericType)
                && priority == other.priority
                && builtIn == other.builtIn;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[provider=" + provider + ", genericType=" + genericType + ", priority=" + priority
                + ", builtIn=" + builtIn + "]";
    }

    private static Set<MediaType> produces(final Class<?> type) {
        if (type.isAnnotationPresent(Produces.class)) {
            final Set<MediaType> result = new LinkedHashSet<>();
            for (String mediaType : type.getAnnotation(Produces.class).value()) {
                result.add(MediaType.valueOf(mediaType));
            }
            return result;
        }
        // https://jakarta.ee/specifications/restful-ws/3.1/jakarta-restful-ws-spec-3.1#declaring_provider_capabilities
        // The absence of these annotations is equivalent to their inclusion with media type ("*/*"), i.e. absence
        // implies that any media type is supported.
        return Set.of(MediaType.WILDCARD_TYPE);
    }

    private static Set<MediaType> consumes(final Class<?> type) {
        if (type.isAnnotationPresent(Consumes.class)) {
            final Set<MediaType> result = new LinkedHashSet<>();
            for (String mediaType : type.getAnnotation(Consumes.class).value()) {
                result.add(MediaType.valueOf(mediaType));
            }
            return result;
        }
        // https://jakarta.ee/specifications/restful-ws/3.1/jakarta-restful-ws-spec-3.1#declaring_provider_capabilities
        // The absence of these annotations is equivalent to their inclusion with media type ("*/*"), i.e. absence
        // implies that any media type is supported.
        return Set.of(MediaType.WILDCARD_TYPE);
    }
}
