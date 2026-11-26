/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.client.jaxrs.engines;

import java.net.CookieManager;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SNIHostName;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;

import org.jboss.resteasy.client.jaxrs.ClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.api.ClientBuilderConfiguration;
import org.jboss.resteasy.client.jaxrs.engine.ClientHttpEngineFactory;
import org.jboss.resteasy.client.jaxrs.i18n.LogMessages;
import org.jboss.resteasy.client.jaxrs.spi.ClientConfigProvider;
import org.jboss.resteasy.spi.PriorityServiceLoader;

/**
 * Creates HTTP client engines backed by the JDK's {@link HttpClient}.
 * <p>
 * The client builder uses this factory when no other {@link ClientHttpEngineFactory} is found. Each engine uses one
 * {@code HttpClient} for every request, so connections are pooled between requests.
 * </p>
 * <p>
 * If no {@link SSLContext} is configured on the builder and a {@link ClientConfigProvider} is found, the engine asks
 * the provider for the {@link SSLContext} of each TLS connection, including a connection opened for a redirect.
 * </p>
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
public class JavaClientHttpEngineFactory implements ClientHttpEngineFactory {

    @Override
    public ClientHttpEngine httpClientEngine(final ClientBuilderConfiguration configuration) {
        return ClientHttpEngineFactory.super.httpClientEngine(configuration);
    }

    @Override
    public AsyncClientHttpEngine asyncHttpClientEngine(final ClientBuilderConfiguration configuration) {
        // An SSLContext configured on the builder, including one created from a key or trust store, takes precedence
        // over the ClientConfigProvider
        final ClientConfigProvider configProvider = configuration.sslContext() == null ? findClientConfigProvider() : null;
        // The provider is asked for the SSLContext of each TLS connection, including those opened for a redirect
        final SSLContext sslContext = configProvider == null ? configuration.sslContext()
                : new ClientConfigProviderSSLContext(configProvider, defaultSslContext());
        return new JavaClientHttpEngine(createHttpClient(configuration, sslContext, sslParameters(configuration)),
                configuration);
    }

    private HttpClient createHttpClient(final ClientBuilderConfiguration configuration,
            final SSLContext sslContext,
            final SSLParameters sslParameters) {
        final HttpClient.Builder clientBuilder = HttpClient.newBuilder()
                // Use NORMAL to follow redirects while preventing HTTPS -> HTTP downgrades
                .followRedirects(configuration.isFollowRedirects() ? HttpClient.Redirect.NORMAL : HttpClient.Redirect.NEVER);

        // The client executor is not used by the HttpClient. The HttpClient runs its I/O on its executor, and a
        // synchronous send() waits on that I/O. If both use one bounded executor, such as a ManagedExecutorService,
        // synchronous requests made from its threads could block every thread and leave none for the I/O. The engine
        // uses the client executor to process asynchronous responses.

        final long connectionTimeout = configuration.connectionTimeout(TimeUnit.MILLISECONDS);
        if (connectionTimeout > 0L) {
            clientBuilder.connectTimeout(Duration.ofMillis(connectionTimeout));
        }
        final String proxyHostname = configuration.defaultProxyHostname();
        if (proxyHostname != null) {
            final int port = configuration.defaultProxyPort() < 0 ? 80 : configuration.defaultProxyPort();
            final ProxySelector proxySelector = ProxySelector.of(
                    InetSocketAddress.createUnresolved(configuration.defaultProxyHostname(), port));
            clientBuilder.proxy(proxySelector);
        }

        if (configuration.isCookieManagementEnabled()) {
            clientBuilder.cookieHandler(new CookieManager());
        }

        final HostnameVerifier hostnameVerifier = configuration.hostnameVerifier().orElse(null);
        if (hostnameVerifier != null) {
            if (sslParameters != null) {
                clientBuilder.sslParameters(sslParameters);
            }
            clientBuilder.sslContext(new HostnameVerifyingSSLContext(
                    sslContext == null ? defaultSslContext() : sslContext, hostnameVerifier));
        } else if (sslContext != null) {
            if (sslParameters != null) {
                clientBuilder.sslParameters(sslParameters);
            }
            clientBuilder.sslContext(sslContext);
        } else if (sslParameters != null) {
            clientBuilder.sslParameters(sslParameters);
        }
        return clientBuilder.build();
    }

    private static SSLParameters sslParameters(final ClientBuilderConfiguration configuration) {
        if (configuration.sniHostNames().isEmpty()) {
            return null;
        }
        final SSLParameters sslParameters = new SSLParameters();
        sslParameters.setServerNames(configuration.sniHostNames()
                .stream()
                .map(SNIHostName::new)
                .collect(Collectors.toList()));
        return sslParameters;
    }

    private static SSLContext defaultSslContext() {
        try {
            return SSLContext.getDefault();
        } catch (NoSuchAlgorithmException e) {
            throw LogMessages.LOGGER.defaultSslContextUnavailable(e);
        }
    }

    private static ClientConfigProvider findClientConfigProvider() {
        return PriorityServiceLoader.load(ClientConfigProvider.class, getClassLoader()).first()
                .orElse(null);
    }

    private static ClassLoader getClassLoader() {
        ClassLoader result = Thread.currentThread().getContextClassLoader();
        if (result == null) {
            result = ClientConfigProvider.class.getClassLoader();
        }
        return result;
    }
}
