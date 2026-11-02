/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2021 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.resteasy.concurrent;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.concurrent.ThreadContexts;

/**
 * An {@linkplain ExecutorService executor} which wraps runnables and callables to capture the context of the current
 * thread.
 * <p>
 * If a server is {@linkplain #isManaged() managed} it's the responsibility of the user or container to manage the
 * lifecycle of the wrapped executor service.
 * </p>
 * <p>
 * <strong>Note:</strong> if the executor is consider managed, for example running in a Jakarta EE environment, the
 * following methods are effectively ignored.
 * <ul>
 * <li>{@link #shutdown()}</li>
 * <li>{@link #shutdownNow()}</li>
 * <li>{@link #isShutdown()}</li>
 * <li>{@link #isTerminated()}</li>
 * <li>{@link #awaitTermination(long, TimeUnit)}</li>
 * </ul>
 * </p>
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 * @since 5.0.0
 */
public class ContextualExecutorService implements ExecutorService {

    private final boolean managed;
    private final ThreadContexts threadContexts;
    private final AtomicBoolean shutdown;
    private volatile ExecutorService delegate;

    /**
     * Creates a new contextual executor.
     *
     * @param delegate the contextual executor whose delegate and thread contexts are copied
     * @param managed  {@code true} if the delegate is managed by the user or container
     */
    ContextualExecutorService(final ContextualExecutorService delegate, final boolean managed) {
        this(delegate.delegate, managed, delegate.threadContexts);
    }

    /**
     * Creates a new contextual executor.
     *
     * @param delegate the executor to delegate to
     * @param managed  {@code true} if the delegate is managed by the user or container
     */
    ContextualExecutorService(final ExecutorService delegate, final boolean managed) {
        this(delegate, managed, null);
    }

    /**
     * Creates a new contextual executor.
     *
     * @param delegate       the executor to delegate to
     * @param managed        {@code true} if the delegate is managed by the user or container
     * @param threadContexts the thread contexts to capture for each task
     */
    ContextualExecutorService(final ContextualExecutorService delegate, final boolean managed,
            final ThreadContexts threadContexts) {
        this(delegate.delegate, managed, threadContexts);
    }

    /**
     * Creates a new contextual executor.
     *
     * @param delegate       the executor to delegate to
     * @param managed        {@code true} if the delegate is managed by the user or container
     * @param threadContexts the thread contexts to capture for each task
     */
    ContextualExecutorService(final ExecutorService delegate, final boolean managed,
            final ThreadContexts threadContexts) {
        this.delegate = delegate;
        this.managed = managed;
        this.threadContexts = threadContexts;
        shutdown = new AtomicBoolean(false);
    }

    @Override
    public void shutdown() {
        if (shutdown.compareAndSet(false, true)) {
            if (isManaged()) {
                // Clear the delegate as we're done with it
                delegate = null;
            } else {
                final ExecutorService delegate = getDelegate();
                delegate.shutdown();
            }
        }
    }

    @Override
    public List<Runnable> shutdownNow() {
        if (shutdown.compareAndSet(false, true)) {
            if (isManaged()) {
                // Clear the delegate as we're done with it
                delegate = null;
            } else {
                final ExecutorService delegate = getDelegate();
                return delegate.shutdownNow();
            }
        }
        return Collections.emptyList();
    }

    @Override
    public boolean isShutdown() {
        if (isManaged()) {
            return shutdown.get();
        }
        return getDelegate().isShutdown();
    }

    @Override
    public boolean isTerminated() {
        if (isManaged()) {
            return false;
        }
        return getDelegate().isTerminated();
    }

    @Override
    public boolean awaitTermination(final long timeout, final TimeUnit unit) throws InterruptedException {
        if (isManaged()) {
            return false;
        }
        return getDelegate().awaitTermination(timeout, unit);
    }

    @Override
    public <T> Future<T> submit(final Callable<T> task) {
        return getDelegate().submit(ContextualExecutors.callable(threadContexts(), task));
    }

    @Override
    public <T> Future<T> submit(final Runnable task, final T result) {
        return getDelegate().submit(ContextualExecutors.runnable(threadContexts(), task), result);
    }

    @Override
    public Future<?> submit(final Runnable task) {
        return getDelegate().submit(ContextualExecutors.runnable(threadContexts(), task));
    }

    @Override
    public <T> List<Future<T>> invokeAll(
            final Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return getDelegate().invokeAll(ContextualExecutors.callable(threadContexts(), tasks));
    }

    @Override
    public <T> List<Future<T>> invokeAll(
            final Collection<? extends Callable<T>> tasks, final long timeout,
            final TimeUnit unit) throws InterruptedException {
        return getDelegate().invokeAll(ContextualExecutors.callable(threadContexts(), tasks), timeout, unit);
    }

    @Override
    public <T> T invokeAny(final Collection<? extends Callable<T>> tasks)
            throws InterruptedException, ExecutionException {
        return getDelegate().invokeAny(ContextualExecutors.callable(threadContexts(), tasks));
    }

    @Override
    public <T> T invokeAny(final Collection<? extends Callable<T>> tasks, final long timeout,
            final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return getDelegate().invokeAny(ContextualExecutors.callable(threadContexts(), tasks), timeout, unit);
    }

    @Override
    public void execute(final Runnable command) {
        getDelegate().execute(ContextualExecutors.runnable(threadContexts(), command));
    }

    /**
     * Indicates this executor is managed and the following methods are not executed. If the method has a return type
     * a default value is returned.
     * <ul>
     * <li>{@link #shutdown()}</li>
     * <li>{@link #shutdownNow()}</li>
     * <li>{@link #isShutdown()}</li>
     * <li>{@link #isTerminated()}</li>
     * <li>{@link #awaitTermination(long, TimeUnit)}</li>
     * </ul>
     *
     * @return {@code true} if this is a managed executor, otherwise {@code false}
     */
    public boolean isManaged() {
        return managed;
    }

    /**
     * Returns the thread contexts associated with this executor. If there were no initial thread contexts associated,
     * the {@link ResteasyProviderFactory#getInstance()} is used to
     * {@linkplain ResteasyProviderFactory#getThreadContexts() get the thread contexts}.
     *
     * @return the thread contexts for this executor
     */
    ThreadContexts threadContexts() {
        return threadContexts != null ? threadContexts : ResteasyProviderFactory.getInstance().getThreadContexts();
    }

    ExecutorService getDelegate() {
        final ExecutorService delegate = this.delegate;
        if (delegate == null) {
            throw Messages.MESSAGES.executorShutdown();
        }
        return delegate;
    }
}
