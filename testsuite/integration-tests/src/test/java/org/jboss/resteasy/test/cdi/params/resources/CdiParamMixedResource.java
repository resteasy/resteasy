/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.cdi.params.resources;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;

@Path("/mixed")
@RequestScoped
public class CdiParamMixedResource {

    private final String query;
    private final UriInfo uriInfo;
    private final String customHeader;

    CdiParamMixedResource() {
        this.query = null;
        this.uriInfo = null;
        this.customHeader = null;
    }

    @Inject
    public CdiParamMixedResource(@QueryParam("q") final String query,
            final UriInfo uriInfo,
            @HeaderParam("Custom") final String customHeader) {
        this.query = query;
        this.uriInfo = uriInfo;
        this.customHeader = customHeader;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject get() {
        return Json.createObjectBuilder()
                .add("query", query != null ? query : "")
                .add("path", uriInfo != null ? uriInfo.getPath() : "")
                .add("customHeader", customHeader != null ? customHeader : "")
                .build();
    }
}
