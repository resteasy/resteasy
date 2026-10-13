/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.cdi.basic.resource;

import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import jakarta.enterprise.concurrent.ManagedExecutorService;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.container.AsyncResponse;

/**
 * A {@link Dependent} resource which records when it is destroyed.
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
@Dependent
@Path("dependent")
public class DependentResource {

    @Inject
    private DependentLifecycleTracker tracker;

    @Resource
    private ManagedExecutorService executor;

    private volatile boolean destroyed;

    @GET
    @Path("sync")
    public String sync() {
        return "sync";
    }

    @GET
    @Path("async")
    public void async(final AsyncResponse asyncResponse) {
        executor.execute(() -> asyncResponse.resume("async"));
    }

    @GET
    @Path("async-invoking")
    public void asyncInvoking(final AsyncResponse asyncResponse) {
        asyncResponse.resume("async-invoking");
        // The resource must not be destroyed until this method returns
        tracker.setDestroyedBeforeReturn(destroyed);
    }

    @PreDestroy
    public void destroy() {
        destroyed = true;
        tracker.destroyed();
    }
}
