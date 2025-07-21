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
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class ThreadContexts {

    private final List<ThreadContext<Object>> contexts;

    /**
     * Creates a new collection instance.
     */
    public ThreadContexts() {
        contexts = createContexts();
    }

    /**
     * Adds the context to the current collection.
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
     * sorted by {@linkplain jakarta.annotation.Priority priority}.
     *
     * @return an immutable collection of the curren thread contexts
     */
    public Collection<ThreadContext<Object>> getThreadContexts() {
        final List<ThreadContext<Object>> contexts;
        synchronized (this.contexts) {
            contexts = new ArrayList<>(this.contexts);
        }
        contexts.sort(new PriorityComparator<>());
        return Collections.unmodifiableList(contexts);
    }

    /**
     * Clears the current contexts.
     *
     * @return this instance
     */
    public ThreadContexts clear() {
        synchronized (contexts) {
            contexts.clear();
        }
        return this;
    }

    private static List<ThreadContext<Object>> createContexts() {
        final List<ThreadContext<Object>> contexts = new ArrayList<>();
        ServiceLoader.load(ThreadContext.class).forEach(contexts::add);
        return contexts;
    }
}
