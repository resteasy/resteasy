/**
 * Copyright 2015-2017 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.resteasy.microprofile.client.async;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * TODO: replace with a proper RestEasy solution to trigger code within the async thread when it's implemented
 */
public class ExecutorServiceWrapper implements ExecutorService {
    private final ExecutorService delegate;
    private final Decorator decorator;

    public ExecutorServiceWrapper(final ExecutorService delegate, final Decorator decorator) {
        this.delegate = delegate;
        this.decorator = decorator;
    }

    @Override
    public void shutdown() {
        delegate.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return delegate.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return delegate.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return delegate.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return delegate.awaitTermination(timeout, unit);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return delegate.submit(decorate(task));
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return delegate.submit(decorate(task), result);
    }

    @Override
    public Future<?> submit(Runnable task) {
        return delegate.submit(decorate(task));
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return delegate.invokeAll(decorate(tasks));
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return delegate.invokeAll(decorate(tasks), timeout, unit);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return delegate.invokeAny(decorate(tasks));
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return delegate.invokeAny(decorate(tasks), timeout, unit);
    }

    private <T> Collection<Callable<T>> decorate(Collection<? extends Callable<T>> tasks) {
        return tasks.stream()
                .map(this::decorate)
                .collect(Collectors.toList());
    }

    @Override
    public void execute(Runnable command) {
        delegate.execute(decorate(command));
    }

    private Runnable decorate(Runnable command) {
        return decorator.decorate(command);
    }

    private <T> Callable<T> decorate(Callable<T> command) {
        return decorator.decorate(command);
    }

    interface Decorator {
        Runnable decorate(Runnable runnable);

        <T> Callable<T> decorate(Callable<T> runnable);
    }
}
