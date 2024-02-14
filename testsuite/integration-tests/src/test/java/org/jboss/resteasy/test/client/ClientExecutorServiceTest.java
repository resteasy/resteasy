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

package org.jboss.resteasy.test.client;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.test.client.resource.TestResource;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Tests using a custom {@link java.util.concurrent.ExecutorService} in the client.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ClientExecutorServiceTest {

    @Deployment
    public static Archive<?> deployment() {
        return TestUtil.finishContainerPrepare(
                TestUtil.prepareArchive(ClientExecutorServiceTest.class.getSimpleName()),
                Collections.emptyMap(),
                TestResource.class);
    }

    @ArquillianResource
    private URL url;

    private TestExecutorService executor;

    @BeforeEach
    public void setup() {
        executor = new TestExecutorService(Executors.newScheduledThreadPool(2));
    }

    @AfterEach
    public void shutdown() {
        if (executor != null) {
            executor.delegate.shutdownNow();
        }
    }

    @Test
    public void overrideExecutorService() throws Exception {
        final Client client = ClientBuilder.newBuilder()
                .executorService(executor)
                .build();
        testGet(client);
    }

    @Test
    public void overrideScheduledExecutorService() throws Exception {
        final Client client = ClientBuilder.newBuilder()
                .scheduledExecutorService(executor)
                .build();
        testGet(client);
    }

    private void testGet(final Client client) throws Exception {
        final String result = client.target(TestUtil.generateUri(url, "/get"))
                .request()
                .async()
                .get(String.class)
                .get(5, TimeUnit.SECONDS);
        Assertions.assertEquals("get", result);
        try {
            client.close();
        } catch (ShutdownNotAllowedException e) {
            try (
                    StringWriter writer = new StringWriter();
                    PrintWriter pw = new PrintWriter(writer)) {
                pw.println("The client should not be shutting down the executor.");
                e.printStackTrace(pw);
                Assertions.fail(writer.toString());
            }
        }
        Assertions.assertFalse(executor.isShutdown(), "The executor should not be shutdown");
    }

    private static class TestExecutorService implements ScheduledExecutorService {
        private final ScheduledExecutorService delegate;

        private TestExecutorService(final ScheduledExecutorService delegate) {
            this.delegate = delegate;
        }

        @Override
        public ScheduledFuture<?> schedule(final Runnable command, final long delay,
                final TimeUnit unit) {
            return delegate.schedule(command, delay, unit);
        }

        @Override
        public <V> ScheduledFuture<V> schedule(final Callable<V> callable, final long delay,
                final TimeUnit unit) {
            return delegate.schedule(callable, delay, unit);
        }

        @Override
        public ScheduledFuture<?> scheduleAtFixedRate(final Runnable command, final long initialDelay,
                final long period,
                final TimeUnit unit) {
            return delegate.scheduleAtFixedRate(command, initialDelay, period, unit);
        }

        @Override
        public ScheduledFuture<?> scheduleWithFixedDelay(final Runnable command, final long initialDelay,
                final long delay,
                final TimeUnit unit) {
            return delegate.scheduleWithFixedDelay(command, initialDelay, delay, unit);
        }

        @Override
        public void shutdown() {
            throw new ShutdownNotAllowedException();
        }

        @Override
        public List<Runnable> shutdownNow() {
            throw new ShutdownNotAllowedException();
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
        public boolean awaitTermination(final long timeout, final TimeUnit unit) throws InterruptedException {
            return delegate.awaitTermination(timeout, unit);
        }

        @Override
        public <T> Future<T> submit(final Callable<T> task) {
            return delegate.submit(task);
        }

        @Override
        public <T> Future<T> submit(final Runnable task, final T result) {
            return delegate.submit(task, result);
        }

        @Override
        public Future<?> submit(final Runnable task) {
            return delegate.submit(task);
        }

        @Override
        public <T> List<Future<T>> invokeAll(
                final Collection<? extends Callable<T>> tasks) throws InterruptedException {
            return delegate.invokeAll(tasks);
        }

        @Override
        public <T> List<Future<T>> invokeAll(
                final Collection<? extends Callable<T>> tasks, final long timeout,
                final TimeUnit unit) throws InterruptedException {
            return delegate.invokeAll(tasks, timeout, unit);
        }

        @Override
        public <T> T invokeAny(final Collection<? extends Callable<T>> tasks)
                throws InterruptedException, ExecutionException {
            return delegate.invokeAny(tasks);
        }

        @Override
        public <T> T invokeAny(final Collection<? extends Callable<T>> tasks, final long timeout,
                final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            return delegate.invokeAny(tasks, timeout, unit);
        }

        @Override
        public void execute(final Runnable command) {
            delegate.execute(command);
        }
    }

    private static class ShutdownNotAllowedException extends IllegalStateException {
        private ShutdownNotAllowedException() {
            super("Not allowed to shutdown this service!");
        }
    }
}
