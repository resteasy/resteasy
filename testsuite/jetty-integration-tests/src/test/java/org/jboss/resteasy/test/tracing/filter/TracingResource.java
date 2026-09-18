/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.tracing.filter;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/resource")
public class TracingResource {
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String get() {
        return "traced";
    }
}
