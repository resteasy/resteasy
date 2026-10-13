/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.cdi.basic.resource;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

/**
 * Exposes the {@link DependentLifecycleTracker} state to the client.
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
@ApplicationScoped
@Path("tracker")
public class DependentLifecycleTrackerResource {

    @Inject
    private DependentLifecycleTracker tracker;

    @GET
    @Path("destroyed")
    public int destroyed() {
        return tracker.getDestroyed();
    }

    @GET
    @Path("destroyed-before-return")
    public boolean destroyedBeforeReturn() {
        return tracker.isDestroyedBeforeReturn();
    }
}
