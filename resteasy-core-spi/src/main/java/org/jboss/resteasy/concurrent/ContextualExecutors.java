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

import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.spi.concurrent.ThreadContext;
import org.jboss.resteasy.spi.config.Configuration;
import org.jboss.resteasy.spi.config.ConfigurationFactory;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A utility to create and/or wrap {@linkplain ExecutorService executors} in a contextual executor.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 * @see org.jboss.resteasy.concurrent.ContextualExecutorService
 * @see org.jboss.resteasy.concurrent.ContextualScheduledExecutorService
 * @since 5.0.0
 */
public class ContextualExecutors {

    /**
     * Creates a new {@link ContextualExecutorService} or returns the default {@code ManagedExecutorService} in a
     * Jakarta EE environment.
     * <p>
     * If being created in a Jakarta EE container which includes a default {@code ManagedExecutorService} that executor
     * is wrapped an said to be managed. If the default executor service cannot be found or if not being executed in a
     * Jakarta EE container a new {@linkplain Executors#newCachedThreadPool() cached thread pool} will be wrapped.
     * </p>
     *
     * @return a new contextual executor
     */
    public static ContextualExecutorService threadPool() {
        ExecutorService delegate;
        boolean managed = false;
        try {
            delegate = InitialContext.doLookup("java:comp/DefaultManagedExecutorService");
            managed = true;
        } catch (NamingException ignore) {
            delegate = Executors.newCachedThreadPool();
        }
        return wrap(delegate, managed);
    }

    /**
     * Creates a new {@link ContextualExecutorService} or returns the default {@code ManagedExecutorService} in a
     * Jakarta EE environment.
     * <p>
     * If being created in a Jakarta EE container which includes a default {@code ManagedExecutorService} that executor
     * is wrapped an said to be managed. If the default executor service cannot be found or if not being executed in a
     * Jakarta EE container a new {@linkplain Executors#newFixedThreadPool(int)} fixed thread pool} will be wrapped.
     * </p>
     *
     * @param poolSize the size for the fixed thread pool in cases where it gets created
     *
     * @return a new contextual executor
     */
    public static ContextualExecutorService threadPool(final int poolSize) {
        ExecutorService delegate;
        boolean managed = false;
        try {
            delegate = InitialContext.doLookup("java:comp/DefaultManagedExecutorService");
            managed = true;
        } catch (NamingException ignore) {
            delegate = Executors.newFixedThreadPool(poolSize);
        }
        return wrap(delegate, managed);
    }

    /**
     * Creates a new {@link ContextualScheduledExecutorService} or returns the default {@code ManagedScheduledExecutorService}
     * in a Jakarta EE environment.
     * <p>
     * If being created in a Jakarta EE container which includes a default {@code ManagedScheduledExecutorService} that executor
     * is wrapped an said to be managed. If the default executor service cannot be found or if not being executed in a
     * Jakarta EE container a new {@linkplain Executors#newScheduledThreadPool(int) scheduled thread pool} will be
     * wrapped. The size of the thread pool is retrieved via the {@code resteasy.async.timeout.scheduler.min.pool.size}
     * context parameter. If not found {@code 1} is the default.
     * </p>
     *
     * @return a new contextual executor
     */
    public static ContextualScheduledExecutorService scheduledThreadPool() {
        ScheduledExecutorService delegate;
        boolean managed = false;
        try {
            delegate = InitialContext.doLookup("java:comp/DefaultManagedScheduledExecutorService");
            managed = true;
        } catch (NamingException ignore) {
            final Configuration config = ConfigurationFactory.getInstance().getConfiguration();
            final int poolSize = config.getOptionalValue("resteasy.async.timeout.scheduler.min.pool.size", Integer.class)
                    .orElse(1);
            delegate = Executors.newScheduledThreadPool(poolSize);
        }
        return wrap(delegate, managed);
    }

    /**
     * Creates a new {@link ContextualScheduledExecutorService} or returns the default {@code ManagedScheduledExecutorService}
     * in a Jakarta EE environment.
     * <p>
     * If being created in a Jakarta EE container which includes a default {@code ManagedScheduledExecutorService} that executor
     * is wrapped an said to be managed. If the default executor service cannot be found or if not being executed in a
     * Jakarta EE container a new {@linkplain Executors#newScheduledThreadPool(int) scheduled thread pool} will be
     * wrapped.
     * </p>
     *
     * @param poolSize      the size of the pool to create
     * @param threadFactory the thread factory to use if a new executor is created
     *
     * @return a new contextual executor
     */
    public static ContextualScheduledExecutorService scheduledThreadPool(final int poolSize,
                                                                         final ThreadFactory threadFactory) {
        ScheduledExecutorService delegate;
        boolean managed = false;
        try {
            delegate = InitialContext.doLookup("java:comp/DefaultManagedScheduledExecutorService");
            managed = true;
        } catch (NamingException ignore) {
            delegate = Executors.newScheduledThreadPool(poolSize, threadFactory);
        }
        return wrap(delegate, managed);
    }

    /**
     * Wraps the executor service in contextual executor and is said to be managed. You are responsible for the
     * lifecycle of the delegate.
     *
     * @param delegate the executor to wrap
     *
     * @return a new contextual executor
     */
    public static ContextualExecutorService wrap(final ExecutorService delegate) {
        return wrap(delegate, true);
    }

