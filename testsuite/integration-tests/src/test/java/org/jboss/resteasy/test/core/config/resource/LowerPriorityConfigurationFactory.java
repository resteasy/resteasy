/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.core.config.resource;

import org.jboss.resteasy.spi.config.ConfigurationFactory;

/**
 * A concrete instance of {@link ConfigurationFactory}, which has a priority of {@link Integer#MIN_VALUE}/2.
 */
public class LowerPriorityConfigurationFactory implements ConfigurationFactory {

    @Override
    public int priority() {
        return Integer.MIN_VALUE / 2;
    }
}
