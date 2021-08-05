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

/**
 * A utility used to {@linkplain #capture() capture} the current context and {@linkplain #push(Object) set it} on a
 * thread before it's execute. Finally {@linkplain #reset() resetting} the context.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 * @see org.jboss.resteasy.concurrent.ContextualExecutorService
 * @see org.jboss.resteasy.concurrent.ContextualScheduledExecutorService
 * @since 5.0.0
 */
public interface ThreadContext<T> {

    /**
     * Captures the current context to be passed to {@link #push(Object)} before a thread executes.
     *
     * @return the current context
     */
    T capture();

    /**
     * Pushes the context previously captured to the currently running thread.
     *
     * @param context the context to push
     */
    void push(T context);

    /**
     * Resets the context on the current thread.
     */
    void reset();
}
