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
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.spi.ResteasyDeployment;
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
     * Creates a new {@link ContextualExecutorService} or wraps the default {@code ManagedExecutorService} in a
     * Jakarta EE environment.
     * <p>
     * If executed in a Jakarta EE container which includes a default {@code ManagedExecutorService}, that executor
     * is wrapped and said to be managed. If the default executor service cannot be found or if not being executed in a
     * Jakarta EE container a new {@linkplain Executors#newCachedThreadPool() cached thread pool} will be wrapped.
     * </p>
     * <p>
     * In a Jakarta EE container the JNDI lookup name can be overridden with the {@code resteasy.async.executor.service.jndi}
     * configuration property. By default, the JNDI lookup name is {@code java:comp/DefaultManagedExecutorService}.
     * </p>
     *
     * @return a new contextual executor
     */
    public static ContextualExecutorService threadPool() {
        return threadPool(true);
    }

    /**
     * Creates a new {@link ContextualExecutorService} or wraps the default {@code ManagedExecutorService} in a
     * Jakarta EE environment.
     * <p>
     * If executed in a Jakarta EE container which includes a default {@code ManagedExecutorService}, that executor
     * is wrapped and said to be managed. If the default executor service cannot be found or if not being executed in a
     * Jakarta EE container a new {@linkplain Executors#newCachedThreadPool() cached thread pool} will be wrapped.
     * </p>
     * <p>
     * In a Jakarta EE container the JNDI lookup name can be overridden with the {@code resteasy.async.executor.service.jndi}
     * configuration property. By default, the JNDI lookup name is {@code java:comp/DefaultManagedExecutorService}.
     * </p>
     *
     * @param useDeployment {@code true} to use the deployment's executor if available, {@code false} to always create
     *                      a new unmanaged executor
     *
     * @return a new contextual executor
     */
    public static ContextualExecutorService threadPool(boolean useDeployment) {
        return threadPool(useDeployment, ResteasyProviderFactory.getInstance());
    }

    /**
     * Creates a new {@link ContextualExecutorService} or wraps the default {@code ManagedExecutorService} in a
     * Jakarta EE environment. The thread contexts are captured for each task.
     *
     * @param useDeployment   {@code true} to use the deployment's executor if available, {@code false} to always
     *                        create a new unmanaged executor
     * @param providerFactory the {@link ResteasyProviderFactory} to locate the {@linkplain ResteasyDeployment deployment}
     *                        and the {@link ThreadContexts}
     *
     * @return a new contextual executor
     *
     * @see #threadPool(boolean)
     */
    public static ContextualExecutorService threadPool(final boolean useDeployment,
            final ResteasyProviderFactory providerFactory) {
        ExecutorService delegate = lookup(EXECUTOR_SERVICE_JNDI);
        final ThreadContexts threadContexts = providerFactory.getThreadContexts();
        if (delegate == null) {
            if (useDeployment) {
                final ResteasyDeployment deployment = providerFactory.getContextData(ResteasyDeployment.class);
                if (deployment != null) {
                    // Deployment executors are managed by the deployment, not by us
                    delegate = deployment.getExecutorService();
                } else {
                    return newThreadPool(threadContexts);
                }
            } else {
                return newThreadPool(threadContexts);
            }
        }
        return wrap(delegate, true, threadContexts);
    }

    /**
     * Creates a new {@link ContextualExecutorService} based on a {@link ThreadPoolExecutor}. The pool size can be
     * overridden with the {@code dev.resteasy.concurrent.core.pool.size} configuration property.
     *
     * @return a new contextual executor
     */
    public static ContextualExecutorService newThreadPool() {
        return newThreadPool(currentThreadContexts());
    }

    /**
     * Creates a new {@link ContextualExecutorService} based on a {@link ThreadPoolExecutor}. The pool size can be
     * overridden with the {@code dev.resteasy.concurrent.core.pool.size} configuration property.
     *
     * @param threadContexts the thread contexts to capture for each task
     *
     * @return a new contextual executor
     *
     * @see #newThreadPool()
     */
    public static ContextualExecutorService newThreadPool(final ThreadContexts threadContexts) {
        final int poolSize = CoreThreads.getCoreThreads("dev.resteasy.concurrent.core.pool.size");
        final ExecutorService delegate = new ThreadPoolExecutor(poolSize, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(), new ContextualThreadFactory("contextual-pool"));
        return new ContextualExecutorService(delegate, false, threadContexts);
    }

    /**
     * Creates a new {@link ContextualScheduledExecutorService} or wraps the default {@code ManagedScheduledExecutorService}
     * in a Jakarta EE environment.
     * <p>
     * If executed in a Jakarta EE container which includes a default {@code ManagedScheduledExecutorService}, that executor
     * is wrapped and said to be managed. If the default executor service cannot be found or if not being executed in a
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
        return scheduledThreadPool(true);
    }

    /**
     * Creates a new {@link ContextualScheduledExecutorService} or wraps the default {@code ManagedScheduledExecutorService}
     * in a Jakarta EE environment.
     * <p>
     * If executed in a Jakarta EE container which includes a default {@code ManagedScheduledExecutorService}, that executor
     * is wrapped and said to be managed. If the default executor service cannot be found or if not being executed in a
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
     * @param useDeployment {@code true} to use the deployment's executor if available, {@code false} to always create
     *                      a new unmanaged executor
     *
     * @return a new contextual executor
     */
    public static ContextualScheduledExecutorService scheduledThreadPool(boolean useDeployment) {
        return scheduledThreadPool(useDeployment, ResteasyProviderFactory.getInstance());
    }

    /**
     * Creates a new {@link ContextualScheduledExecutorService} or wraps the default
     * {@code ManagedScheduledExecutorService} in a Jakarta EE environment. The thread contexts are captured for each
     * task.
     *
     * @param useDeployment   {@code true} to use the deployment's executor if available, {@code false} to always
     *                        create a new unmanaged executor
     * @param providerFactory the {@link ResteasyProviderFactory} to locate the {@linkplain ResteasyDeployment deployment}
     *                        and the {@link ThreadContexts}
     *
     * @return a new contextual executor
     *
     * @see #scheduledThreadPool(boolean)
     */
    public static ContextualScheduledExecutorService scheduledThreadPool(final boolean useDeployment,
            final ResteasyProviderFactory providerFactory) {
        ScheduledExecutorService delegate = lookup(SCHEDULED_EXECUTOR_SERVICE_JNDI);
        final ThreadContexts threadContexts = providerFactory.getThreadContexts();
        if (delegate == null) {
            if (useDeployment) {
                final ResteasyDeployment deployment = providerFactory.getContextData(ResteasyDeployment.class);
                if (deployment != null) {
                    // Deployment executors are managed by the deployment, not by us
                    delegate = deployment.getScheduledExecutorService();
                } else {
                    return newScheduledThreadPool(threadContexts);
                }
            } else {
                return newScheduledThreadPool(threadContexts);
            }
        }
        return wrap(delegate, true, threadContexts);
    }

    /**
     * Creates a new {@link ContextualScheduledExecutorService} or wraps the default {@code ManagedScheduledExecutorService}
     * in a Jakarta EE environment.
     * <p>
     * If executed in a Jakarta EE container which includes a default {@code ManagedScheduledExecutorService}, that executor
     * is wrapped and said to be managed. If the default executor service cannot be found or if not being executed in a
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
        return scheduledThreadPool(poolSize, threadFactory, currentThreadContexts());
    }

    /**
     * Creates a new {@link ContextualScheduledExecutorService} or wraps the default
     * {@code ManagedScheduledExecutorService} in a Jakarta EE environment. The thread contexts are captured for each
     * task.
     *
     * @param poolSize       the size of the pool to create, ignored in a Jakarta EE container
     * @param threadFactory  the thread factory to use if a new executor is created, ignored in a Jakarta EE
     *                       container
     * @param threadContexts the thread contexts to capture for each task
     *
     * @return a new contextual executor
     *
     * @see #scheduledThreadPool(int, ThreadFactory)
     */
    public static ContextualScheduledExecutorService scheduledThreadPool(final int poolSize,
            final ThreadFactory threadFactory, final ThreadContexts threadContexts) {
        ScheduledExecutorService delegate = lookup(SCHEDULED_EXECUTOR_SERVICE_JNDI);
        boolean managed = true;
        if (delegate == null) {
            delegate = Executors.newScheduledThreadPool(poolSize, threadFactory);
            managed = false;
        }
        return wrap(delegate, managed, threadContexts);
    }

    /**
     * Creates a new {@link ContextualScheduledExecutorService} based on a {@link ScheduledThreadPoolExecutor}. The
     * pool size can be overridden with the {@code resteasy.async.timeout.scheduler.min.pool.size} configuration property.
     *
     * @return a new contextual executor
     */
    public static ContextualScheduledExecutorService newScheduledThreadPool() {
        return newScheduledThreadPool(currentThreadContexts());
    }

    /**
     * Creates a new {@link ContextualScheduledExecutorService} based on a {@link ScheduledThreadPoolExecutor}. The
     * pool size can be overridden with the {@code resteasy.async.timeout.scheduler.min.pool.size} configuration
     * property.
     *
     * @param threadContexts the thread contexts to capture for each task
     *
     * @return a new contextual executor
     *
     * @see #newScheduledThreadPool()
     */
    public static ContextualScheduledExecutorService newScheduledThreadPool(final ThreadContexts threadContexts) {
        final int poolSize = CoreThreads.getCoreThreads("resteasy.async.timeout.scheduler.min.pool.size");
        final ScheduledThreadPoolExecutor delegate = new ScheduledThreadPoolExecutor(poolSize,
                new ContextualThreadFactory("contextual-scheduled-pool"));
        // Cancelled timeout watchdogs must not sit in the queue until their delay elapses.
        delegate.setRemoveOnCancelPolicy(true);
        return new ContextualScheduledExecutorService(delegate, false, threadContexts);
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
        if (delegate instanceof ContextualExecutorService current) {
            if (current.isManaged() == managed) {
                return current;
            }
            return new ContextualExecutorService(current, managed);
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
        if (delegate instanceof ContextualScheduledExecutorService current) {
            if (current.isManaged() == managed) {
                return current;
            }
            return new ContextualScheduledExecutorService(current, managed);
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
     * Wraps the executor service in a contextual executor which captures the given thread contexts. If the delegate
     * is already a contextual executor, its underlying delegate is wrapped and the given thread contexts replace its
     * existing ones.
     * <p>
     * The context is copied in before each invocation of the delegate, then reset after the thread is done executing.
     * </p>
     *
     * @param delegate       the executor to wrap
     * @param managed        {@code true} if the lifecycle is managed by another process
     * @param threadContexts the thread contexts to capture for each task
     *
     * @return a new contextual executor or {@code null} if the delegate was {@code null}
     */
    public static ContextualExecutorService wrap(final ExecutorService delegate, final boolean managed,
            final ThreadContexts threadContexts) {
        if (delegate == null) {
            return null;
        }
        if (delegate instanceof ScheduledExecutorService) {
            return wrap(((ScheduledExecutorService) delegate), managed, threadContexts);
        }
        if (delegate instanceof final ContextualExecutorService current) {
            return new ContextualExecutorService(current, managed, threadContexts);
        }
        return new ContextualExecutorService(delegate, managed, threadContexts);
    }

    /**
     * Wraps the executor service in a contextual executor which captures the given thread contexts. If the delegate
     * is already a contextual executor, its underlying delegate is wrapped and the given thread contexts replace its
     * existing ones.
     * <p>
     * The context is copied in before each invocation of the delegate, then reset after the thread is done executing.
     * </p>
     *
     * @param delegate       the executor to wrap
     * @param managed        {@code true} if the lifecycle is managed by another process
     * @param threadContexts the thread contexts to capture for each task
     *
     * @return a new contextual executor or {@code null} if the delegate was {@code null}
     */
    public static ContextualScheduledExecutorService wrap(final ScheduledExecutorService delegate,
            final boolean managed, final ThreadContexts threadContexts) {
        if (delegate == null) {
            return null;
        }
        if (delegate instanceof final ContextualScheduledExecutorService current) {
            return new ContextualScheduledExecutorService(current, managed, threadContexts);
        }
        return new ContextualScheduledExecutorService(delegate, managed, threadContexts);
    }

    /**
     * If the delegate is not {@code null}, it is wrapped. If the delegate is {@code null} and this is a Jakarta EE
     * environment an attempt will be made to look up the default {@code ManagedScheduledExecutorService}. If found the
     * default {@code ManagedScheduledExecutorService} will be wrapped. Otherwise, {@code null} will be returned.
     *
     * @param delegate       the delegate to wrap, which may be {@code null}
     * @param threadContexts the thread contexts to capture for each task
     *
     * @return a wrapped contextual executor or {@code null} if no executor was provided or could be found
     *
     * @see #wrap(ScheduledExecutorService, boolean, ThreadContexts)
     */
    public static ContextualScheduledExecutorService wrapOrLookup(final ScheduledExecutorService delegate,
            final ThreadContexts threadContexts) {
        if (delegate != null) {
            return wrap(delegate, true, threadContexts);
        }
        ScheduledExecutorService found = lookup(SCHEDULED_EXECUTOR_SERVICE_JNDI);
        return found == null ? null : wrap(found, true, threadContexts);
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
        return callable(getContexts(), task);
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

    /**
     * Creates a new {@linkplain Runnable runnable} which sets up the context captured from the thread contexts
     * before the runnable is executed. Finally, resetting the context.
     *
     * @param threadContexts the thread contexts to capture for each task
     * @param task           the runnable to wrap
     *
     * @return a new contextual runnable
     */
    static Runnable runnable(final ThreadContexts threadContexts, final Runnable task) {
        return runnable(getContexts(threadContexts), task);
    }

    /**
     * Creates a new {@linkplain Callable callable} which sets up the context captured from the thread contexts
     * before the callable is executed. Finally, resetting the context.
     *
     * @param threadContexts the thread contexts to capture for each task
     * @param task           the callable to wrap
     * @param <V>            the return type
     *
     * @return a new contextual callable
     */
    static <V> Callable<V> callable(final ThreadContexts threadContexts, final Callable<V> task) {
        return callable(getContexts(threadContexts), task);
    }

    /**
     * Creates a new collection of {@linkplain Callable callables} which sets up the context captured from the thread
     * contexts before each callable is executed. Finally, resetting the context.
     *
     * @param threadContexts the thread contexts to capture for each task
     * @param tasks          the callables to wrap
     * @param <T>            the return type of the callable
     *
     * @return a collection of new contextual callables
     */
    static <T> Collection<? extends Callable<T>> callable(final ThreadContexts threadContexts,
            final Collection<? extends Callable<T>> tasks) {
        return tasks.stream()
                .map((Function<Callable<T>, Callable<T>>) (task) -> callable(threadContexts, task))
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

    private static <V> Callable<V> callable(final Map<ThreadContext<Object>, Object> contexts, final Callable<V> task) {
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

    private static Map<ThreadContext<Object>, Object> getContexts() {
        return getContexts(currentThreadContexts());
    }

    private static Map<ThreadContext<Object>, Object> getContexts(final ThreadContexts threadContexts) {
        final Map<ThreadContext<Object>, Object> contexts = new LinkedHashMap<>();
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

    private static ThreadContexts currentThreadContexts() {
        // Resolve the contexts once as the current provider factory may be a delegate for another factory
        return ResteasyProviderFactory.getInstance().getThreadContexts();
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

}
