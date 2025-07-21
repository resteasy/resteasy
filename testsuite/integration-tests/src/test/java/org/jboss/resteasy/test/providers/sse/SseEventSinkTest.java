package org.jboss.resteasy.test.providers.sse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.sse.SseEventSource;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.plugins.providers.sse.client.SseEventSourceImpl;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class SseEventSinkTest {

    private static final Logger logger = Logger.getLogger(SseEventSinkTest.class);

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(SseEventSinkTest.class.getSimpleName());
        war.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
        return TestUtil.finishContainerPrepare(war, null, Arrays.asList(SseResource.class),
                ExecutorServletContextListener.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, SseEventSinkTest.class.getSimpleName());
    }

    @Test
    public void testCloseByEventSource() throws Exception {
        final CountDownLatch latch = new CountDownLatch(5);
        final List<String> results = new ArrayList<String>();
        final AtomicInteger errors = new AtomicInteger(0);
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(generateURL("/server-sent-events/events"));
        SseEventSource eventSource = SseEventSource.target(target).build();

        Assertions.assertEquals(SseEventSourceImpl.class, eventSource.getClass());
        eventSource.register(event -> {
            results.add(event.readData(String.class));
            latch.countDown();
        }, ex -> {
            errors.incrementAndGet();
            logger.error(ex.getMessage(), ex);
            throw new RuntimeException(ex);
        });
        eventSource.open();

        boolean waitResult = latch.await(30, TimeUnit.SECONDS);
        Assertions.assertEquals(0, errors.get());
        Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
        Client isOpenClient = ClientBuilder.newClient();
        Invocation.Builder isOpenRequest = isOpenClient.target(generateURL("/server-sent-events/isopen"))
                .request();

        jakarta.ws.rs.core.Response response = isOpenRequest.get();
        Assertions.assertTrue(response.readEntity(Boolean.class), "EventSink open is expected ");

        eventSource.close();
        Assertions.assertFalse(eventSource.isOpen(), "Closed eventSource state is expceted");

        WebTarget messageTarget = ClientBuilder.newClient().target(generateURL("/server-sent-events"));
        for (int counter = 0; counter < 5; counter++) {
            String msg = "messageAfterClose";
            messageTarget.request().post(Entity.text(msg));
        }

        Assertions.assertTrue(results.indexOf("messageAfterClose") == -1,
                "EventSource should not receive msg after it is closed");
        Assertions.assertFalse(isOpenRequest.get().readEntity(Boolean.class),
                "EventSink close is expected ");

    }

    /**
     * @tpTestDetails Test deadlock in sending of first sse events seen in 3.7.2
     * @tpInfo RESTEASY-3033
     * @tpSince RESTEasy 3.7.2
     */
    @Test
    public void deadlockAtInitialization() throws Exception {
        for (int i = 0; i < 100; i++) {
            testDeadlockAtInitialization(i);
        }
    }

    private void testDeadlockAtInitialization(final int run) throws Exception {
        final int count = run == 0 ? 10 : Math.min((run * 10), 200);
        final CountDownLatch latch = new CountDownLatch(1);
        final List<String> results = new ArrayList<>();
        final Client client = ClientBuilder.newClient();
        try {
            final WebTarget target = client.target(generateURL("/server-sent-events/initialization-deadlock/" + count));
            try (SseEventSource eventSource = SseEventSource.target(target).build()) {
                eventSource.register(event -> {
                    final String msg = event.readData(String.class);
                    results.add(msg);
                    if (msg.startsWith("last-msg")) {
                        latch.countDown();
                    }
                }, ex -> {
                    logger.error(ex.getMessage(), ex);
                    Assertions.fail(ex.getMessage());
                });
                eventSource.open();
                final boolean await = latch.await(15, TimeUnit.SECONDS);
                Assertions.assertTrue(await, String.format("Waiting for event to be delivered has timed out at run %d.", run));
                for (int i = 0; i < results.size(); i++) {
                    Assertions.assertTrue(results.get(i).endsWith("-" + i),
                            String.format("Wrong message order in run %d: %s", run, results));
                }
            }
        } finally {
            client.close();
        }
    }
}
