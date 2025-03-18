/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2024 Red Hat, Inc., and individual contributors
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

package org.jboss.resteasy.test.providers.sse;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.ServiceUnavailableException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.sse.SseEventSource;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.plugins.providers.sse.client.SseEventSourceImpl;
import org.jboss.resteasy.test.providers.sse.resource.SseCallbackResource;
import org.jboss.resteasy.utils.TestApplication;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests the conditions for callbacks defined in {@link SseEventSource}.
 * <p>
 * Note that some of these tests are similar to tests in {@link SseTest}. However, a bit of a different approach is
 * taken. It makes sense to keep both.
 * </p>
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@ArquillianTest
@RunAsClient
public class SseCallbackTest {

    @ArquillianResource
    private URI uri;

    @Deployment
    public static WebArchive deployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addClasses(TestApplication.class,
                        SseCallbackResource.class,
                        UnavailableFilter.class,
                        TestUtil.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    /**
     * A simple check that all events sent are received.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void onEvent() throws Exception {
        try (Client client = ClientBuilder.newClient()) {
            final CountDownLatch latch = new CountDownLatch(5);
            final WebTarget target = client.target(TestUtil.generateUri(uri, "callback/send/" + latch.getCount()));
            try (SseEventSource source = createEventSource(target)) {
                final List<String> eventCollector = new ArrayList<>();
                source.register(event -> {
                    eventCollector.add(event.readData());
                    latch.countDown();
                });
                source.open();
                Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS),
                        () -> String.format("Failed to receive %d events in 5 seconds. Received events: %s", latch.getCount(),
                                eventCollector));
            }
        }
    }

    /**
     * Checks that the onError callback was invoked. No reconnect should be done.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void onErrorInvoked() throws Exception {
        try (Client client = ClientBuilder.newClient()) {
            final CompletableFuture<Throwable> cause = new CompletableFuture<>();
            final WebTarget target = client.target(TestUtil.generateUri(uri, "callback/send/5?error=true"));
            // Create the event source and do not allow reconnecting
            try (SseEventSource source = createEventSource(target)) {
                final List<String> eventCollector = new ArrayList<>();
                source.register(event -> eventCollector.add(event.readData()), cause::complete);
                source.open();
                Assertions.assertInstanceOf(ServiceUnavailableException.class, cause.get(5, TimeUnit.SECONDS));
                // The events should be empty
                Assertions.assertTrue(eventCollector.isEmpty(),
                        () -> String.format("Expected no events but got %s", eventCollector));
            }
        }
    }

    /**
     * Checks that the onComplete callback was invoked and there were no errors discovered with a 204 response..
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void onCompleteInvokedNoContent() throws Exception {
        try (Client client = ClientBuilder.newClient()) {
            final AtomicReference<Throwable> onError = new AtomicReference<>();
            final CountDownLatch latch = new CountDownLatch(1);
            final WebTarget target = client.target(TestUtil.generateUri(uri, "callback/no-content"));
            try (SseEventSource source = createEventSource(target)) {
                final List<String> eventCollector = new ArrayList<>();
                source.register(event -> eventCollector.add(event.readData()), onError::set, latch::countDown);
                source.open();
                Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS),
                        () -> {
                            if (onError.get() != null) {
                                return formatStackTrace(onError.get());
                            }
                            return String.format("The onComplete callback was not invoked with 5 seconds. Received events: %s",
                                    eventCollector);
                        });
                Assertions.assertNull(onError.get(), () -> formatStackTrace(onError.get()));
                // The events should be empty
                Assertions.assertTrue(eventCollector.isEmpty(),
                        () -> String.format("Expected no events but got %s", eventCollector));
            }
        }
    }

    /**
     * Checks that the onComplete callback was invoked and there were no errors discovered
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void onCompleteInvoked() throws Exception {
        try (Client client = ClientBuilder.newClient()) {
            final AtomicReference<Throwable> onError = new AtomicReference<>();
            final CountDownLatch latch = new CountDownLatch(1);
            final WebTarget target = client.target(TestUtil.generateUri(uri, "callback/send/5"));
            try (SseEventSource source = createEventSource(target)) {
                final List<String> eventCollector = new ArrayList<>();
                source.register(event -> eventCollector.add(event.readData()), onError::set, latch::countDown);
                source.open();
                Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS),
                        () -> {
                            if (onError.get() != null) {
                                return formatStackTrace(onError.get());
                            }
                            return String.format("The onComplete callback was not invoked with 5 seconds. Received events: %s",
                                    eventCollector);
                        });
                Assertions.assertNull(onError.get(), () -> formatStackTrace(onError.get()));
                Assertions.assertEquals(5, eventCollector.size(),
                        () -> String.format("Expected only 5 events got: %s", eventCollector));
            }
        }
    }

    /**
     * Checks that the onError callback was invoked and the onComplete callback was not invoked.
     *
     * @throws Exception if an error occurs
     * @see SseEventSource#register(Consumer, Consumer, Runnable)
     */
    @Test
    public void onCompleteNotInvoked() throws Exception {
        try (Client client = ClientBuilder.newClient()) {
            final CompletableFuture<Throwable> cause = new CompletableFuture<>();
            final CountDownLatch latch = new CountDownLatch(1);
            final WebTarget target = client.target(TestUtil.generateUri(uri, "callback/send/5?error=true"));
            try (SseEventSource source = createEventSource(target)) {
                final List<String> eventCollector = new ArrayList<>();
                source.register(event -> eventCollector.add(event.readData()),
                        cause::complete,
                        latch::countDown);
                source.open();
                Assertions.assertInstanceOf(ServiceUnavailableException.class, cause.get(5, TimeUnit.SECONDS));
                // The events should be empty
                Assertions.assertTrue(eventCollector.isEmpty(),
                        () -> String.format("Expected no events but got %s", eventCollector));
                // There is a potential race condition however, it should be minimal. The issue would be that
                // onComplete could be invoked, but after this timeout. This is the only way to test that onComplete
                // was not invoked.
                Assertions.assertFalse(latch.await(2, TimeUnit.SECONDS),
                        "onComplete callbacks seem to have been invoked");
            }
        }
    }

    /**
     * Send a request which does not set a content type and results in a {@link ProcessingException}
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void successNoContentType() throws Exception {
        try (Client client = ClientBuilder.newClient()) {
            final CompletableFuture<Throwable> cause = new CompletableFuture<>();
            final CountDownLatch latch = new CountDownLatch(1);
            final WebTarget target = client.target(TestUtil.generateUri(uri, "callback/invalid-content-type"));
            try (SseEventSource source = createEventSource(target)) {
                final List<String> eventCollector = new ArrayList<>();
                source.register(event -> eventCollector.add(event.readData()),
                        cause::complete,
                        latch::countDown);
                source.open();
                final Throwable foundCause = cause.get(5, TimeUnit.SECONDS);
                Assertions.assertInstanceOf(ProcessingException.class, foundCause,
                        () -> formatStackTrace(ProcessingException.class, foundCause));
                // The events should be empty
                Assertions.assertTrue(eventCollector.isEmpty(),
                        () -> String.format("Expected no events but got %s", eventCollector));
                // There is a potential race condition however, it should be minimal. The issue would be that
                // onComplete could be invoked, but after this timeout. This is the only way to test that onComplete
                // was not invoked.
                Assertions.assertFalse(latch.await(2, TimeUnit.SECONDS),
                        "onComplete callbacks seem to have been invoked");
            }
        }
    }

    /**
     * Checks that the onError callback was invoked and the onComplete callback was not invoked on a 404 error.
     *
     * @throws Exception if an error occurs
     * @see SseEventSource#register(Consumer, Consumer, Runnable)
     */
    @Test
    public void onCompleteNotInvoked404() throws Exception {
        try (Client client = ClientBuilder.newClient()) {
            final CompletableFuture<Throwable> cause = new CompletableFuture<>();
            final CountDownLatch latch = new CountDownLatch(1);
            final WebTarget target = client.target(TestUtil.generateUri(uri, "callback/404"));
            try (SseEventSource source = createEventSource(target)) {
                final List<String> eventCollector = new ArrayList<>();
                source.register(event -> eventCollector.add(event.readData()),
                        cause::complete,
                        latch::countDown);
                source.open();
                Assertions.assertInstanceOf(NotFoundException.class, cause.get(5, TimeUnit.SECONDS));
                // The events should be empty
                Assertions.assertTrue(eventCollector.isEmpty(),
                        () -> String.format("Expected no events but got %s", eventCollector));
                // There is a potential race condition however, it should be minimal. The issue would be that
                // onComplete could be invoked, but after this timeout. This is the only way to test that onComplete
                // was not invoked.
                Assertions.assertFalse(latch.await(2, TimeUnit.SECONDS),
                        "onComplete callbacks seem to have been invoked");
            }
        }
    }

    /**
     * Checks that the onError callback was invoked and the onComplete callback was not invoked when a generic
     * exception is thrown. The resulting exception should be a {@link WebApplicationException}.
     *
     * @throws Exception if an error occurs
     * @see SseEventSource#register(Consumer, Consumer, Runnable)
     */
    @Test
    public void onCompleteNotInvokedWAE() throws Exception {
        try (Client client = ClientBuilder.newClient()) {
            final CompletableFuture<Throwable> cause = new CompletableFuture<>();
            final CountDownLatch latch = new CountDownLatch(1);
            final WebTarget target = client.target(TestUtil.generateUri(uri, "callback/send?error=runtime"));
            try (SseEventSource source = createEventSource(target)) {
                final List<String> eventCollector = new ArrayList<>();
                source.register(event -> eventCollector.add(event.readData()),
                        cause::complete,
                        latch::countDown);
                source.open();
                Assertions.assertInstanceOf(WebApplicationException.class, cause.get(5, TimeUnit.SECONDS));
                final WebApplicationException wae = (WebApplicationException) cause.get();
                Assertions.assertEquals("Failed on purpose with RuntimeException", wae.getResponse().readEntity(String.class));
                // The events should be empty
                Assertions.assertTrue(eventCollector.isEmpty(),
                        () -> String.format("Expected no events but got %s", eventCollector));
                // There is a potential race condition however, it should be minimal. The issue would be that
                // onComplete could be invoked, but after this timeout. This is the only way to test that onComplete
                // was not invoked.
                Assertions.assertFalse(latch.await(2, TimeUnit.SECONDS),
                        "onComplete callbacks seem to have been invoked");
            }
        }
    }

    /**
     * Invokes a request that first throws a {@link ServiceUnavailableException} with a 1 second retry. After the retry,
     * the filter should no longer throw an exception as the service is deemed available. The onEvent callback and
     * onComplete callback should be invoked. The onError callback should not be.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void onCompleteInvokedAfterRetry() throws Exception {
        try (Client client = ClientBuilder.newClient()) {
            reset(client);
            final AtomicReference<Throwable> onError = new AtomicReference<>();
            final CountDownLatch latch = new CountDownLatch(1);
            // Fail on the first two attempts, then allow success
            final WebTarget target = client.target(TestUtil.generateUri(uri, "callback/send/5?error=true&retry=1&passAfter=2"));
            try (SseEventSource source = createEventSource(target)) {
                final List<String> eventCollector = new ArrayList<>();
                source.register(event -> eventCollector.add(event.readData()), onError::set, latch::countDown);
                source.open();
                Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS),
                        () -> {
                            if (onError.get() != null) {
                                return "An error was found during invocation: " + onError.get().getMessage();
                            }
                            return String.format("The onComplete callback was not invoked with 5 seconds. Received events: %s",
                                    eventCollector);
                        });
                Assertions.assertNull(onError.get(), () -> formatStackTrace(onError.get()));
                Assertions.assertEquals(5, eventCollector.size(),
                        () -> String.format("Expected only 5 events got: %s", eventCollector));
            }
        }
    }

    /**
     * Makes a request that asks to be reconnected every second. The test waits for 2 seconds, then closes the
     * {@link SseEventSource}. This should cause the onComplete callback only to be invoked. The onError and onEvent
     * callbacks should never be invoked.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void onCompleteInvokedAfterClose() throws Exception {
        try (Client client = ClientBuilder.newClient()) {
            final AtomicReference<Throwable> onError = new AtomicReference<>();
            final CountDownLatch latch = new CountDownLatch(1);
            final WebTarget target = client.target(TestUtil.generateUri(uri, "callback/send/5?error=true&retry=1"));
            try (SseEventSource source = createEventSource(target)) {
                final List<String> eventCollector = new ArrayList<>();
                source.register(event -> eventCollector.add(event.readData()),
                        onError::set,
                        latch::countDown);
                source.open();
                // Wait for the reconnect to ensure the onComplete was invoked. We should reconnect after 1 second, we
                // will wait 2 to ensure the reconnect happens.
                TimeUnit.SECONDS.sleep(2L);
                source.close();
                Assertions.assertTrue(latch.await(1, TimeUnit.SECONDS), () -> {
                    final Throwable foundCause = onError.get();
                    if (foundCause != null) {
                        return formatStackTrace(foundCause);
                    }
                    return "A retry should have been done and the onComplete should have been invoked twice. Once before the reconnect and once after the reconnect.";
                });
                Assertions.assertNull(onError.get(), () -> formatStackTrace(onError.get()));
                // The events should be empty
                Assertions.assertTrue(eventCollector.isEmpty(),
                        () -> String.format("Expected no events but got %s", eventCollector));
            }
        }
    }

    private SseEventSource createEventSource(final WebTarget target) {
        return ((SseEventSourceImpl.SourceBuilder) SseEventSource.target(target))
                // Do not allow reconnecting as we want to abort when an error is received or the sink is closed
                .alwaysReconnect(false).build();
    }

    private void reset(final Client client) throws URISyntaxException {
        try (
                Response response = client.target(TestUtil.generateUri(uri, "callback/reset"))
                        .request()
                        .post(Entity.text(""))) {
            Assertions.assertEquals(200, response.getStatus(),
                    () -> "Unexpected response code: " + response.getStatus() + " - " + response.readEntity(String.class));
        }
    }

    private String formatStackTrace(final Throwable throwable) {
        return formatStackTrace(null, throwable);
    }

    private String formatStackTrace(final Class<? extends Throwable> expectedError, final Throwable throwable) {
        try (StringWriter writer = new StringWriter()) {
            throwable.printStackTrace(new PrintWriter(writer));
            if (expectedError != null) {
                return String.format("%s should have occurred, however we got: %s", expectedError, writer);
            }
            return String.format("No error should have occurred, however we got: %s", writer);
        } catch (IOException e) {
            if (expectedError != null) {
                return String.format("%s should have occurred, however we got: %s", expectedError, throwable.getMessage());
            }
            return String.format("No error should have occurred, however we got: %s", throwable.getMessage());
        }
    }

    @Provider
    @ApplicationScoped
    @PreMatching // This is done as a pre-match to avoid having to have a reset endpoint
    public static class UnavailableFilter implements ContainerRequestFilter {
        private final AtomicInteger counter = new AtomicInteger();

        @Override
        public void filter(final ContainerRequestContext requestContext) throws IOException {
            // Do not proceed, just reset the counter and respond with a 200.
            if (requestContext.getUriInfo().getPath().startsWith("/callback/reset")) {
                counter.set(0);
                requestContext.abortWith(Response.ok().build());
                return;
            }
            // Respond with no-content (204)
            if (requestContext.getUriInfo().getPath().startsWith("/callback/no-content")) {
                requestContext.abortWith(Response.noContent().build());
                return;
            }
            // Respond with success, but remove the Content-Type header
            if (requestContext.getUriInfo().getPath().startsWith("/callback/invalid-content-type")) {
                requestContext.abortWith(Response.ok().build());
                return;
            }
            final int currentCount = counter.incrementAndGet();
            final MultivaluedMap<String, String> queryParams = requestContext.getUriInfo().getQueryParameters();
            if (queryParams.containsKey("passAfter")) {
                if (currentCount > Integer.parseInt(queryParams.getFirst("passAfter"))) {
                    return;
                }
            }
            if (queryParams.containsKey("error")) {
                if (queryParams.containsKey("retry")) {
                    throw new ServiceUnavailableException("Failed on purpose",
                            Long.parseLong(queryParams.getFirst("retry")));
                } else {
                    if ("runtime".equals(queryParams.getFirst("error"))) {
                        throw new RuntimeException("Failed on purpose with RuntimeException");
                    }
                    throw new ServiceUnavailableException("Failed on purpose");
                }
            }
        }
    }
}
