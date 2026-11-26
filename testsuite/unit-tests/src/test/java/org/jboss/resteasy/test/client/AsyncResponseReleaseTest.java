/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.resteasy.test.client;

import java.net.URI;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import dev.resteasy.junit.extension.annotations.RequestPath;
import dev.resteasy.junit.extension.annotations.RestBootstrap;
import dev.resteasy.junit.extension.annotations.RestResource;

/**
 * Tests that an asynchronous invocation releases the response body when the response is not processed.
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
@RestBootstrap(AsyncResponseReleaseTest.StreamResource.class)
class AsyncResponseReleaseTest {

    /**
     * Checks that the response body is closed when the client executor rejects the task that processes the response.
     * <p>
     * The resource writes more data than the socket buffers hold. If the client does not close the body, the write on
     * the server blocks until the client is closed.
     * </p>
     */
    @Test
    void rejectedExtractionClosesBody(@RestResource @RequestPath("stream") final URI uri) throws Exception {
        final RejectAfterFirstExecutor executor = new RejectAfterFirstExecutor();
        try (Client client = ClientBuilder.newBuilder().executorService(executor).build()) {
            final Future<Response> future = client.target(uri).request().async().get();
            final ExecutionException error = Assertions.assertThrows(ExecutionException.class,
                    () -> future.get(10, TimeUnit.SECONDS));
            Assertions.assertInstanceOf(RejectedExecutionException.class, error.getCause());
            Assertions.assertTrue(StreamResource.WRITE_ENDED.await(10, TimeUnit.SECONDS),
                    "The response body was not closed, so the server is still writing it");
        } finally {
            executor.shutdownNow();
        }
    }

    @Path("/stream")
    public static class StreamResource {
        static final CountDownLatch WRITE_ENDED = new CountDownLatch(1);
        private static final int CHUNK_SIZE = 64 * 1024;
        private static final int CHUNKS = 1024;

        @GET
        @Produces(MediaType.APPLICATION_OCTET_STREAM)
        public StreamingOutput stream() {
            return output -> {
                try {
                    final byte[] chunk = new byte[CHUNK_SIZE];
                    for (int i = 0; i < CHUNKS; i++) {
                        output.write(chunk);
                        output.flush();
                    }
                } finally {
                    WRITE_ENDED.countDown();
                }
            };
        }
    }

    /**
     * Runs the first task and rejects every task after it. For an asynchronous invocation, the first task runs the
     * request filters and the second processes the response.
     */
    private static class RejectAfterFirstExecutor extends AbstractExecutorService {
        private final ExecutorService delegate = Executors.newSingleThreadExecutor();
        private final AtomicInteger submitted = new AtomicInteger();

        @Override
        public void execute(final Runnable command) {
            if (submitted.incrementAndGet() > 1) {
                throw new RejectedExecutionException("Only the first task is accepted");
            }
            delegate.execute(command);
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
        public boolean awaitTermination(final long timeout, final TimeUnit unit) throws InterruptedException {
            return delegate.awaitTermination(timeout, unit);
        }
    }
}
