/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.resteasy.test.cdi.injection.resource;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/orders")
@RequestScoped
public class OrderResource {
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String get() {
        return "original";
    }
}