    /**
     * Wraps the executor service in contextual executor and is said to be managed. You are responsible for the
     * lifecycle of the delegate.
     *
     * @param delegate the executor to wrap
     * @param managed  {@code true} if the lifecycle is managed by another process
     *
     * @return a new contextual executor
     */
    public static ContextualExecutorService wrap(final ExecutorService delegate, final boolean managed) {
        if (delegate == null) {
            return null;
        }
        if (delegate instanceof ScheduledExecutorService) {
            return wrap(((ScheduledExecutorService) delegate), managed);
        }
        if (delegate instanceof ContextualExecutorService && managed == ((ContextualExecutorService) delegate).isManaged()) {
            if (managed == ((ContextualExecutorService) delegate).isManaged()) {
                return (ContextualExecutorService) delegate;
            }
            return new ContextualExecutorService(((ContextualExecutorService) delegate).getDelegate(), managed);
        }
        return new ContextualExecutorService(delegate, managed);
    }

    /**
     * Wraps the executor service in contextual executor and is said to be managed. You are responsible for the
     * lifecycle of the delegate.
     *
     * @param delegate the executor to wrap
     *
     * @return a new contextual executor
     */
    public static ContextualScheduledExecutorService wrap(final ScheduledExecutorService delegate) {
        return wrap(delegate, true);
    }

    /**
     * Wraps the executor service in contextual executor and is said to be managed. You are responsible for the
     * lifecycle of the delegate.
     *
     * @param delegate the executor to wrap
     * @param managed  {@code true} if the lifecycle is managed by another process
     *
     * @return a new contextual executor
     */
    public static ContextualScheduledExecutorService wrap(final ScheduledExecutorService delegate,
                                                          final boolean managed) {
        if (delegate == null) {
            return null;
        }
        if (delegate instanceof ContextualScheduledExecutorService && managed == ((ContextualScheduledExecutorService) delegate).isManaged()) {
            if (managed == ((ContextualScheduledExecutorService) delegate).isManaged()) {
                return (ContextualScheduledExecutorService) delegate;
            }
            return new ContextualScheduledExecutorService(((ContextualScheduledExecutorService) delegate).getDelegate(), managed);
        }
        return new ContextualScheduledExecutorService(delegate, managed);
    }

    /**
     * Creates a new {@linkplain Runnable runnable} which sets up the current context before the runnable is executed.
     * Finally, resetting the context.
     *
     * @param task the runnable to wrap
     *
     * @return a new contextual runnable
     */
    public static Runnable runnable(final Runnable task) {
        final Map<ThreadContext<Object>, Object> contexts = getContexts();
        return () -> {
            try {
                for (Map.Entry<ThreadContext<Object>, Object> entry : contexts.entrySet()) {
                    entry.getKey().push(entry.getValue());
                }
                task.run();
            } finally {
                reset(contexts.keySet());
            }
        };
    }

    /**
     * Creates a new {@linkplain Callable callable} which sets up the current context before the callable is executed.
     * Finally, resetting the context.
     *
     * @param task the callable to wrap
     * @param <V>  the return type
     *
     * @return a new contextual callable
     */
    public static <V> Callable<V> callable(final Callable<V> task) {
        final Map<ThreadContext<Object>, Object> contexts = getContexts();
        return () -> {
            try {
                for (Map.Entry<ThreadContext<Object>, Object> entry : contexts.entrySet()) {
                    entry.getKey().push(entry.getValue());
                }
                return task.call();
            } finally {
                reset(contexts.keySet());
            }
        };
    }

    /**
     * Creates a new collection of {@linkplain Callable callables} which sets up the current context before each
     * callable is executed. Finally, resetting the context.
     *
     * @param tasks the callables to wrap
     * @param <T>   the return type of the callable
     *
     * @return a collection of new contextual callables
     *
     * @see #callable(Callable)
     */
    public static <T> Collection<? extends Callable<T>> callable(final Collection<? extends Callable<T>> tasks) {
        return tasks.stream()
                .map((Function<Callable<T>, Callable<T>>) ContextualExecutors::callable)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private static Map<ThreadContext<Object>, Object> getContexts() {
        if (System.getSecurityManager() == null) {
            final Map<ThreadContext<Object>, Object> contexts = new LinkedHashMap<>();
            ServiceLoader.load(ThreadContext.class).forEach(context -> contexts.put(context, context.capture()));
            return contexts;
        }
        return AccessController.doPrivileged((PrivilegedAction<Map<ThreadContext<Object>, Object>>) () -> {
            final Map<ThreadContext<Object>, Object> contexts = new LinkedHashMap<>();
            ServiceLoader.load(ThreadContext.class).forEach(context -> contexts.put(context, context.capture()));
            return contexts;
        });
    }

    private static void reset(final Collection<ThreadContext<Object>> contexts) {
        Throwable error = null;
        for (ThreadContext<Object> context : contexts) {
            try {
                context.reset();
            } catch (Throwable t) {
                if (error == null) {
                    error = t;
                } else {
                    error.addSuppressed(t);
                }
            }
        }
        if (error != null) {
            LogMessages.LOGGER.unableToResetThreadContext(error, Thread.currentThread().getName());
        }
    }
}
