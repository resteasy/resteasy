package org.jboss.resteasy.test.providers.sse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.sse.InboundSseEvent;
import jakarta.ws.rs.sse.SseEventSource;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.test.providers.sse.resource.SseSmokeResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class SseEventSourceTest {
    private static final Logger logger = Logger.getLogger(SseEventSourceTest.class);

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(SseEventSourceTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, SseSmokeResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, SseEventSourceTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Test `SseEventSource.register(Consumer<InboundSseEvent> onEvent)`
     * @tpInfo RESTEASY-1680
     * @tpSince RESTEasy 3.5.0
     */
    @Test
    public void testSseEventSourceOnEventCallback() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final List<InboundSseEvent> results = new ArrayList<InboundSseEvent>();
        Client client = ClientBuilder.newBuilder().build();
        try {
            WebTarget target = client.target(generateURL("/sse/eventssimple"));
            SseEventSource msgEventSource = SseEventSource.target(target).build();
            try (SseEventSource eventSource = msgEventSource) {
                eventSource.register(event -> {
                    results.add(event);
                    latch.countDown();
                });
                eventSource.open();

                boolean waitResult = latch.await(30, TimeUnit.SECONDS);
                Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
            }

            Assertions.assertEquals(1, results.size(), "One message was expected.");
            Assertions.assertEquals("data", results.get(0).readData(String.class),
                    "The message doesn't have expected content.");
        } finally {
            client.close();
        }
    }

    /**
     * @tpTestDetails Test `SseEventSource.register(Consumer<InboundSseEvent> onEvent, Consumer<Throwable> onError)`
     * @tpInfo RESTEASY-1680
     * @tpSince RESTEasy 3.5.0
     */
    @Test
    public void testSseEventSourceOnEventOnErrorCallback() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final List<InboundSseEvent> results = new ArrayList<InboundSseEvent>();
        final AtomicInteger errors = new AtomicInteger(0);
        Client client = ClientBuilder.newBuilder().build();
        try {
            WebTarget target = client.target(generateURL("/sse/eventssimple"));
            SseEventSource msgEventSource = SseEventSource.target(target).build();
            try (SseEventSource eventSource = msgEventSource) {
                eventSource.register(event -> {
                    results.add(event);
                    latch.countDown();
                }, ex -> {
                    errors.incrementAndGet();
                    logger.error(ex.getMessage(), ex);
                    throw new RuntimeException(ex);
                });
                eventSource.open();

                boolean waitResult = latch.await(30, TimeUnit.SECONDS);
                Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
            }
            Assertions.assertEquals(1, results.size(), "One message was expected.");
            Assertions.assertEquals("data", results.get(0).readData(String.class),
                    "The message doesn't have expected content.");
        } finally {
            client.close();
        }
    }

    /**
     * @tpTestDetails Test `SseEventSource.register(Consumer<InboundSseEvent> onEvent, Consumer<Throwable> onError, Runnable
     *                onComplete)`
     * @tpInfo RESTEASY-1680
     * @tpSince RESTEasy 3.5.0
     */
    @Test
    public void testSseEventSourceOnEventOnErrorOnCompleteCallback() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final List<InboundSseEvent> results = new ArrayList<InboundSseEvent>();
        final AtomicInteger errors = new AtomicInteger(0);
        final AtomicInteger completed = new AtomicInteger(0);
        Client client = ClientBuilder.newBuilder().build();
        try {
            WebTarget target = client.target(generateURL("/sse/eventssimple"));
            SseEventSource msgEventSource = SseEventSource.target(target).build();
            try (SseEventSource eventSource = msgEventSource) {
                eventSource.register(event -> {
                    results.add(event);
                    latch.countDown();
                }, ex -> {
                    errors.incrementAndGet();
                    logger.error(ex.getMessage(), ex);
                    throw new RuntimeException(ex);
                }, () -> {
                    completed.incrementAndGet();
                });
                eventSource.open();

                boolean waitResult = latch.await(30, TimeUnit.SECONDS);
                Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
            }
            Assertions.assertEquals(0, errors.get());
            Assertions.assertEquals(1, results.size(), "One message was expected.");
            Assertions.assertEquals("data", results.get(0).readData(String.class),
                    "The message doesn't have expected content.");
            Assertions.assertEquals(1, completed.get(), "On complete callback should be called one time");
        } finally {
            client.close();
        }
    }

    @Test
    public void testSseEventSourceCountDownOnCompleteCallback() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final List<InboundSseEvent> results = new ArrayList<InboundSseEvent>();
        final AtomicInteger errors = new AtomicInteger(0);
        final AtomicInteger completed = new AtomicInteger(0);
        Client client = ClientBuilder.newBuilder().build();
        try {
            WebTarget target = client.target(generateURL("/sse/eventssimple"));
            SseEventSource msgEventSource = SseEventSource.target(target).build();
            try (SseEventSource eventSource = msgEventSource) {
                eventSource.register(event -> {
                    results.add(event);
                    latch.countDown();
                }, ex -> {
                    errors.incrementAndGet();
                    logger.error(ex.getMessage(), ex);
                    throw new RuntimeException(ex);
                }, () -> {
                    completed.incrementAndGet();
                });
                eventSource.open();

                boolean waitResult = latch.await(30, TimeUnit.SECONDS);
                if ((!waitResult) && (results.size() != 1)) {
                    Assertions.assertEquals(1, results.size(), "Waiting has timed out and only one message was expected.");
                }

            }
            Assertions.assertTrue(completed.get() > 0, "Waiting for onComplete has timed out.");
            Assertions.assertEquals(0, errors.get());
            Assertions.assertEquals(1, results.size(), "One message was expected.");
            Assertions.assertEquals("data", results.get(0).readData(String.class),
                    "The message doesn't have expected content.");
            Assertions.assertEquals(1, completed.get(), "On complete callback should be called one time");
        } finally {
            client.close();
        }
    }

}
