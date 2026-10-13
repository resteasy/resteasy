/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.cdi;

import java.util.Map;
import java.util.Set;

import jakarta.ws.rs.core.Application;

import org.jboss.resteasy.core.ResteasyContext;

/**
 * An {@link Application} which delegates to the {@link Application} of the current request. The CDI beans for the
 * {@link Application} are shared by each application deployed with the same CDI container, e.g. the WARs in an EAR, so
 * they must not hold the {@link Application} which was current when they were created.
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
class ContextApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        return getDelegate().getClasses();
    }

    @Deprecated
    @Override
    public Set<Object> getSingletons() {
        return getDelegate().getSingletons();
    }

    @Override
    public Map<String, Object> getProperties() {
        return getDelegate().getProperties();
    }

    private Application getDelegate() {
        return ResteasyContext.getRequiredContextData(Application.class);
    }
}
