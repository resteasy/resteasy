/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.cdi.injection.resource;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.servlet.ServletConfig;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;

@Path("/servlet-config")
@RequestScoped
public class ServletConfigSetterResource {
    private ServletConfig servletConfig;

    @Inject
    @Context
    public void setServletConfig(final ServletConfig servletConfig) {
        this.servletConfig = servletConfig;
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String servletName() {
        return servletConfig.getServletName();
    }
}
