package org.jboss.resteasy.test.providers.sse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.ws.rs.ServiceUnavailableException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.sse.InboundSseEvent;
import jakarta.ws.rs.sse.SseEvent;
import jakarta.ws.rs.sse.SseEventSource;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.providers.sse.resource.SseReconnectResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class SseReconnectTest {

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(SseReconnectTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, SseReconnectResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, SseReconnectTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Check SseEvent.getReconnectDelay() returns SseEvent.RECONNECT_NOT_SET,
     *                when OutboundSseEvent.reconnectDelay() is not set.
     * @tpInfo RESTEASY-1680
     * @tpSince RESTEasy 3.5.0
     */
    @Test
    public void testReconnectDelayIsNotSet() throws Exception {
        Client client = ClientBuilder.newClient();
        try {
            WebTarget baseTarget = client.target(generateURL("/reconnect/defaultReconnectDelay"));
            try (Response response = baseTarget.request().get()) {
                Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
                Assertions.assertEquals(SseEvent.RECONNECT_NOT_SET, (long) response.readEntity(long.class));
            }
        } finally {
            client.close();
        }
    }

    /**
     * @tpTestDetails *** Check SseEvent.getReconnectDelay() returns correct delay,
     *                when OutboundSseEvent.reconnectDelay() is set.
     * @tpInfo RESTEASY-1680
     * @tpSince RESTEasy 3.5.0
     */
    @Test
    public void testReconnectDelayIsSet() throws Exception {
        Client client = ClientBuilder.newClient();
        try {
            WebTarget baseTarget = client.target(generateURL("/reconnect/reconnectDelaySet"));
            try (Response response = baseTarget.request().get()) {
                Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
                Assertions.assertEquals(1000L, (long) response.readEntity(long.class));
            }
        } finally {
            client.close();
        }
    }

    /**
     * @tpTestDetails SseEventSource receives HTTP 503 + "Retry-After" from the SSE endpoint. Check that SseEventSource
     *                retries after the specified period
     * @tpInfo RESTEASY-1680
     * @tpSince RESTEasy 3.5.0
     */
    @Test
    public void testSseEndpointUnavailable() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final List<String> results = new ArrayList<String>();
        Client client = ClientBuilder.newBuilder().build();
        try {
            WebTarget target = client.target(generateURL("/reconnect/unavailable"));
            SseEventSource msgEventSource = SseEventSource.target(target).build();
            try (SseEventSource eventSource = msgEventSource) {
                eventSource.register(event -> {
                    results.add(event.readData(String.class));
                    latch.countDown();
                }, ex -> {
                });
                eventSource.open();

                boolean waitResult = latch.await(30, TimeUnit.SECONDS);
                Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
            }
            Assertions.assertTrue(results.get(0).equals("ServiceAvailable"), "ServiceAvailable message is expected");
        } finally {
            client.close();
        }
    }

    /**
     * @tpTestDetails SseEventSource receives HTTP 503 + "Retry-After"
     *                from the SSE endpoint. Endpoint does retries but still does not succeed
     * @tpInfo RESTEASY-2854
     * @tpSince RESTEasy 4.7.0
     */
    @Test
    public void testSseEndpointUnavailableAfterRetry() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicInteger errors = new AtomicInteger(0);
        final List<String> results = new ArrayList<String>();
        Client client = ClientBuilder.newBuilder().build();
        try {
            WebTarget target = client.target(generateURL("/reconnect/unavailableAfterRetry"));
            SseEventSource msgEventSource = SseEventSource.target(target).build();
            try (SseEventSource eventSource = msgEventSource) {
                eventSource.register(event -> {
                    results.add(event.readData(String.class));
                    latch.countDown();
                }, ex -> {
                    errors.incrementAndGet();
                    Assertions.assertTrue(ex instanceof ServiceUnavailableException, "ServiceUnavalile exception is expected");
                });
                eventSource.open();

                boolean waitResult = latch.await(15, TimeUnit.SECONDS);
                Assertions.assertEquals(1, errors.get());
            }
        } finally {
            client.close();
        }
    }

    /**
     * @tpTestDetails Check that SseEventSource use last received 'retry' field value as the default reconnect delay for all
     *                future requests.
     * @tpInfo RESTEASY-1958
     * @tpSince RESTEasy
     */
    @Test
    public void testReconnectDelayIsUsed() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        List<InboundSseEvent> results = new ArrayList<>();
        AtomicInteger errorCount = new AtomicInteger();
        Client client = ClientBuilder.newBuilder().build();
        try {
            WebTarget target = client.target(generateURL("/reconnect/testReconnectDelayIsUsed"));
            SseEventSource sseEventSource = SseEventSource.target(target).reconnectingEvery(500, TimeUnit.MILLISECONDS)
                    .build();
            sseEventSource.register(event -> {
                results.add(event);
                latch.countDown();
            }, error -> {
                if (error instanceof WebApplicationException) {
                    if (599 == ((WebApplicationException) error).getResponse().getStatus()) {
                        return;
                    }
                }
                errorCount.incrementAndGet();
            });
            try (SseEventSource eventSource = sseEventSource) {
                eventSource.open();
                boolean waitResult = latch.await(30, TimeUnit.SECONDS);
                Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
                Assertions.assertEquals(0, errorCount.get());
                Assertions.assertEquals(1, results.size());
                Assertions.assertTrue(results.get(0).isReconnectDelaySet());
                Assertions.assertEquals(TimeUnit.SECONDS.toMillis(3), results.get(0).getReconnectDelay());
            }
        } finally {
            client.close();
        }
    }

    @Test
    public void testReconnect() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        List<InboundSseEvent> results = new ArrayList<>();
        Client client = ClientBuilder.newBuilder().build();
        try {
            WebTarget target = client.target(generateURL("/reconnect/sselost"));
            SseEventSource sseEventSource = SseEventSource.target(target).reconnectingEvery(2000, TimeUnit.MILLISECONDS)
                    .build();
            sseEventSource.register(event -> {
                results.add(event);
                latch.countDown();
            });
            try (SseEventSource eventSource = sseEventSource) {
                eventSource.open();
                boolean waitResult = latch.await(30, TimeUnit.SECONDS);
                Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
                Assertions.assertEquals(1, results.size());
            }
        } finally {
            client.close();
        }
    }

    @Test
    public void testEventSourceIsOpen() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        List<InboundSseEvent> results = new ArrayList<>();
        Client client = ClientBuilder.newBuilder().build();
        try {
            WebTarget target = client.target(generateURL("/reconnect/data"));
            SseEventSource sseEventSource = SseEventSource.target(target).build();
            sseEventSource.register(event -> {
                results.add(event);
                latch.countDown();
            });
            try (SseEventSource eventSource = sseEventSource) {
                eventSource.open();
                boolean waitResult = latch.await(30, TimeUnit.SECONDS);
                Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
                Assertions.assertEquals(1, results.size());
                Assertions.assertTrue(eventSource.isOpen(), "SseEventSource#isOpen returns false");
            }
        } finally {
            client.close();
        }
    }

}
