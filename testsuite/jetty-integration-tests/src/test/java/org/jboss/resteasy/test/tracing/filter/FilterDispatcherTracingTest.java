/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.tracing.filter;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.tracing.RESTEasyTracingLogger;
import org.jboss.resteasy.tracing.api.RESTEasyTracing;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Server-side tracing must work when RESTEasy is deployed as a servlet filter ({@code Filter30Dispatcher}) rather
 * than as a servlet. The filter path dispatches through {@code SynchronousDispatcher.invokePropagateNotFound()} so
 * that unmatched requests continue down the filter chain.
 * <p>
 * The tracing log records are observed through a {@link java.util.logging} handler: the embedded Jetty container
 * runs in the test JVM, so its loggers are the test's loggers.
 * </p>
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class FilterDispatcherTracingTest {

    private static final Logger TRACING_LOG = Logger.getLogger("org.jboss.resteasy.tracing");
    private static final List<String> TRACING_RECORDS = new CopyOnWriteArrayList<>();
    private static final Handler RECORDER = new Handler() {
        @Override
        public void publish(final LogRecord record) {
            TRACING_RECORDS.add(record.getMessage());
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() {
        }
    };

    private static Client client;
    private static Level tracingLogLevel;

    @Deployment
    public static Archive<?> createTestArchive() {
        return ShrinkWrap.create(WebArchive.class, FilterDispatcherTracingTest.class.getSimpleName() + ".war")
                .addClasses(TracingApplication.class, TracingResource.class, FallThroughServlet.class)
                .addAsWebInfResource("filter-dispatcher-tracing-web.xml", "web.xml");
    }

    @ArquillianResource
    private URI baseUri;

    @BeforeAll
    public static void setUp() {
        Assertions.assertTrue(RESTEasyTracingLogger.TRACING.AVAILABLE,
                "resteasy-tracing-api must be on the test class path; the embedded Jetty webapp class loader delegates"
                        + " to it, so the deployment sees the same class path");
        tracingLogLevel = TRACING_LOG.getLevel();
        TRACING_LOG.setLevel(Level.INFO);
        TRACING_LOG.addHandler(RECORDER);
        client = ResteasyClientBuilder.newClient();
    }

    @AfterAll
    public static void tearDown() {
        client.close();
        TRACING_LOG.removeHandler(RECORDER);
        TRACING_LOG.setLevel(tracingLogLevel);
    }

    @BeforeEach
    public void clearTracingRecords() {
        TRACING_RECORDS.clear();
    }

    @Test
    public void tracingHeadersPresentForMatchedRequest() {
        try (Response response = client.target(baseUri).path("resource").request().get()) {
            Assertions.assertEquals(200, response.getStatus());
            Assertions.assertEquals("traced", response.readEntity(String.class));

            final List<String> tracingHeaders = tracingHeaders(response);
            Assertions.assertFalse(tracingHeaders.isEmpty(),
                    () -> "Expected " + RESTEasyTracing.HEADER_TRACING_PREFIX + "* response headers, got only: "
                            + response.getStringHeaders().keySet());
            final List<String> events = tracedEvents(tracingHeaders);
            Assertions.assertTrue(events.contains("START"), () -> "START event missing from trace: " + events);
            Assertions.assertTrue(events.contains("FINISHED"), () -> "FINISHED event missing from trace: " + events);
        }
    }

    @Test
    public void unmatchedRequestFallsThroughWithoutTracingHeaders() {
        final WebTarget target = client.target(baseUri).path("servlet/fall-through");
        try (Response response = target.request().get()) {
            Assertions.assertEquals(200, response.getStatus());
            Assertions.assertEquals(FallThroughServlet.BODY, response.readEntity(String.class));
            Assertions.assertEquals(List.of(), tracingHeaders(response),
                    "RESTEasy did not handle this request and must not add tracing headers to the response");
        }

        final String start = TRACING_RECORDS.stream()
                .filter(record -> record.contains(" START ") && record.contains("requestUri=[" + target.getUri() + "]"))
                .findFirst()
                .orElseGet(() -> Assertions.fail("START record missing for " + target.getUri() + " in:\n"
                        + String.join("\n", TRACING_RECORDS)));
        final String requestId = start.substring(0, start.indexOf(' '));
        final List<String> finished = TRACING_RECORDS.stream()
                .filter(record -> record.startsWith(requestId + " FINISHED "))
                .collect(Collectors.toList());
        Assertions.assertEquals(1, finished.size(),
                () -> "Expected one FINISHED record for " + requestId + " in:\n" + String.join("\n", TRACING_RECORDS));
        Assertions.assertTrue(finished.get(0).contains("Response status: 404"),
                () -> "Declined request must finish with the 404 RESTEasy decided on: " + finished.get(0));
    }

    private static List<String> tracingHeaders(final Response response) {
        return response.getStringHeaders().entrySet().stream()
                .filter(e -> e.getKey().startsWith(RESTEasyTracing.HEADER_TRACING_PREFIX))
                .sorted(Map.Entry.comparingByKey())
                .flatMap(e -> e.getValue().stream())
                .collect(Collectors.toList());
    }

    /**
     * Each tracing header value is {@code <request id> <event category> [<timing>] <message>}; the second token is
     * the event category.
     */
    private static List<String> tracedEvents(final List<String> tracingHeaders) {
        return tracingHeaders.stream()
                .map(value -> value.split("\\s+")[1])
                .collect(Collectors.toList());
    }
}
