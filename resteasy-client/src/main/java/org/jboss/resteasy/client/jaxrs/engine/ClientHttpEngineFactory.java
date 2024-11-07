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

package org.jboss.resteasy.client.jaxrs.engine;

import org.jboss.resteasy.client.jaxrs.ClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.api.ClientBuilderConfiguration;
import org.jboss.resteasy.client.jaxrs.engines.AsyncClientHttpEngine;

/**
 * A factory for creating HTTP engines for the {@linkplain jakarta.ws.rs.client.Client Jakarta REST Client}
 * implementation.
 * <p>
 * Implementations are loaded via a {@link java.util.ServiceLoader} and may be annotated with
 * {@link jakarta.annotation.Priority @Priority} to indicate ranking.
 * </p>
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@FunctionalInterface
public interface ClientHttpEngineFactory {

    /**
     * Creates, or retrieves, a {@link ClientHttpEngine} based on the configuration.
     *
     * @param configuration the {@link jakarta.ws.rs.client.ClientBuilder} configuration to use
     *
     * @return the client HTTP engine, should never be {@code null}
     */
    default ClientHttpEngine httpClientEngine(ClientBuilderConfiguration configuration) {
        return asyncHttpClientEngine(configuration);
    }

    /**
     * Creates, or retrieves, a {@link AsyncClientHttpEngine} based on the configuration.
     *
     * @param configuration the {@link jakarta.ws.rs.client.ClientBuilder} configuration to use
     *
     * @return the client HTTP engine, should never be {@code null}
     */
    AsyncClientHttpEngine asyncHttpClientEngine(ClientBuilderConfiguration configuration);
}
