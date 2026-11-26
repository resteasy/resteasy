/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.client.jaxrs.engines.config;

/**
 * Constants used for client configuration.
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
public class HttpClientConfigurationOptions {

    /**
     * Determines which {@link java.net.http.HttpClient.Version} to use.
     */
    public static final String HTTP_VERSION = "dev.resteasy.client.engine.http.version";
}
