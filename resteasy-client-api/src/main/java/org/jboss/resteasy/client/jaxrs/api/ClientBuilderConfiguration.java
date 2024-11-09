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

package org.jboss.resteasy.client.jaxrs.api;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Configuration;

/**
 * Represents the {@link ClientBuilder} configuration for configuring an
 * {@link org.jboss.resteasy.client.jaxrs.ClientHttpEngine}.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public interface ClientBuilderConfiguration {

    /**
     * Get the connections time to live, if set.
     *
     * @param unit the unit used to convert the time
     *
     * @return the time to live or {@code -1} if not set
     */
    long connectionIdleTime(TimeUnit unit);

    /**
     * Returns a list of SNI host names for authentication.
     *
     * @return the SNI host names or an empty list
     */
    List<String> sniHostNames();

    /**
     * Returns the default proxy host name if configured.
     *
     * @return the default proxy host name or {@code null}
     */
    String defaultProxyHostname();

    /**
     * Returns the default proxy port if configured.
     *
     * @return the default proxy port or {@code -1} if not set
     */
    int defaultProxyPort();

    /**
     * The default proxy scheme, the default is {@code http}.
     *
     * @return the proxy scheme
     */
    String defaultProxyScheme();

    /**
     * Indicates whether cookie management should be used or not.
     *
     * @return {@code true} if cookie management should be used
     */
    boolean isCookieManagementEnabled();

    /**
     * The {@link SSLContext used for the connection, if applicable.}
     *
     * @return the {@link SSLContext} or {@code null} if one was not configured
     */
    SSLContext sslContext();

    /**
     * Returns the read timeout converted in the time unit provided.
     *
     * @param unit the time unit used to convert the read timeout
     *
     * @return the read timeout or {@code -1} if no read timeout configured
     */
    long readTimeout(TimeUnit unit);

    /**
     * Returns the connection timeout converted in the time unit provided.
     *
     * @param unit the time unit used to convert the connection timeout
     *
     * @return the connection timeout or {@code -1} if no connection timeout configured
     */
    long connectionTimeout(TimeUnit unit);

    /**
     * Indicates if redirects should be followed.
     *
     * @return {@code true} if redirects should be followed, otherwise {@code false}
     */
    boolean isFollowRedirects();

    /**
     * Returns the {@link ExecutorService} associated with the
     * {@link jakarta.ws.rs.client.ClientBuilder#executorService(ExecutorService)}.
     *
     * @return the executor service or {@code null} if one was not configured
     */
    Optional<ExecutorService> executorService();

    /**
     * Returns the {@link ScheduledExecutorService} associated with the
     * {@link jakarta.ws.rs.client.ClientBuilder#scheduledExecutorService(ScheduledExecutorService)}.
     *
     * @return the scheduled executor service or {@code null} if one was not configured
     */
    Optional<ScheduledExecutorService> scheduledExecutorService();

    /**
     * Returns the {@link Configuration} for the {@link ClientBuilder#getConfiguration()}.
     *
     * @return the configuration
     */
    Configuration configuration();
}
