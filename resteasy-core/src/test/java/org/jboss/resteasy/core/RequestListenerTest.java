/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.container.AsyncResponse;
import jakarta.ws.rs.container.Suspended;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.mock.MockDispatcherFactory;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.spi.Dispatcher;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.RequestListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests that {@link RequestListener#requestCompleted(HttpRequest, HttpResponse)} is invoked exactly once for each
 * request.
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
public class RequestListenerTest {
    // The listener is registered for every test in this module, only requests with this attribute are tracked
    private static final String TRACKED_ATTRIBUTE = RequestListenerTest.class.getName() + ".tracked";
    private static final AtomicInteger COMPLETED = new AtomicInteger();
    private static final AtomicInteger COMPLETED_BEFORE_RETURN = new AtomicInteger();
    private static volatile CountDownLatch completedLatch;

    private Dispatcher dispatcher;

    @BeforeEach
    public void setup() {
        COMPLETED.set(0);
        COMPLETED_BEFORE_RETURN.set(-1);
        completedLatch = new CountDownLatch(1);
        dispatcher = MockDispatcherFactory.createDispatcher();
        dispatcher.getRegistry().addPerRequestResource(SyncResource.class);
        dispatcher.getRegistry().addPerRequestResource(ExceptionResource.class);
        dispatcher.getRegistry().addPerRequestResource(LocatorResource.class);
        dispatcher.getRegistry().addPerRequestResource(AsyncResource.class);
    }

    @Test
    public void syncRequest() throws Exception {
        final MockHttpResponse response = invoke("/sync", false);
        assertEquals(200, response.getStatus());
        assertCompletedOnce();
    }

    @Test
    public void webApplicationException() throws Exception {
        final MockHttpResponse response = invoke("/exception", false);
        assertEquals(409, response.getStatus());
        assertCompletedOnce();
    }

    @Test
    public void notFound() throws Exception {
        final MockHttpResponse response = invoke("/missing", false);
        assertEquals(404, response.getStatus());
        assertCompletedOnce();
    }

    @Test
    public void subResourceLocator() throws Exception {
        final MockHttpResponse response = invoke("/locator/sub", false);
        assertEquals(200, response.getStatus());
        assertCompletedOnce();
    }

    @Test
    public void asyncResumeOnOtherThread() throws Exception {
        final MockHttpResponse response = invoke("/async/thread", true);
        awaitCompleted();
        assertEquals(200, response.getStatus());
        assertCompletedOnce();
    }

    @Test
    public void asyncResumeOnInvokingThread() throws Exception {
        final MockHttpResponse response = invoke("/async/invoking", true);
        awaitCompleted();
        assertEquals(200, response.getStatus());
        assertEquals(0, COMPLETED_BEFORE_RETURN.get(), "The request was completed before the resource method returned");
        assertCompletedOnce();
    }

    @Test
    public void asyncTimeout() throws Exception {
        final MockHttpResponse response = invoke("/async/timeout", true);
        awaitCompleted();
        assertEquals(503, response.getStatus());
        assertCompletedOnce();
    }

    @Test
    public void completionStage() throws Exception {
        final MockHttpResponse response = invoke("/async/stage", true);
        awaitCompleted();
        assertEquals(200, response.getStatus());
        assertCompletedOnce();
    }

    private MockHttpResponse invoke(final String uri, final boolean async) throws Exception {
        final MockHttpRequest request = MockHttpRequest.get(uri);
        request.setAttribute(TRACKED_ATTRIBUTE, Boolean.TRUE);
        final MockHttpResponse response = new MockHttpResponse();
        if (async) {
            request.setAsynchronousContext(
                    new SynchronousExecutionContext((SynchronousDispatcher) dispatcher, request, response));
        }
        dispatcher.invoke(request, response);
        return response;
    }

    private static void awaitCompleted() throws InterruptedException {
        // Asynchronous completion may complete the request after the dispatcher returns
        assertTrue(completedLatch.await(5, TimeUnit.SECONDS), "The request was not completed");
    }

    private static void assertCompletedOnce() {
        assertEquals(1, COMPLETED.get(), "Expected requestCompleted to be invoked exactly once");
    }

    /**
     * Registered in {@code META-INF/services/org.jboss.resteasy.spi.RequestListener}.
     */
    public static class TrackingRequestListener implements RequestListener {

        @Override
        public void requestCompleted(final HttpRequest request, final HttpResponse response) {
            if (request.getAttribute(TRACKED_ATTRIBUTE) != null) {
                COMPLETED.incrementAndGet();
                completedLatch.countDown();
            }
        }
    }

    @Path("/sync")
    public static class SyncResource {
        @GET
        public String get() {
            return "sync";
        }
    }

    @Path("/exception")
    public static class ExceptionResource {
        @GET
        public String get() {
            throw new WebApplicationException(Response.Status.CONFLICT);
        }
    }

    @Path("/locator")
    public static class LocatorResource {
        @Path("sub")
        public SubResource sub() {
            return new SubResource();
        }
    }

    public static class SubResource {
        @GET
        public String get() {
            return "sub";
        }
    }

    @Path("/async")
    public static class AsyncResource {
        @GET
        @Path("thread")
        public void resumeOnOtherThread(@Suspended final AsyncResponse asyncResponse) {
            new Thread(() -> asyncResponse.resume("thread")).start();
        }

        @GET
        @Path("invoking")
        public void resumeOnInvokingThread(@Suspended final AsyncResponse asyncResponse) {
            asyncResponse.resume("invoking");
            COMPLETED_BEFORE_RETURN.set(COMPLETED.get());
        }

        @GET
        @Path("timeout")
        public void timeout(@Suspended final AsyncResponse asyncResponse) {
            asyncResponse.setTimeout(100, TimeUnit.MILLISECONDS);
        }

        @GET
        @Path("stage")
        public CompletionStage<String> stage() {
            return CompletableFuture.supplyAsync(() -> "stage");
        }
    }
}
