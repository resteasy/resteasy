/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.tracing.filter;

import java.util.Set;

import jakarta.ws.rs.core.Application;

public class TracingApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        return Set.of(TracingResource.class);
    }
}
