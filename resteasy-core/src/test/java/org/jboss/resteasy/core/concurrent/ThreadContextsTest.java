/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.core.concurrent;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;

import org.jboss.resteasy.spi.concurrent.ThreadContext;
import org.jboss.resteasy.spi.concurrent.ThreadContexts;
import org.junit.jupiter.api.Test;

/**
 * Tests the {@link ThreadContexts} creates new service instances on each request and shares added contexts.
 */
class ThreadContextsTest {

    @Test
    void serviceContextsAreNewInstances() {
        final ThreadContexts threadContexts = new ThreadContexts();
        final ServiceThreadContext first = findService(threadContexts.getThreadContexts());
        final ServiceThreadContext second = findService(threadContexts.getThreadContexts());
        assertNotSame(first, second, "Each request for the contexts should create new service instances");
    }

    @Test
    void addedContextsAreSameInstance() {
        final ThreadContexts threadContexts = new ThreadContexts();
        final NoopThreadContext added = new NoopThreadContext();
        threadContexts.add(added);
        assertTrue(threadContexts.getThreadContexts().contains(added));
        assertTrue(threadContexts.getThreadContexts().contains(added));
    }

    @Test
    @SuppressWarnings("removal")
    void clearIsNoOp() {
        final ThreadContexts threadContexts = new ThreadContexts();
        final NoopThreadContext added = new NoopThreadContext();
        threadContexts.add(added);
        assertSame(threadContexts, threadContexts.clear());
        assertTrue(threadContexts.getThreadContexts().contains(added), "Clearing the contexts should be a no-op");
    }

    @Test
    void copyIncludesServices() {
        final ThreadContexts copy = new ThreadContexts(new ThreadContexts());
        findService(copy.getThreadContexts());
    }

    @Test
    void copyIncludesParentAddedContexts() {
        final ThreadContexts parent = new ThreadContexts();
        final NoopThreadContext added = new NoopThreadContext();
        parent.add(added);
        final ThreadContexts copy = new ThreadContexts(parent);
        assertTrue(copy.getThreadContexts().contains(added), "The copy should include the parent's added contexts");
    }

    @Test
    void contextAddedToCopyNotAddedToParent() {
        final ThreadContexts parent = new ThreadContexts();
        final ThreadContexts copy = new ThreadContexts(parent);
        final NoopThreadContext added = new NoopThreadContext();
        copy.add(added);
        assertFalse(parent.getThreadContexts().contains(added), "Adding to the copy should not change the parent");
    }

    private static ServiceThreadContext findService(final Collection<ThreadContext<Object>> contexts) {
        return contexts.stream()
                .filter(ServiceThreadContext.class::isInstance)
                .map(ServiceThreadContext.class::cast)
                .findFirst()
                .orElseThrow(() -> new AssertionError("ServiceThreadContext was not located: " + contexts));
    }

    private static class NoopThreadContext implements ThreadContext<Object> {
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
}
