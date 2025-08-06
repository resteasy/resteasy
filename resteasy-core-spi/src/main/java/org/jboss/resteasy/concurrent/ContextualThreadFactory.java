/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2023 Red Hat, Inc., and individual contributors
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

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Creates contextual threads that are always daemon threads.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
class ContextualThreadFactory implements ThreadFactory {
    private static final AtomicInteger POOL_COUNTER = new AtomicInteger(0);
    private final AtomicInteger threadCounter = new AtomicInteger(0);
    private final String prefix;

    ContextualThreadFactory(final String prefix) {
        this.prefix = String.format("%s-%d-thread", prefix, POOL_COUNTER.incrementAndGet());
    }

    @Override
    public Thread newThread(final Runnable r) {
        final Thread thread = new Thread(r, String.format("%s-%d", prefix, threadCounter.incrementAndGet()));
        thread.setDaemon(true);
        if (thread.getPriority() != Thread.NORM_PRIORITY) {
            thread.setPriority(Thread.NORM_PRIORITY);
        }
        return thread;
    }
}
