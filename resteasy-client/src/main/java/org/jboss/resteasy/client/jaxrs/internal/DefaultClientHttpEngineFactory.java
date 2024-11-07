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

package org.jboss.resteasy.client.jaxrs.internal;

import org.jboss.resteasy.client.jaxrs.ClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.api.ClientBuilderConfiguration;
import org.jboss.resteasy.client.jaxrs.engine.ClientHttpEngineFactory;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpAsyncClient4Engine;
import org.jboss.resteasy.client.jaxrs.engines.AsyncClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.engines.ClientHttpEngineBuilder43;
import org.jboss.resteasy.client.jaxrs.i18n.Messages;

/**
 * The default HTTP Client Engine factory.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@SuppressWarnings("removal")
class DefaultClientHttpEngineFactory implements ClientHttpEngineFactory {
    @Override
    public ClientHttpEngine httpClientEngine(final ClientBuilderConfiguration configuration) {
        if (!(configuration instanceof ResteasyClientBuilder)) {
            throw Messages.MESSAGES.invalidClientBuilderConfiguration(configuration.getClass(), ResteasyClientBuilder.class);
        }
        final ClientHttpEngineBuilder43 builder = new ClientHttpEngineBuilder43();
        return builder.resteasyClientBuilder((ResteasyClientBuilder) configuration).build();
    }

    @Override
    public AsyncClientHttpEngine asyncHttpClientEngine(final ClientBuilderConfiguration configuration) {
        return new ApacheHttpAsyncClient4Engine(true);
    }
}
