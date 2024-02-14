package org.jboss.resteasy.test.providers.sse;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.SseEventSource;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.providers.sse.resource.SseBroadcastResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class SseBroadcastTest {

    private static final Logger logger = Logger.getLogger(SseBroadcastTest.class);
    final String textMessage = "This is broadcast message";

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(SseBroadcastTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, SseBroadcastResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, SseBroadcastTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Server broadcaster can be used to manage multiple server sinks. Use sseBroadcaster.register(sink);
     *                to multiple sinks (client connections). Broadcast to all clients. Note: there is similar test
     *                SseTest.testBroadcast(),
     *                which uses two different broadcasters. This test uses one broadcaster and three clients subscribed to it.
     * @tpInfo RESTEASY-1680
     * @tpSince RESTEasy 3.5.0
     */
    @Test
    public void testBroadcasterMultipleSinks() throws Exception {
        final CountDownLatch latch = new CountDownLatch(3);
        final AtomicInteger errors = new AtomicInteger(0);
        final String textMessage = "This is broadcast message";

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(generateURL("/broadcast/subscribe"));

        Client client2 = ClientBuilder.newClient();
        WebTarget target2 = client2.target(generateURL("/broadcast/subscribe"));

        Client client3 = ClientBuilder.newClient();
        WebTarget target3 = client2.target(generateURL("/broadcast/subscribe"));

        SseEventSource msgEventSource = SseEventSource.target(target).build();
        SseEventSource msgEventSource2 = SseEventSource.target(target2).build();
        SseEventSource msgEventSource3 = SseEventSource.target(target3).build();

        try (SseEventSource eventSource = msgEventSource;
                SseEventSource eventSource2 = msgEventSource2;
                SseEventSource eventSource3 = msgEventSource3) {
            eventSource.register(event -> {
                Assertions.assertTrue(textMessage.equals(event.readData()), "Unexpected sever sent event data");
                latch.countDown();
            }, ex -> {
                errors.incrementAndGet();
                logger.error(ex.getMessage(), ex);
            });
            eventSource2.register(event -> {
                Assertions.assertTrue(textMessage.equals(event.readData()), "Unexpected sever sent event data");
                latch.countDown();
            }, ex -> {
                errors.incrementAndGet();
                logger.error(ex.getMessage(), ex);
            });
            eventSource3.register(event -> {
                Assertions.assertTrue(textMessage.equals(event.readData()), "Unexpected sever sent event data");
                latch.countDown();
            }, ex -> {
                errors.incrementAndGet();
                logger.error(ex.getMessage(), ex);
            });
            eventSource.open();
            eventSource2.open();
            eventSource3.open();

            client.target(generateURL("/broadcast/start")).request()
                    .post(Entity.entity(textMessage, MediaType.SERVER_SENT_EVENTS));
            client2.target(generateURL("/broadcast/start")).request()
                    .post(Entity.entity(textMessage, MediaType.SERVER_SENT_EVENTS));
            client3.target(generateURL("/broadcast/start")).request()
                    .post(Entity.entity(textMessage, MediaType.SERVER_SENT_EVENTS));
            Assertions.assertTrue(latch.await(20, TimeUnit.SECONDS),
                    "Waiting for broadcast event to be delivered has timed out.");
        } finally {
            client.close();
            client2.close();
            client3.close();
            removeBroadcaster();
        }
    }

    /**
     * @tpTestDetails SseBroadcaster.onClose(). The SseBroadcaster.onClose() is called, when server calls broadcaster.close().
     * @tpInfo RESTEASY-1680
     * @tpSince RESTEasy 3.5.0
     */
    @Test
    public void testBroadcasterOnCloseCallbackCloseBroadsCasterOnServer() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicInteger errors = new AtomicInteger(0);
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(generateURL("/broadcast/subscribe"));

        SseEventSource msgEventSource = SseEventSource.target(target).build();

        try (SseEventSource eventSource = msgEventSource) {
            eventSource.register(event -> {
                Assertions.assertTrue(textMessage.equals(event.readData()),
                        "Unexpected sever sent event data");
                latch.countDown();
            }, ex -> {
                errors.incrementAndGet();
                logger.error(ex.getMessage(), ex);
            });
            eventSource.open();

            client.target(generateURL("/broadcast/listeners")).request().get();
            client.target(generateURL("/broadcast/start")).request()
                    .post(Entity.entity(textMessage, MediaType.SERVER_SENT_EVENTS));
            Assertions.assertTrue(latch.await(20, TimeUnit.SECONDS),
                    "Waiting for broadcast event to be delivered has timed out.");
        } finally {
            client.close();
        }

        Client checkClient = ((ResteasyClientBuilder) ClientBuilder.newBuilder()).connectionPoolSize(10).build();
        checkClient.target(generateURL("/broadcast")).request().delete();
        boolean onCloseCalled = checkClient.target(generateURL("/broadcast/onCloseCalled")).request().get(boolean.class);
        Assertions.assertTrue(onCloseCalled);
        checkClient.close();
        removeBroadcaster();
    }

    /**
     * @tpTestDetails SseBroadcaster.onClose() The SseBroadcaster.onClose() is called after the SseEventSink is closed.
     * @tpInfo RESTEASY-1680, RESTEASY-1819
     * @tpSince RESTEasy 3.5.0
     */
    @Test
    @Disabled("https://issues.jboss.org/browse/RESTEASY-1819")
    public void testBroadcasterOnCloseCallbackCloseSinkOnServer() throws Exception {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(generateURL("/broadcast/subscribe"));

        SseEventSource msgEventSource = SseEventSource.target(target).reconnectingEvery(5, TimeUnit.MINUTES).build();
        try (SseEventSource eventSource = msgEventSource) {
            eventSource.register(event -> {
                Assertions.fail("Event should not be received");
            }, ex -> {
                logger.error(ex.getMessage(), ex);
            });
            eventSource.open();

            client.target(generateURL("/broadcast/listeners")).request().get();
            client.target(generateURL("/broadcast/closeSink")).request().get();
        } finally {
            client.close();
        }

        Client checkClient = ClientBuilder.newClient();

        boolean onCloseCalled = false;
        for (int i = 0; i < 30; i++) {
            onCloseCalled = checkClient.target(generateURL("/broadcast/onCloseCalled")).request().get(boolean.class);
            if (onCloseCalled) {
                break;
            }
            Thread.sleep(100);
        }

        Assertions.assertTrue(onCloseCalled);
        checkClient.close();
        removeBroadcaster();
    }

    /**
     * @tpTestDetails SseBroadcaster.onClose() The SseBroadcaster.onClose() is called
     *                after the client connection is closed.
     * @tpInfo RESTEASY-1680, RESTEASY-1819
     * @tpSince RESTEasy 3.5.0
     */
    @Test
    @Disabled("https://issues.jboss.org/browse/RESTEASY-1819")
    public void testBroadcasterOnCloseCallbackCloseClientConnection() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicInteger errors = new AtomicInteger(0);
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(generateURL("/broadcast/subscribe"));

        SseEventSource msgEventSource = SseEventSource.target(target).build();

        try (SseEventSource eventSource = msgEventSource) {
            eventSource.register(event -> {
                Assertions.assertTrue(textMessage.equals(event.readData()),
                        "Unexpected sever sent event data");
                latch.countDown();
            }, ex -> {
                errors.incrementAndGet();
                logger.error(ex.getMessage(), ex);
            });
            eventSource.open();

            client.target(generateURL("/broadcast/listeners")).request().get();
            client.target(generateURL("/broadcast/start")).request()
                    .post(Entity.entity(textMessage, MediaType.SERVER_SENT_EVENTS));
            Assertions.assertTrue(latch.await(20, TimeUnit.SECONDS),
                    "Waiting for broadcast event to be delivered has timed out.");
        } finally {
            client.close();
        }

        Client checkClient = ClientBuilder.newClient();

        boolean onCloseCalled = false;
        for (int i = 0; i < 30; i++) {
            onCloseCalled = checkClient.target(generateURL("/broadcast/onCloseCalled")).request().get(boolean.class);
            if (onCloseCalled) {
                break;
            }
            Thread.sleep(100);
        }

        Assertions.assertTrue(onCloseCalled);
        checkClient.close();
        removeBroadcaster();
    }

    private void removeBroadcaster() {
        // Close broadcaster
        Client client = ClientBuilder.newClient();
        client.target(generateURL("/broadcast")).request().delete();
        client.close();
    }
}
