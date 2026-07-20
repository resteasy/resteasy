/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.cdi;

import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.FeatureContext;

/**
 * A Jakarta REST {@link Feature} that registers CDI integration providers. This feature is loaded automatically via
 * {@link java.util.ServiceLoader} when {@code jakarta.ws.rs.loadServices} is not set to {@code false}.
 * <p>
 * Currently registers:
 * </p>
 * <ul>
 * <li>{@link CdiProxyUnwrapFilter} &mdash; unwraps CDI proxies from response entities before serialization</li>
 * </ul>
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
public class CdiFeature implements Feature {
    @Override
    public boolean configure(final FeatureContext context) {
        context.register(new CdiProxyUnwrapFilter());
        return true;
    }
}
