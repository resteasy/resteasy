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

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * An {@linkplain ScheduledExecutorService executor} which wraps runnables and callables to capture the context of the
 * current thread.
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
public class ContextualScheduledExecutorService extends ContextualExecutorService implements ScheduledExecutorService {

    private final ScheduledExecutorService delegate;

    ContextualScheduledExecutorService(final ScheduledExecutorService delegate, final boolean managed) {
        super(delegate, managed);
        this.delegate = delegate;
    }

    @Override
    public ScheduledFuture<?> schedule(final Runnable command, final long delay, final TimeUnit unit) {
        return delegate.schedule(ContextualExecutors.runnable(command), delay, unit);
    }

    @Override
    public <V> ScheduledFuture<V> schedule(final Callable<V> callable, final long delay, final TimeUnit unit) {
        return delegate.schedule(ContextualExecutors.callable(callable), delay, unit);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(final Runnable command, final long initialDelay, final long period,
                                                  final TimeUnit unit) {
        return delegate.scheduleAtFixedRate(ContextualExecutors.runnable(command), initialDelay, period, unit);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(final Runnable command, final long initialDelay, final long delay,
                                                     final TimeUnit unit) {
        return delegate.scheduleWithFixedDelay(ContextualExecutors.runnable(command), initialDelay, delay, unit);
    }

    @Override
    ScheduledExecutorService getDelegate() {
        return delegate;
    }
}
