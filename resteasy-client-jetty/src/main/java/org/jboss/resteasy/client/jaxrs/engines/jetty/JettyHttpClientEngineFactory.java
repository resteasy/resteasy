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

package org.jboss.resteasy.client.jaxrs.engines.jetty;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SNIHostName;
import javax.net.ssl.SNIServerName;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpProxy;
import org.eclipse.jetty.client.Origin;
import org.eclipse.jetty.client.http.HttpClientTransportOverHTTP;
import org.eclipse.jetty.io.ClientConnector;
import org.eclipse.jetty.util.HttpCookieStore;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.jboss.resteasy.client.jaxrs.api.ClientBuilderConfiguration;
import org.jboss.resteasy.client.jaxrs.engine.ClientHttpEngineFactory;
import org.jboss.resteasy.client.jaxrs.engines.AsyncClientHttpEngine;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@Deprecated(forRemoval = true, since = "6.2")
public class JettyHttpClientEngineFactory implements ClientHttpEngineFactory {
    @Override
    public AsyncClientHttpEngine asyncHttpClientEngine(final ClientBuilderConfiguration configuration) {
        final ClientConnector connector = new ClientConnector();
        if (configuration.sslContext() != null) {
            final SslContextFactory.Client sslClient = new SslContextFactory.Client();
            sslClient.setSslContext(configuration.sslContext());

            if (!configuration.sniHostNames().isEmpty()) {
                final SslContextFactory.Client.SniProvider provider = (sslEngine, serverNames) -> {
                    final List<SNIServerName> sniServerNames = new ArrayList<>();
                    for (String name : configuration.sniHostNames()) {
                        sniServerNames.add(new SNIHostName(name));
                    }
                    return List.copyOf(sniServerNames);

                };
                sslClient.setSNIProvider(provider);
            }
            connector.setSslContextFactory(sslClient);
        }
        final HttpClient httpClient = new HttpClient(new HttpClientTransportOverHTTP(connector));
        configuration.executorService().ifPresent(httpClient::setExecutor);

        final long connectionTimeout = configuration.connectionTimeout(TimeUnit.MILLISECONDS);
        if (connectionTimeout >= 0L) {
            httpClient.setConnectTimeout(connectionTimeout);
        }

        final String proxyHost = configuration.defaultProxyHostname();
        if (proxyHost != null) {
            final String proxyProtocol = configuration.defaultProxyHostname();
            final int proxyPort = configuration.defaultProxyPort();
            final Origin.Address address = new Origin.Address(proxyHost, proxyPort);
            final HttpProxy proxy = new HttpProxy(address, proxyProtocol.equalsIgnoreCase("https"));
            httpClient.getProxyConfiguration().addProxy(proxy);
        }

        if (configuration.isCookieManagementEnabled()) {
            httpClient.setCookieStore(new HttpCookieStore());
        }

        httpClient.setFollowRedirects(configuration.isFollowRedirects());
        return new JettyClientEngine(httpClient, configuration.connectionIdleTime(TimeUnit.MILLISECONDS),
                configuration.readTimeout(TimeUnit.MILLISECONDS));
    }
}
