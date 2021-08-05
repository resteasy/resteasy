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
 *     <li>{@link #shutdown()}</li>
 *     <li>{@link #shutdownNow()}</li>
 *     <li>{@link #isShutdown()}</li>
 *     <li>{@link #isTerminated()}</li>
 *     <li>{@link #awaitTermination(long, TimeUnit)}</li>
 * </ul>
 * </p>
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 * @since 5.0.0
 */
public class ContextualExecutorService implements ExecutorService {

    private final ExecutorService delegate;
    private final boolean managed;

    ContextualExecutorService(final ExecutorService delegate, final boolean managed) {
        this.delegate = delegate;
        this.managed = managed;
    }

    @Override
    public void shutdown() {
        if (!managed) {
            delegate.shutdown();
        }
    }

    @Override
    public List<Runnable> shutdownNow() {
        if (managed) {
            return Collections.emptyList();
        }
        return delegate.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        if (managed) {
            return false;
        }
        return delegate.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        if (managed) {
            return false;
        }
        return delegate.isTerminated();
    }

    @Override
    public boolean awaitTermination(final long timeout, final TimeUnit unit) throws InterruptedException {
        if (managed) {
            return false;
        }
        return delegate.awaitTermination(timeout, unit);
    }

    @Override
    public <T> Future<T> submit(final Callable<T> task) {
        return delegate.submit(ContextualExecutors.callable(task));
    }

    @Override
    public <T> Future<T> submit(final Runnable task, final T result) {
        return delegate.submit(ContextualExecutors.runnable(task), result);
    }

    @Override
    public Future<?> submit(final Runnable task) {
        return delegate.submit(ContextualExecutors.runnable(task));
    }

    @Override
    public <T> List<Future<T>> invokeAll(
            final Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return delegate.invokeAll(ContextualExecutors.callable(tasks));
    }

    @Override
    public <T> List<Future<T>> invokeAll(
            final Collection<? extends Callable<T>> tasks, final long timeout,
            final TimeUnit unit) throws InterruptedException {
        return delegate.invokeAll(ContextualExecutors.callable(tasks), timeout, unit);
    }

    @Override
    public <T> T invokeAny(final Collection<? extends Callable<T>> tasks)
            throws InterruptedException, ExecutionException {
        return delegate.invokeAny(ContextualExecutors.callable(tasks));
    }

    @Override
    public <T> T invokeAny(final Collection<? extends Callable<T>> tasks, final long timeout,
                           final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return delegate.invokeAny(ContextualExecutors.callable(tasks), timeout, unit);
    }

    @Override
    public void execute(final Runnable command) {
        delegate.execute(ContextualExecutors.runnable(command));
    }

    /**
     * Indicates this executor is managed and the following methods are not executed. If the method has a return type
     * a default value is returned.
     * <ul>
     *     <li>{@link #shutdown()}</li>
     *     <li>{@link #shutdownNow()}</li>
     *     <li>{@link #isShutdown()}</li>
     *     <li>{@link #isTerminated()}</li>
     *     <li>{@link #awaitTermination(long, TimeUnit)}</li>
     * </ul>
     *
     * @return {@code true} if this is a managed executor, otherwise {@code false}
     */
    public boolean isManaged() {
        return managed;
    }

    ExecutorService getDelegate() {
        return delegate;
    }

}
