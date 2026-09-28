/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.resteasy.embedded.server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jakarta.ws.rs.SeBootstrap;

import org.jboss.resteasy.core.se.ResteasySeConfiguration;
import org.jboss.resteasy.spi.ResteasyConfiguration;

/**
 * A {@link ResteasyConfiguration} that uses the Undertow servlet parameters and if not found, uses the
 * {@link SeBootstrap.Configuration} to resolve properties.
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
class UndertowResteasyConfiguration implements ResteasyConfiguration {
    private final Map<String, String> initParams;
    private final SeBootstrap.Configuration configuration;

    UndertowResteasyConfiguration(final Map<String, String> servletConfigInitParams,
            final Map<String, String> servletContextInitParams, final SeBootstrap.Configuration configuration) {
        final Map<String, String> initParams = new HashMap<>(servletContextInitParams);
        initParams.putAll(servletConfigInitParams);
        this.initParams = Map.copyOf(initParams);
        this.configuration = configuration;
    }

    @Override
    public String getParameter(final String name) {
        if (initParams.containsKey(name)) {
            return initParams.get(name);
        }
        final Object value = configuration.property(name);
        return value == null ? null : value.toString();
    }

    @Override
    public Set<String> getParameterNames() {
        final Set<String> names = new HashSet<>(initParams.keySet());
        if (configuration instanceof ResteasySeConfiguration seConfiguration) {
            names.addAll(seConfiguration.propertyNames());
        }
        return Set.copyOf(names);
    }

    @Override
    public Set<String> getInitParameterNames() {
        return initParams.keySet();
    }

    @Override
    public String getInitParameter(String name) {
        return initParams.get(name);
    }
}
