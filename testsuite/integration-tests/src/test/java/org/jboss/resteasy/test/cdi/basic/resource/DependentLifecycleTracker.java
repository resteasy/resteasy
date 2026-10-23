/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.cdi.basic.resource;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * Records when {@link DependentResource} instances are destroyed.
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
@ApplicationScoped
public class DependentLifecycleTracker {
    private final AtomicInteger destroyed = new AtomicInteger();
    private final AtomicBoolean destroyedBeforeReturn = new AtomicBoolean();

    public void destroyed() {
        destroyed.incrementAndGet();
    }

    public int getDestroyed() {
        return destroyed.get();
    }

    public void setDestroyedBeforeReturn(final boolean value) {
        destroyedBeforeReturn.set(value);
    }

    public boolean isDestroyedBeforeReturn() {
        return destroyedBeforeReturn.get();
    }
}
