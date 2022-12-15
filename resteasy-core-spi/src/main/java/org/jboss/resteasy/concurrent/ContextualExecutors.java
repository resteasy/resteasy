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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.concurrent.ThreadContext;
import org.jboss.resteasy.spi.concurrent.ThreadContexts;

/**
 * A utility to create and/or wrap {@linkplain ExecutorService executors} in a contextual executor.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 * @see ContextualExecutorService
 * @see ContextualScheduledExecutorService
 * @see Executor
 * @since 5.0.0
 */
public class ContextualExecutors {
    private static final String EXECUTOR_SERVICE_JNDI = "java:comp/DefaultManagedExecutorService";
    private static final String SCHEDULED_EXECUTOR_SERVICE_JNDI = "java:comp/DefaultManagedScheduledExecutorService";

    private static final Map<String, Boolean> JNDI_LOOKUPS = new ConcurrentHashMap<>();

    /**
     * An executor which executes tasks in the current thread
     *
     * @return a new contextual executor
     */
    public static Executor executor() {
        final Map<ThreadContext<Object>, Object> contexts = getContexts();
        return (task) -> runnable(contexts, task).run();
    }

    /**
     * Wraps the executor in contextual executor.
     * <p>
     * The context is copied in before each invocation of the delegate, then reset after the thread is done executing.
     * </p>
     *
     * @param delegate the executor to wrap
     *
     * @return a new contextual executor
     */
    public static Executor wrap(final Executor delegate) {
        if (delegate instanceof ContextualExecutor || delegate instanceof ContextualExecutorService) {
            return delegate;
        }
        return new ContextualExecutor(delegate);
    }

    /**
     * Creates a new {@link ContextualExecutorService} or wraps the default {@code ManagedExecutorService} in a
     * Jakarta EE environment.
     * <p>
     * If executed in a Jakarta EE container which includes a default {@code ManagedExecutorService}, that executor
     * is wrapped an said to be managed. If the default executor service cannot be found or if not being executed in a
     * Jakarta EE container a new {@linkplain Executors#newCachedThreadPool() cached thread pool} will be wrapped.
     * </p>
     * <p>
     * In a Jakarta EE container the JNDI lookup name can be overridden with the {@code resteasy.async.executor.service.jndi}
     * configuration property. By default the JNDI lookup name is {@code java:comp/DefaultManagedExecutorService}.
     * </p>
     *
     * @return a new contextual executor
     */
    public static ContextualExecutorService threadPool() {
        ExecutorService delegate = lookup(EXECUTOR_SERVICE_JNDI);
        if (delegate == null) {
            delegate = GlobalContextualExecutorService.INSTANCE;
        }
        return wrap(delegate, true);
    }

    /**
     * Creates a new {@link ContextualScheduledExecutorService} or wraps the default {@code ManagedScheduledExecutorService}
     * in a Jakarta EE environment.
     * <p>
     * If executed in a Jakarta EE container which includes a default {@code ManagedScheduledExecutorService}, that executor
     * is wrapped an said to be managed. If the default executor service cannot be found or if not being executed in a
     * Jakarta EE container a new {@linkplain Executors#newScheduledThreadPool(int) scheduled thread pool} will be
     * wrapped. The size of the thread pool is retrieved via the {@code resteasy.async.timeout.scheduler.min.pool.size}
     * context parameter. If not found {@code 1} is the default. The thread pool size is ignored in Jakarta EE
     * environments.
     * </p>
     * <p>
     * In a Jakarta EE container the JNDI lookup name can be overridden with the
     * {@code resteasy.async.scheduled.executor.service.jndi} configuration property. By default the JNDI lookup name is
     * {@code java:comp/DefaultManagedScheduledExecutorService}.
     * </p>
     *
     * @return a new contextual executor
     */
    public static ContextualScheduledExecutorService scheduledThreadPool() {
        ScheduledExecutorService delegate = lookup(SCHEDULED_EXECUTOR_SERVICE_JNDI);
        if (delegate == null) {
            delegate = GlobalContextualScheduledExecutorService.INSTANCE;
        }
        return wrap(delegate, true);
    }

    /**
     * Creates a new {@link ContextualScheduledExecutorService} or wraps the default {@code ManagedScheduledExecutorService}
     * in a Jakarta EE environment.
     * <p>
     * If executed in a Jakarta EE container which includes a default {@code ManagedScheduledExecutorService}, that executor
     * is wrapped an said to be managed. If the default executor service cannot be found or if not being executed in a
     * Jakarta EE container a new {@linkplain Executors#newScheduledThreadPool(int) scheduled thread pool} will be
     * wrapped.
     * </p>
     * <p>
     * In a Jakarta EE container the JNDI lookup name can be overridden with the
     * {@code resteasy.async.scheduled.executor.service.jndi} configuration property. By default the JNDI lookup name is
     * {@code java:comp/DefaultManagedScheduledExecutorService}.
     * </p>
     *
     * @param poolSize      the size of the pool to create, ignored in a Jakarta EE container
     * @param threadFactory the thread factory to use if a new executor is created, ignored in a Jakarta EE container
     *
     * @return a new contextual executor
     */
    public static ContextualScheduledExecutorService scheduledThreadPool(final int poolSize,
            final ThreadFactory threadFactory) {
        ScheduledExecutorService delegate = lookup(SCHEDULED_EXECUTOR_SERVICE_JNDI);
        boolean managed = true;
        if (delegate == null) {
            delegate = Executors.newScheduledThreadPool(poolSize, threadFactory);
            managed = false;
        }
        return wrap(delegate, managed);
    }

