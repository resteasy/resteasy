/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.core.concurrent;

import org.jboss.resteasy.spi.concurrent.ThreadContext;

/**
 * A no-op {@link ThreadContext} located as a service.
 */
public class ServiceThreadContext implements ThreadContext<Object> {

    @Override
    public Object capture() {
        return null;
    }

    @Override
    public void push(final Object context) {
    }

    @Override
    public void reset(final Object context) {
    }
}
