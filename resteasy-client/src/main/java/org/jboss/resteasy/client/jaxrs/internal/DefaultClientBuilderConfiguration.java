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

import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import jakarta.ws.rs.core.Configuration;

import org.jboss.resteasy.client.jaxrs.api.ClientBuilderConfiguration;
import org.jboss.resteasy.client.jaxrs.engines.PassthroughTrustManager;
import org.jboss.resteasy.client.jaxrs.i18n.LogMessages;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.config.Options;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
class DefaultClientBuilderConfiguration implements ClientBuilderConfiguration {
    private final long connectionTTL;
    private final List<String> sniHostNames;
    private final String proxyHostName;
    private final int proxyPort;
    private final String proxyScheme;
    private final boolean cookieManagementEnabled;
    private final SSLContext sslContext;
    private final long readTimeout;
    private final long connectionTimeout;
    private final boolean isFollowRedirect;
    private final ExecutorService executorService;
    private final ScheduledExecutorService scheduledExecutorService;
    private final Configuration configuration;

    DefaultClientBuilderConfiguration(final ResteasyClientBuilderImpl builder) {
        this.connectionTTL = builder.getConnectionTTL(TimeUnit.MILLISECONDS);
        this.sniHostNames = List.copyOf(builder.getSniHostNames());
        this.proxyHostName = builder.getDefaultProxyHostname();
        this.proxyPort = builder.getDefaultProxyPort();
        this.proxyScheme = builder.getDefaultProxyScheme();
        this.cookieManagementEnabled = builder.isCookieManagementEnabled();
        this.sslContext = resolveSslContext(builder);
        this.readTimeout = builder.getReadTimeout(TimeUnit.MILLISECONDS);
        this.connectionTimeout = builder.getConnectionTimeout(TimeUnit.MILLISECONDS);
        this.isFollowRedirect = builder.isFollowRedirects();
        this.executorService = builder.asyncExecutor;
        this.scheduledExecutorService = builder.scheduledExecutorService;
        this.configuration = builder.getConfiguration();
    }

    static DefaultClientBuilderConfiguration create(final ResteasyClientBuilderImpl builder) {
        return new DefaultClientBuilderConfiguration(builder);
    }

    @Override
    public long connectionIdleTime(final TimeUnit unit) {
        return connectionTTL;
    }

    @Override
    public List<String> sniHostNames() {
        return sniHostNames;
    }

    @Override
    public String defaultProxyHostname() {
        return proxyHostName;
    }

    @Override
    public int defaultProxyPort() {
        return proxyPort;
    }

    @Override
    public String defaultProxyScheme() {
        return proxyScheme;
    }

    @Override
    public boolean isCookieManagementEnabled() {
        return cookieManagementEnabled;
    }

    @Override
    public SSLContext sslContext() {
        return sslContext;
    }

    @Override
    public long readTimeout(final TimeUnit unit) {
        return TimeUnit.MILLISECONDS.convert(readTimeout, unit);
    }

    @Override
    public long connectionTimeout(final TimeUnit unit) {
        return TimeUnit.MILLISECONDS.convert(connectionTimeout, unit);
    }

    @Override
    public boolean isFollowRedirects() {
        return isFollowRedirect;
    }

    @Override
    public Optional<ExecutorService> executorService() {
        return Optional.ofNullable(executorService);
    }

    @Override
    public Optional<ScheduledExecutorService> scheduledExecutorService() {
        return Optional.ofNullable(scheduledExecutorService);
    }

    @Override
    public Configuration configuration() {
        return configuration;
    }

    private SSLContext resolveSslContext(final ResteasyClientBuilderImpl resteasyClientBuilder) {
        try {
            SSLContext sslContext = null;
            if (resteasyClientBuilder.isTrustManagerDisabled()) {
                sslContext = resolveSslContext(resteasyClientBuilder.getConfiguration());
                sslContext.init(null, new TrustManager[] { new PassthroughTrustManager() }, null);
            } else if (resteasyClientBuilder.getSSLContext() != null) {
                sslContext = resteasyClientBuilder.getSSLContext();
            } else if (resteasyClientBuilder.getKeyStore() != null || resteasyClientBuilder.getTrustStore() != null) {
                final KeyStore keyStore = resteasyClientBuilder.getKeyStore();
                final KeyStore trustStore = resteasyClientBuilder.getTrustStore();
                sslContext = resolveSslContext(resteasyClientBuilder.getConfiguration());
                final KeyManager[] keyManagers;
                if (keyStore != null) {
                    keyManagers = new KeyManager[] {
                            SslUtils.getKeyManager(keyStore, resteasyClientBuilder.getKeyStorePassword()) };
                } else {
                    keyManagers = SslUtils.getKeyManagers(null, resteasyClientBuilder.getKeyStorePassword());
                }
                final TrustManager[] trustManagers;
                if (trustStore != null) {
                    final X509TrustManager trustManager;
                    if (resteasyClientBuilder.isTrustSelfSignedCertificates()) {
                        trustManager = new TrustSelfSignedTrustManager(SslUtils.getTrustManager(trustStore));
                        trustManagers = new TrustManager[] { trustManager };
                    } else {
                        trustManagers = SslUtils.getTrustManagers(trustStore);
                    }
                } else {
                    trustManagers = SslUtils.getTrustManagers(null);
                }
                sslContext.init(keyManagers, trustManagers, null);
            }
            return sslContext;
        } catch (Exception e) {
            throw Messages.MESSAGES.failedToResolveSSLContext(e);
        }
    }

    private static SSLContext resolveSslContext(final Configuration configuration) throws NoSuchAlgorithmException {
        // https://docs.oracle.com/en/java/javase/11/docs/specs/security/standard-names.html#sslcontext-algorithms
        final Object protocolObject = configuration.getProperty(Options.CLIENT_SSL_CONTEXT_ALGORITHM.name());
        final String protocol;
        if (protocolObject == null) {
            protocol = Options.CLIENT_SSL_CONTEXT_ALGORITHM.getValue();
        } else {
            if (protocolObject instanceof String) {
                protocol = (String) protocolObject;
            } else {
                protocol = Options.CLIENT_SSL_CONTEXT_ALGORITHM.getValue();
                LogMessages.LOGGER.invalidProtocol(protocolObject, protocol);
            }
        }
        return SSLContext.getInstance(protocol);
    }

    private static class TrustSelfSignedTrustManager implements X509TrustManager {
        private final X509TrustManager delegate;

        TrustSelfSignedTrustManager(final X509TrustManager delegate) {
            this.delegate = delegate;
        }

        @Override
        public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
            delegate.checkClientTrusted(chain, authType);
        }

        @Override
        public void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
            if (chain.length != 1) {
                delegate.checkServerTrusted(chain, authType);
            }
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return delegate.getAcceptedIssuers();
        }
    }
}
