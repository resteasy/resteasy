/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2022 Red Hat, Inc., and individual contributors
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

package org.jboss.resteasy.client.jaxrs.engines;

import java.util.concurrent.Executor;

import org.jboss.resteasy.client.jaxrs.ClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.ClientHttpEngineBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.spi.ClientConfigProvider;

/**
 * A {@link ClientHttpEngineBuilder} for the {@link java.net.http.HttpClient}.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class HttpClientEngineBuilder implements ClientHttpEngineBuilder {
    private final ClientConfigProvider clientConfigProvider;
    private Executor executor;
    private ResteasyClientBuilder resteasyClientBuilder;

    public HttpClientEngineBuilder(final ClientConfigProvider clientConfigProvider) {
        this.clientConfigProvider = clientConfigProvider;
    }

    @Override
    public ClientHttpEngineBuilder resteasyClientBuilder(final ResteasyClientBuilder resteasyClientBuilder) {
        this.resteasyClientBuilder = resteasyClientBuilder;
        return this;
    }

    @Override
    public ClientHttpEngineBuilder executor(final Executor executor) {
        this.executor = executor;
        return this;
    }

    @Override
    public ClientHttpEngine build() {
        return new HttpClientEngine(resteasyClientBuilder.toImmutable(), executor, clientConfigProvider);
    }
}
