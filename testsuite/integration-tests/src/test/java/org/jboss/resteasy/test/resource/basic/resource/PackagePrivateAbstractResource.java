/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.resource.basic.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Produces;

/**
 * Package-private abstract class with a JAX-RS resource method.
 * Used to verify that methods inherited from package-private superclasses
 * can be invoked via reflection (RESTEASY-3621).
 */
abstract class PackagePrivateAbstractResource {

    @GET
    @Produces("text/plain")
    public String get() {
        return "hello";
    }
}
