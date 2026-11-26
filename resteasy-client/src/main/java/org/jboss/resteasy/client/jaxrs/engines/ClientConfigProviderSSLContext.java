/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.client.jaxrs.engines;

import java.net.URI;
import java.security.SecureRandom;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLContextSpi;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.jboss.resteasy.client.jaxrs.i18n.LogMessages;
import org.jboss.resteasy.client.jaxrs.spi.ClientConfigProvider;

/**
 * An {@link SSLContext} which creates each engine from the {@link SSLContext} the {@link ClientConfigProvider} returns
 * for the host and port of the connection.
 * <p>
 * The {@link java.net.http.HttpClient} creates an engine for each TLS connection, including the connections it opens
 * when it follows a redirect. Each connection therefore uses the {@link SSLContext} for its own target, not the one for
 * the target of the first request. If the provider returns {@code null} for a target, the default context is used.
 * </p>
 * <p>
 * The default parameters of this context have no protocols and no cipher suites. The
 * {@link java.net.http.HttpClient} sets the default parameters on each engine it creates, so the engines keep the
 * protocols and cipher suites of the context which created them.
 * </p>
 * <p>
 * The socket factories are not supported, because sockets created from them would not use the provider.
 * </p>
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
final class ClientConfigProviderSSLContext extends SSLContext {

    /**
     * Creates a context which asks the {@code provider} for the context of each connection.
     *
     * @param provider       the provider of the {@link SSLContext} for each target, cannot be {@code null}
     * @param defaultContext the initialized context used when the provider returns {@code null}, cannot be
     *                       {@code null}
     */
    ClientConfigProviderSSLContext(final ClientConfigProvider provider, final SSLContext defaultContext) {
        super(new ProviderSpi(provider, defaultContext), defaultContext.getProvider(), defaultContext.getProtocol());
    }

    private static class ProviderSpi extends SSLContextSpi {
        private final ClientConfigProvider provider;
        private final SSLContext defaultContext;

        private ProviderSpi(final ClientConfigProvider provider, final SSLContext defaultContext) {
            this.provider = provider;
            this.defaultContext = defaultContext;
        }

        @Override
        protected void engineInit(final KeyManager[] km, final TrustManager[] tm, final SecureRandom sr) {
            throw LogMessages.LOGGER.unsupportedSslContextOperation("init");
        }

        @Override
        protected SSLSocketFactory engineGetSocketFactory() {
            throw LogMessages.LOGGER.unsupportedSslContextOperation("getSocketFactory");
        }

        @Override
        protected SSLServerSocketFactory engineGetServerSocketFactory() {
            throw LogMessages.LOGGER.unsupportedSslContextOperation("getServerSocketFactory");
        }

        @Override
        protected SSLEngine engineCreateSSLEngine() {
            return defaultContext.createSSLEngine();
        }

        @Override
        protected SSLEngine engineCreateSSLEngine(final String host, final int port) {
            // Without a host, there is no target to find a context for
            if (host == null) {
                return defaultContext.createSSLEngine(null, port);
            }
            final SSLContext context = provider.getSSLContext(target(host, port));
            return (context == null ? defaultContext : context).createSSLEngine(host, port);
        }

        @Override
        protected SSLSessionContext engineGetServerSessionContext() {
            return defaultContext.getServerSessionContext();
        }

        @Override
        protected SSLSessionContext engineGetClientSessionContext() {
            return defaultContext.getClientSessionContext();
        }

        @Override
        protected SSLParameters engineGetDefaultSSLParameters() {
            // No protocols and no cipher suites, so the engines keep those of the context which created them
            return new SSLParameters();
        }

        @Override
        protected SSLParameters engineGetSupportedSSLParameters() {
            return defaultContext.getSupportedSSLParameters();
        }

        // The provider finds the SSLContext with the scheme, host and port of the target. The default port is
        // omitted, as it is in a request URI without a port.
        private static URI target(final String host, final int port) {
            // An IPv6 address must be enclosed in brackets in a URI
            final String uriHost = host.indexOf(':') >= 0 && !host.startsWith("[") ? "[" + host + "]" : host;
            if (port < 0 || port == 443) {
                return URI.create(String.format("https://%s", uriHost));
            }
            return URI.create(String.format("https://%s:%d", uriHost, port));
        }
    }
}
