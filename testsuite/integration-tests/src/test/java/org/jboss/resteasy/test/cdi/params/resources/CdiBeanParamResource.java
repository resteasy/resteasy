/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.cdi.params.resources;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/bean-param")
@RequestScoped
public class CdiBeanParamResource {

    @Inject
    @BeanParam
    private SearchParams params;

    CdiBeanParamResource() {
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public SearchParams get() {
        return params;
    }
}
