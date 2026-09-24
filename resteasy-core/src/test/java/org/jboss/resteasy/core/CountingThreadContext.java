/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.core;

import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.resteasy.spi.concurrent.ThreadContext;

/**
 * A no-op {@link ThreadContext} which counts how often its context is captured.
 */
class CountingThreadContext implements ThreadContext<Object> {

    private final AtomicInteger captures = new AtomicInteger();

    @Override
    public Object capture() {
        captures.incrementAndGet();
        return new Object();
    }

    @Override
    public void push(final Object context) {
    }

    @Override
    public void reset(final Object context) {
    }

    int captures() {
        return captures.get();
    }
}
