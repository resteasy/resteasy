package org.jboss.resteasy.test.providers.sse;

import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.test.providers.sse.resource.SseSmokeResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.sse.InboundSseEvent;
import javax.ws.rs.sse.SseEventSource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@RunWith(Arquillian.class)
@RunAsClient
public class SseEventSourceTest {
    private final static Logger logger = Logger.getLogger(SseEventSourceTest.class);

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
                Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
            }
            Assert.assertEquals("One message was expected.", 1, results.size());
            Assert.assertThat("The message doesn't have expected content.", "data",
                    CoreMatchers.is(CoreMatchers.equalTo(results.get(0).readData(String.class))));
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
                Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
            }
            Assert.assertEquals("One message was expected.", 1, results.size());
            Assert.assertThat("The message doesn't have expected content.", "data",
                    CoreMatchers.is(CoreMatchers.equalTo(results.get(0).readData(String.class))));
        } finally {
            client.close();
        }
    }

    /**
     * @tpTestDetails Test `SseEventSource.register(Consumer<InboundSseEvent> onEvent, Consumer<Throwable> onError, Runnable onComplete)`
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
                Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
            }
            Assert.assertEquals(0, errors.get());
            Assert.assertEquals("One message was expected.", 1, results.size());
            Assert.assertThat("The message doesn't have expected content.", "data",
                    CoreMatchers.is(CoreMatchers.equalTo(results.get(0).readData(String.class))));
            Assert.assertEquals("On complete callback should be called one time", 1, completed.get());
        } finally {
            client.close();
        }
    }

}
