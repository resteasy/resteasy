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

package org.jboss.resteasy.spi.concurrent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;

import org.jboss.resteasy.spi.PriorityComparator;

/**
 * Represents a collection of {@link ThreadContext}'s.
 * <p>
 * The {@link ThreadContext} services are located once, when this collection is created, using the current thread's
 * context class loader. A new instance of each service is created each time the {@linkplain #getThreadContexts()
 * contexts are requested}. Contexts {@linkplain #add(ThreadContext) added} to this collection are shared.
 * </p>
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class ThreadContexts {

    @SuppressWarnings("rawtypes")
    private final List<ServiceLoader.Provider<ThreadContext>> serviceProviders;
    private final List<ThreadContext<Object>> contexts;

    /**
     * Creates a new collection instance and locates the {@link ThreadContext} services.
     */
    public ThreadContexts() {
        serviceProviders = ServiceLoader.load(ThreadContext.class)
                .stream()
                .toList();
        contexts = new ArrayList<>();
    }

    /**
     * Creates a new collection instance with the same {@link ThreadContext} services as the parent and a copy of the
     * contexts {@linkplain #add(ThreadContext) added} to the parent. The services are not located again.
     *
     * @param parent the thread contexts to copy
     */
    public ThreadContexts(final ThreadContexts parent) {
        serviceProviders = parent.serviceProviders;
        synchronized (parent.contexts) {
            contexts = new ArrayList<>(parent.contexts);
        }
    }

    /**
     * Adds the context to the current collection. The same instance is used for each {@linkplain #getThreadContexts()
     * request} for the contexts.
     *
     * @param threadContext the thread context to add
     *
     * @return this instance
     */
    @SuppressWarnings("unchecked")
    public ThreadContexts add(final ThreadContext<?> threadContext) {
        synchronized (contexts) {
            if (!contexts.contains(threadContext)) {
                contexts.add((ThreadContext<Object>) threadContext);
            }
        }
        return this;
    }

    /**
     * Returns a collection of the current thread contexts. This is a snapshot of what is currently in the collection
     * sorted by {@linkplain jakarta.annotation.Priority priority}. Contexts located as services are new instances on
     * each invocation. Contexts {@linkplain #add(ThreadContext) added} to this collection are returned as is.
     *
     * @return an immutable collection of the current thread contexts
     */
    @SuppressWarnings("unchecked")
    public Collection<ThreadContext<Object>> getThreadContexts() {
        final List<ThreadContext<Object>> contexts = new ArrayList<>();
        for (var provider : serviceProviders) {
            contexts.add((ThreadContext<Object>) provider.get());
        }
        synchronized (this.contexts) {
            contexts.addAll(this.contexts);
        }
        contexts.sort(new PriorityComparator<>());
        return Collections.unmodifiableList(contexts);
    }

    /**
     * This is a no-op. The contexts may be shared and cannot be cleared by one consumer.
     *
     * @return this instance
     * @deprecated the contexts may be shared and cannot be cleared
     */
    @Deprecated(forRemoval = true, since = "8.0")
    public ThreadContexts clear() {
        return this;
    }
}
