/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.spi;

import java.util.Set;

import jakarta.ws.rs.container.ResourceContext;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Providers;
import jakarta.ws.rs.sse.Sse;

/**
 * The context types which may be injected with {@code @Inject}, or with {@link jakarta.ws.rs.core.Context @Context}
 * as in previous versions. These are the context types required by the
 * Jakarta REST specification and the Jakarta Servlet types. The Jakarta Servlet types are compared by name, so Jakarta
 * Servlet does not need to be visible to this class loader.
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 * @since 8.0
 */
public class RequiredContextTypes {

    private static final Set<Class<?>> CONTEXT_TYPES = Set.of(
            Application.class,
            Configuration.class,
            HttpHeaders.class,
            Providers.class,
            Request.class,
            ResourceContext.class,
            ResourceInfo.class,
            SecurityContext.class,
            Sse.class,
            UriInfo.class,
            // RESTEasy types
            HttpRequest.class

    );

    private static final Set<String> ADDITIONAL_CONTEXT_TYPES = Set.of(
            "jakarta.servlet.http.HttpServletRequest",
            "jakarta.servlet.http.HttpServletResponse",
            "jakarta.servlet.ServletConfig",
            "jakarta.servlet.ServletContext");

    /**
     * Checks whether the type is a context type which may be injected. A subclass of {@link Application} is a context
     * type. Any other type, including the Jakarta Servlet types, must match exactly.
     *
     * @param type the type to check
     *
     * @return {@code true} if the type is a context type, otherwise {@code false}
     */
    public static boolean isContextType(final Class<?> type) {
        // Only an Application subclass is a bean for its own type, other subtypes would be unsatisfied
        if (CONTEXT_TYPES.contains(type) || Application.class.isAssignableFrom(type)) {
            return true;
        }
        return ADDITIONAL_CONTEXT_TYPES.contains(type.getName());
    }
}
