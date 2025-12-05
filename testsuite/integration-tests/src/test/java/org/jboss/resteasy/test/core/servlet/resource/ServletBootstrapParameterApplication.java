/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.core.servlet.resource;

import java.util.Set;

import jakarta.ws.rs.core.Application;

/**
 * Application class for RESTEASY-3678 test.
 */
public class ServletBootstrapParameterApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        return Set.of(ServletBootstrapParameterResource.class);
    }
}
