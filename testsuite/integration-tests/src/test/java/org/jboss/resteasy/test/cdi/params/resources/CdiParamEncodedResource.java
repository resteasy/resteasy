/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.cdi.params.resources;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Encoded;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/encoded/{p}")
@RequestScoped
@Encoded
public class CdiParamEncodedResource {

    @Inject
    @QueryParam("q")
    private String queryValue;

    @Inject
    @PathParam("p")
    private String pathValue;

    CdiParamEncodedResource() {
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public EncodedDescriptor get() {
        return EncodedDescriptor.of()
                .setQueryValue(queryValue)
                .setPathValue(pathValue);
    }
}
