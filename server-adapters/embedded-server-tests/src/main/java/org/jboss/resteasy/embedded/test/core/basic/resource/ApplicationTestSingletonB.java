/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.embedded.test.core.basic.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/singletons")
public class ApplicationTestSingletonB {

    @Path("b")
    @GET
    public String get() {
        return "b";
    }
}
