/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.resteasy.embedded.server;

import org.jboss.resteasy.spi.config.ConfigurationFactory;

/**
 * A concrete {@link ConfigurationFactory} registered via SPI so that the resolver's
 * {@code loadFactory()} returns a real instance (not a lambda) on each invocation.
 */
public class TestConfigurationFactory implements ConfigurationFactory {

    @Override
    public int priority() {
        return 500;
    }
}
