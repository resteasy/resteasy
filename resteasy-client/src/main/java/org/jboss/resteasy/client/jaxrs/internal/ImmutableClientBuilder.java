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

package org.jboss.resteasy.client.jaxrs.internal;

import java.security.KeyStore;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Configuration;

import org.jboss.resteasy.client.jaxrs.ClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
class ImmutableClientBuilder extends ResteasyClientBuilder {
    private final ResteasyClientBuilderImpl delegate;

    ImmutableClientBuilder(final ResteasyClientBuilderImpl delegate) {
        this.delegate = new ResteasyClientBuilderImpl(delegate);
    }

    @Override
    public ResteasyClientBuilder providerFactory(final ResteasyProviderFactory providerFactory) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResteasyProviderFactory getProviderFactory() {
        return delegate.getProviderFactory();
    }

    @Override
    public ResteasyClientBuilder connectionTTL(final long ttl, final TimeUnit unit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getConnectionTTL(final TimeUnit unit) {
        return delegate.getConnectionTTL(unit);
    }

    @Override
    public ResteasyClientBuilder maxPooledPerRoute(final int maxPooledPerRoute) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getMaxPooledPerRoute() {
        return delegate.getMaxPooledPerRoute();
    }

    @Override
    public ResteasyClientBuilder connectionCheckoutTimeout(final long timeout, final TimeUnit unit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getConnectionCheckoutTimeout(final TimeUnit unit) {
        return delegate.getConnectionCheckoutTimeout(unit);
    }

    @Override
    public ResteasyClientBuilder connectionPoolSize(final int connectionPoolSize) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getConnectionPoolSize() {
        return delegate.getConnectionPoolSize();
    }

    @Override
    public ResteasyClientBuilder responseBufferSize(final int size) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getResponseBufferSize() {
        return delegate.getResponseBufferSize();
    }

    @Override
    public ResteasyClientBuilder disableTrustManager() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isTrustManagerDisabled() {
        return delegate.isTrustManagerDisabled();
    }

    @Override
    public void setIsTrustSelfSignedCertificates(final boolean b) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isTrustSelfSignedCertificates() {
        return delegate.isTrustSelfSignedCertificates();
    }

    @Override
    public ResteasyClientBuilder hostnameVerification(final HostnameVerificationPolicy policy) {
        throw new UnsupportedOperationException();
    }

    @Override
    public HostnameVerificationPolicy getHostnameVerification() {
        return delegate.getHostnameVerification();
    }

    @Override
    public ResteasyClientBuilder httpEngine(final ClientHttpEngine httpEngine) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ClientHttpEngine getHttpEngine() {
        return delegate.getHttpEngine();
    }

    @Override
    public ResteasyClientBuilder useAsyncHttpEngine() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isUseAsyncHttpEngine() {
        return delegate.isUseAsyncHttpEngine();
    }

    @Override
    public ResteasyClientBuilder sniHostNames(final String... sniHostNames) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> getSniHostNames() {
        return delegate.getSniHostNames();
    }

    @Override
    public ResteasyClientBuilder defaultProxy(final String hostname) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getDefaultProxyHostname() {
        return delegate.getDefaultProxyHostname();
    }

    @Override
    public int getDefaultProxyPort() {
        return delegate.getDefaultProxyPort();
    }

    @Override
    public String getDefaultProxyScheme() {
        return delegate.getDefaultProxyScheme();
    }

    @Override
    public ResteasyClientBuilder defaultProxy(final String hostname, final int port) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResteasyClientBuilder defaultProxy(final String hostname, final int port, final String scheme) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResteasyClientBuilder enableCookieManagement() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isCookieManagementEnabled() {
        return delegate.isCookieManagementEnabled();
    }

    @Override
    public SSLContext getSSLContext() {
        return delegate.getSSLContext();
    }

    @Override
    public KeyStore getKeyStore() {
        return delegate.getKeyStore();
    }

    @Override
    public String getKeyStorePassword() {
        return delegate.getKeyStorePassword();
    }

    @Override
    public KeyStore getTrustStore() {
        return delegate.getTrustStore();
    }

    @Override
    public HostnameVerifier getHostnameVerifier() {
        return delegate.getHostnameVerifier();
    }

    @Override
    public long getReadTimeout(final TimeUnit unit) {
        return delegate.getReadTimeout(unit);
    }

    @Override
    public long getConnectionTimeout(final TimeUnit unit) {
        return delegate.getConnectionTimeout(unit);
    }

    @Override
    public ResteasyClientBuilder disableAutomaticRetries() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDisableAutomaticRetries() {
        return delegate.isDisableAutomaticRetries();
    }

    @Override
    public ResteasyClientBuilder executorService(final ExecutorService executorService, final boolean cleanupExecutor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResteasyClient build() {
        return delegate.build();
    }

    @Override
    public ResteasyClientBuilder withConfig(final Configuration config) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResteasyClientBuilder sslContext(final SSLContext sslContext) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResteasyClientBuilder keyStore(final KeyStore keyStore, final char[] password) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResteasyClientBuilder keyStore(final KeyStore keyStore, final String password) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResteasyClientBuilder trustStore(final KeyStore trustStore) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResteasyClientBuilder hostnameVerifier(final HostnameVerifier verifier) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResteasyClientBuilder executorService(final ExecutorService executorService) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResteasyClientBuilder scheduledExecutorService(final ScheduledExecutorService scheduledExecutorService) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResteasyClientBuilder connectTimeout(final long timeout, final TimeUnit unit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResteasyClientBuilder readTimeout(final long timeout, final TimeUnit unit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResteasyClientBuilder setFollowRedirects(final boolean followRedirects) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isFollowRedirects() {
        return delegate.isFollowRedirects();
    }

    @Override
    public Configuration getConfiguration() {
        return delegate.getConfiguration();
    }

    @Override
    public ClientBuilder property(final String name, final Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ClientBuilder register(final Class<?> componentClass) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ClientBuilder register(final Class<?> componentClass, final int priority) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ClientBuilder register(final Class<?> componentClass, final Class<?>... contracts) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ClientBuilder register(final Class<?> componentClass, final Map<Class<?>, Integer> contracts) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ClientBuilder register(final Object component) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ClientBuilder register(final Object component, final int priority) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ClientBuilder register(final Object component, final Class<?>... contracts) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ClientBuilder register(final Object component, final Map<Class<?>, Integer> contracts) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResteasyClientBuilder toImmutable() {
        return this;
    }
}
