/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.cdi;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.ws.rs.core.Application;

import org.jboss.resteasy.core.ResteasyContext;

/**
 * The Jakarta REST components managed by CDI.
 * <p>
 * The classes of the {@link Application#getSingletons()} instances of the current {@link Application} are excluded, as
 * the application creates these instances and RESTEasy injects them. The extension is shared by all modules of a
 * deployment, e.g. the WARs of an EAR, so the excluded classes are kept for each {@link Application}.
 * </p>
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
class DefaultResteasyBeanContainer implements ResteasyBeanContainer {

    private final Set<Class<?>> allBeans;
    private final Map<Application, Set<Class<?>>> singletonClasses = Collections
            .synchronizedMap(new IdentityHashMap<>());

    DefaultResteasyBeanContainer(final Set<Class<?>> allBeans) {
        this.allBeans = Set.copyOf(allBeans);
    }

    @Override
    public boolean contains(final Class<?> type) {
        if (!allBeans.contains(type)) {
            return false;
        }
        // An Application is never one of its own singletons. Skipping the lookup also avoids invoking
        // Application.getSingletons() before the Application itself has been injected.
        if (Application.class.isAssignableFrom(type)) {
            return true;
        }
        final Application application = (Application) ResteasyContext.searchContextData(Application.class);
        if (application == null) {
            return true;
        }
        return !singletonClasses.computeIfAbsent(application, DefaultResteasyBeanContainer::singletonClasses)
                .contains(type);
    }

    @SuppressWarnings("deprecation")
    private static Set<Class<?>> singletonClasses(final Application application) {
        return application.getSingletons().stream()
                .map(Object::getClass)
                .collect(Collectors.toUnmodifiableSet());
    }
}
