/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.core.servlet.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.plugins.server.servlet.ServletBootstrap;
import org.jboss.resteasy.spi.ResteasyConfiguration;

/**
 * Test resource for RESTEASY-3678.
 * Verifies that ServletBootstrap.getParameter() checks servlet init-params before context-params.
 */
@Path("bootstrap")
public class ServletBootstrapParameterResource {

    /**
     * Returns the value of the test parameter that exists in both servlet init-param and context-param.
     * The servlet init-param should take precedence.
     */
    @GET
    @Path("shared-param")
    @Produces(MediaType.TEXT_PLAIN)
    public String getSharedParameter() {
        final String value = getParameter("shared.parameter");
        return value != null ? value : "null";
    }

    /**
     * Returns the value of a parameter that only exists in context-param.
     */
    @GET
    @Path("context-only")
    @Produces(MediaType.TEXT_PLAIN)
    public String getContextOnlyParameter() {
        final String value = getParameter("context.only.parameter");
        return value != null ? value : "null";
    }

    /**
     * Returns the value of a parameter that only exists in servlet init-param.
     */
    @GET
    @Path("servlet-only")
    @Produces(MediaType.TEXT_PLAIN)
    public String getServletOnlyParameter() {
        final String value = getParameter("servlet.only.parameter");
        return value != null ? value : "null";
    }

    private static String getParameter(final String name) {
        final ResteasyConfiguration config = ResteasyContext.getRequiredContextData(ResteasyConfiguration.class);
        return config.getParameter(name);
    }
}
