package org.jboss.resteasy.test.providers.sse;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.providers.sse.resource.SseBroadcastResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.SseEventSource;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@RunWith(Arquillian.class)
@RunAsClient
public class SseBroadcastTest {

    private final static Logger logger = Logger.getLogger(SseBroadcastTest.class);
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
     * to multiple sinks (client connections). Broadcast to all clients. Note: there is similar test SseTest.testBroadcast(),
     * which uses two different broadcasters. This test uses one broadcaster and three clients subscribed to it.
     * @tpInfo RESTEASY-1680
     * @tpSince RESTEasy 3.5.0
     */
    @Test
    public void testBroadcasterMultipleSinks() throws Exception {
        final CountDownLatch latch = new CountDownLatch(3);
        final AtomicInteger errors = new AtomicInteger(0);
        final String textMessage = "This is broadcast message";

        Client client = new ResteasyClientBuilder().build();
        WebTarget target = client.target(generateURL("/broadcast/subscribe"));

        Client client2 = new ResteasyClientBuilder().build();
        WebTarget target2 = client2.target(generateURL("/broadcast/subscribe"));

        Client client3 = new ResteasyClientBuilder().build();
        WebTarget target3 = client2.target(generateURL("/broadcast/subscribe"));

        SseEventSource msgEventSource = SseEventSource.target(target).build();
        SseEventSource msgEventSource2 = SseEventSource.target(target2).build();
        SseEventSource msgEventSource3 = SseEventSource.target(target3).build();

        try (SseEventSource eventSource = msgEventSource;
             SseEventSource eventSource2 = msgEventSource2;
             SseEventSource eventSource3 = msgEventSource3) {
            eventSource.register(event -> {
                Assert.assertTrue("Unexpected sever sent event data", textMessage.equals(event.readData()));
                latch.countDown();
            }, ex -> {
                errors.incrementAndGet();
                logger.error(ex.getMessage(), ex);
            });
            eventSource2.register(event -> {
                Assert.assertTrue("Unexpected sever sent event data", textMessage.equals(event.readData()));
                latch.countDown();
            }, ex -> {
                errors.incrementAndGet();
                logger.error(ex.getMessage(), ex);
            });
            eventSource3.register(event -> {
                Assert.assertTrue("Unexpected sever sent event data", textMessage.equals(event.readData()));
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
            Assert.assertTrue("Waiting for broadcast event to be delivered has timed out.", latch.await(20, TimeUnit.SECONDS));
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
        Client client = new ResteasyClientBuilder().build();
        WebTarget target = client.target(generateURL("/broadcast/subscribe"));

        SseEventSource msgEventSource = SseEventSource.target(target).build();

        try (SseEventSource eventSource = msgEventSource) {
            eventSource.register(event -> {
                Assert.assertTrue("Unexpected sever sent event data", textMessage.equals(event.readData()));
                latch.countDown();
            }, ex -> {
                errors.incrementAndGet();
                logger.error(ex.getMessage(), ex);
            });
            eventSource.open();

            client.target(generateURL("/broadcast/listeners")).request().get();
            client.target(generateURL("/broadcast/start")).request()
                    .post(Entity.entity(textMessage, MediaType.SERVER_SENT_EVENTS));
            Assert.assertTrue("Waiting for broadcast event to be delivered has timed out.", latch.await(20, TimeUnit.SECONDS));
        } finally {
            client.close();
        }

        Client checkClient = new ResteasyClientBuilder().connectionPoolSize(10).build();
        checkClient.target(generateURL("/broadcast")).request().delete();
        boolean onCloseCalled = checkClient.target(generateURL("/broadcast/onCloseCalled")).request().get(boolean.class);
        Assert.assertTrue(onCloseCalled);
        checkClient.close();
        removeBroadcaster();
    }

    /**
     * @tpTestDetails SseBroadcaster.onClose() The SseBroadcaster.onClose() is called after the broadcast event is sent
     * and the SseEventSink is closed.
     * @tpInfo RESTEASY-1680
     * @tpSince RESTEasy 3.5.0
     */
    @Test
    public void testBroadcasterOnCloseCallbackCloseSinkOnServer() throws Exception {
        Client client = new ResteasyClientBuilder().build();
        WebTarget target = client.target(generateURL("/broadcast/subscribe"));

        SseEventSource msgEventSource = SseEventSource.target(target).reconnectingEvery(5, TimeUnit.MINUTES).build();
        try (SseEventSource eventSource = msgEventSource) {
            eventSource.register(event -> {
                Assert.assertTrue("Unexpected sever sent event data", textMessage.equals(event.readData()));
            }, ex -> {
                logger.error(ex.getMessage(), ex);
            });
            eventSource.open();

            client.target(generateURL("/broadcast/listeners")).request().get();
            client.target(generateURL("/broadcast/startAndClose")).request()
                    .post(Entity.entity(textMessage, MediaType.SERVER_SENT_EVENTS));
        } finally {
            client.close();
        }

        Client checkClient = new ResteasyClientBuilder().build();
        boolean onCloseCalled = checkClient.target(generateURL("/broadcast/onCloseCalled")).request().get(boolean.class);
        Assert.assertTrue(onCloseCalled);
        checkClient.close();
        removeBroadcaster();
    }

    private void removeBroadcaster() {
        // Close broadcaster
        Client client = new ResteasyClientBuilder().build();
        client.target(generateURL("/broadcast")).request().delete();
        client.close();
    }
}
