/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.atomic.AtomicReference;

import org.jboss.resteasy.concurrent.ContextualExecutors;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.concurrent.ThreadContexts;
import org.junit.jupiter.api.Test;

/**
 * Tests that wrapping tasks on a thread with no {@link ThreadContexts} in its context data uses the provider
 * factory's thread contexts instead of loading the providers again for each task.
 *
 * @see <a href="https://issues.redhat.com/browse/RESTEASY-3313">RESTEASY-3313</a>
 */
class ContextualExecutorsFallbackTest {

    /**
     * The provider factory should hand out one {@link ThreadContexts} instance for its lifetime, from any thread.
     * A single instance means the {@code ThreadContext} providers are loaded once, not once per wrapped task.
     */
    @Test
    void factoryThreadContextsIsStable() throws Exception {
        final ResteasyProviderFactory factory = ResteasyProviderFactory.getInstance();
        final ThreadContexts first = factory.getThreadContexts();
        final AtomicReference<ThreadContexts> fromOtherThread = new AtomicReference<>();
        runOnBareThread(() -> fromOtherThread.set(factory.getThreadContexts()));
        assertSame(first, fromOtherThread.get());
    }

    /**
     * A context registered with the provider factory's thread contexts must be captured by tasks wrapped on threads
     * without a {@code ThreadContexts} entry in their context data.
     */
    @Test
    void fallbackContextsComeFromFactory() throws Exception {
        final ResteasyProviderFactory factory = ResteasyProviderFactory.getInstance();
        final CountingThreadContext counting = new CountingThreadContext();
        factory.getThreadContexts().add(counting);

        runOnBareThread(() -> ContextualExecutors.runnable(() -> {
        }).run());
        runOnBareThread(() -> ContextualExecutors.runnable(() -> {
        }).run());

        assertEquals(2, counting.captures(),
                "Each wrapped task should capture the contexts registered with the provider factory");
    }

    /**
     * A thread whose context class loader is not the class loader that defined the provider factory must not use the
     * factory's cached contexts. A shared factory must not fix one deployment's provider view for all others.
     */
    @Test
    void foreignContextClassLoaderDoesNotUseFactoryContexts() throws Exception {
        final ResteasyProviderFactory factory = ResteasyProviderFactory.getInstance();
        final CountingThreadContext counting = new CountingThreadContext();
        factory.getThreadContexts().add(counting);

        try (URLClassLoader foreignLoader = new URLClassLoader(new URL[0], null)) {
            runOnBareThread(() -> {
                Thread.currentThread().setContextClassLoader(foreignLoader);
                ContextualExecutors.runnable(() -> {
                }).run();
            });
        }

        assertEquals(0, counting.captures(),
                "A task wrapped under a foreign context class loader should not capture the factory's contexts");
    }

    private static void runOnBareThread(final Runnable task) throws Exception {
        final AtomicReference<Throwable> failure = new AtomicReference<>();
        final Thread thread = new Thread(task);
        thread.setUncaughtExceptionHandler((t, e) -> failure.set(e));
        thread.start();
        thread.join(10_000L);
        final Throwable thrown = failure.get();
        if (thrown != null) {
            throw new AssertionError("Task on bare thread failed", thrown);
        }
    }
}