    /**
     * Wraps the executor service in contextual executor and is said to be managed. You are responsible for the
     * lifecycle of the delegate.
     * <p>
     * The context is copied in before each invocation of the delegate, then reset after the thread is done executing.
     * </p>
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
     * <p>
     * The context is copied in before each invocation of the delegate, then reset after the thread is done executing.
     * </p>
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
     * <p>
     * The context is copied in before each invocation of the delegate, then reset after the thread is done executing.
     * </p>
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
     * <p>
     * The context is copied in before each invocation of the delegate, then reset after the thread is done executing.
     * </p>
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
        if (delegate instanceof ContextualScheduledExecutorService
                && managed == ((ContextualScheduledExecutorService) delegate).isManaged()) {
            if (managed == ((ContextualScheduledExecutorService) delegate).isManaged()) {
                return (ContextualScheduledExecutorService) delegate;
            }
            return new ContextualScheduledExecutorService(((ContextualScheduledExecutorService) delegate).getDelegate(),
                    managed);
        }
        return new ContextualScheduledExecutorService(delegate, managed);
    }

    /**
     * If the delegate is not {@code null}, it is wrapped. If the delegate is {@code null} and this is a Jakarta EE
     * environment an attempt will be made to look up the default {@code ManagedScheduledExecutorService}. If found the
     * default {@code ManagedScheduledExecutorService} will be wrapped. Otherwise, {@code null} will be returned.
     *
     * @param delegate the delegate to wrap, which may be {@code null}
     *
     * @return a wrapped contextual executor or {@code null} if no executor was provided or could be found
     *
     * @see #wrap(ScheduledExecutorService)
     */
    public static ContextualScheduledExecutorService wrapOrLookup(final ScheduledExecutorService delegate) {
        if (delegate != null) {
            return wrap(delegate, true);
        }
        ScheduledExecutorService found = lookup(SCHEDULED_EXECUTOR_SERVICE_JNDI);
        return found == null ? null : wrap(found, true);
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
        return runnable(getContexts(), task);
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
                reset(contexts);
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

    private static Runnable runnable(final Map<ThreadContext<Object>, Object> contexts, final Runnable task) {
        return () -> {
            try {
                for (Map.Entry<ThreadContext<Object>, Object> entry : contexts.entrySet()) {
                    entry.getKey().push(entry.getValue());
                }
                task.run();
            } finally {
                reset(contexts);
            }
        };
    }

    private static Map<ThreadContext<Object>, Object> getContexts() {
        final Map<ThreadContext<Object>, Object> contexts = new LinkedHashMap<>();
        // Load any registered providers
        ThreadContexts threadContexts = ResteasyProviderFactory.getInstance()
                .getContextData(ThreadContexts.class);
        // Create a new ThreadContexts which will load at least the ones from services
        if (threadContexts == null) {
            threadContexts = new ThreadContexts();
        }
        for (ThreadContext<Object> context : threadContexts.getThreadContexts()) {
            contexts.put(context, context.capture());
        }
        return contexts;
    }

    private static void reset(final Map<ThreadContext<Object>, Object> contexts) {
        Throwable error = null;
        for (Map.Entry<ThreadContext<Object>, Object> context : contexts.entrySet()) {
            try {
                context.getKey().reset(context.getValue());
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

    private static <T extends ExecutorService> T lookup(final String jndiName) {
        final Boolean performLookup = JNDI_LOOKUPS.get(jndiName);
        if (performLookup != null && performLookup) {
            try {
                // This could have some performance impact. However, we can't assume the context we're in, so we need
                // to look it up each time.
                return InitialContext.doLookup(jndiName);
            } catch (NamingException ignore) {
            } catch (Exception e) {
                LogMessages.LOGGER.failedToLookupManagedExecutorService(e, jndiName);
            }
        } else if (performLookup == null) {
            // Do one lookup and if not found assume it won't be in the future
            try {
                final T service = InitialContext.doLookup(jndiName);
                JNDI_LOOKUPS.put(jndiName, Boolean.TRUE);
                return service;
            } catch (NamingException ignore) {
            } catch (Exception e) {
                LogMessages.LOGGER.failedToLookupManagedExecutorService(e, jndiName);
            }
            JNDI_LOOKUPS.put(jndiName, Boolean.FALSE);
        }
        return null;
    }

    private static class ContextualExecutor implements Executor {
        private final Executor delegate;

        private ContextualExecutor(final Executor delegate) {
            this.delegate = delegate;
        }

        @Override
        public void execute(final Runnable command) {
            delegate.execute(runnable(command));
        }
    }

}
