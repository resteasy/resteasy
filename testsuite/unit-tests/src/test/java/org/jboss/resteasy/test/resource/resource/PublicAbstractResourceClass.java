/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.resource.resource;

import jakarta.ws.rs.GET;

/**
 * @author <a href="mailto:tom@intevation.de">Tom Gottfried</a>
 */
public abstract class PublicAbstractResourceClass {
    @GET
    public String get() {
        return "test";
    }
}
