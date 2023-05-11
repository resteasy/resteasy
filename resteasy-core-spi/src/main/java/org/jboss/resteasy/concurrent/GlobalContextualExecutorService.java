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

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
class GlobalContextualExecutorService extends ContextualExecutorService implements AutoCloseable {
    static final GlobalContextualExecutorService INSTANCE = new GlobalContextualExecutorService();
    private final Thread shutdownHook;

    private volatile ExecutorService delegate;

    private GlobalContextualExecutorService() {
        super(null, true);
        shutdownHook = new Thread("resteasy-shutdown") {
            @Override
            public void run() {
                synchronized (GlobalContextualExecutorService.this) {
                    if (delegate != null) {
                        delegate.shutdown();
                        delegate = null;
                    }
                }
            }
        };
    }

    @Override
    public void shutdown() {
        // Do nothing as we will shut it down later
    }

    @Override
    public List<Runnable> shutdownNow() {
        return Collections.emptyList();
    }

    @Override
    public boolean isShutdown() {
        return false;
    }

    @Override
    public boolean isTerminated() {
        return false;
    }

    @Override
    ExecutorService getDelegate() {
        if (delegate == null) {
            synchronized (this) {
                if (delegate == null) {
                    final int poolSize = SecurityActions.getCoreThreads("dev.resteasy.concurrent.core.pool.size");
                    delegate = new ThreadPoolExecutor(poolSize, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS,
                            new LinkedBlockingQueue<>(), new ContextualThreadFactory("contextual-pool"));
                    SecurityActions.registerShutdownHook(shutdownHook);
                }
            }
        }
        return delegate;
    }

    @Override
    public void close() {
        synchronized (this) {
            SecurityActions.removeShutdownHook(shutdownHook);
            if (delegate != null) {
                delegate.shutdown();
                delegate = null;
            }
        }
    }
}
