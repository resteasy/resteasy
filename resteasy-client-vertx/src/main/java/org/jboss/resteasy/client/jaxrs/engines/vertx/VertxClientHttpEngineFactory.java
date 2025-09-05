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

package org.jboss.resteasy.client.jaxrs.engines.vertx;

import java.util.concurrent.TimeUnit;

import org.jboss.resteasy.client.jaxrs.api.ClientBuilderConfiguration;
import org.jboss.resteasy.client.jaxrs.engine.ClientHttpEngineFactory;
import org.jboss.resteasy.client.jaxrs.engines.AsyncClientHttpEngine;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.net.ProxyOptions;
import io.vertx.core.net.ProxyType;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 * @deprecated use new dependencies
 */
@Deprecated(forRemoval = true, since = "6.2.13.Final")
public class VertxClientHttpEngineFactory implements ClientHttpEngineFactory {
    @Override
    public AsyncClientHttpEngine asyncHttpClientEngine(final ClientBuilderConfiguration configuration) {
        final Vertx vertx = Vertx.vertx();

        final HttpClientOptions options = new HttpClientOptions();

        final long connectionTimeout = configuration.connectionTimeout(TimeUnit.MILLISECONDS);
        if (connectionTimeout > 0L) {
            options.setConnectTimeout(Math.toIntExact(connectionTimeout));
        }

        final long idleTimeout = configuration.connectionIdleTime(TimeUnit.SECONDS);
        if (idleTimeout > 0L) {
            options.setIdleTimeout(Math.toIntExact(idleTimeout));
        }

        final String proxyHostname = configuration.defaultProxyHostname();
        if (proxyHostname != null) {
            final ProxyOptions proxyOptions = new ProxyOptions();
            proxyOptions.setHost(proxyHostname);
            proxyOptions.setPort(configuration.defaultProxyPort());
            proxyOptions.setType(ProxyType.HTTP);
            options.setProxyOptions(proxyOptions);
        }

        final long readTimeout = configuration.readTimeout(TimeUnit.SECONDS);
        if (readTimeout > 0L) {
            options.setReadIdleTimeout(Math.toIntExact(readTimeout));
        }

        return new VertxClientHttpEngine(vertx, options, configuration);
    }
}
