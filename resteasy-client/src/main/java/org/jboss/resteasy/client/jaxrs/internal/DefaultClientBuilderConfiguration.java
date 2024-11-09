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

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;

import jakarta.ws.rs.core.Configuration;

import org.jboss.resteasy.client.jaxrs.api.ClientBuilderConfiguration;

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
        this.sslContext = builder.getSSLContext();
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
}
