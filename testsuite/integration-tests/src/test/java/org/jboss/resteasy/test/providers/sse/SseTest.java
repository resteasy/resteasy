package org.jboss.resteasy.test.providers.sse;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.sse.InboundSseEvent;
import jakarta.ws.rs.sse.SseEventSource;
import jakarta.xml.bind.JAXBElement;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.plugins.providers.sse.client.SseEventSourceImpl;
import org.jboss.resteasy.plugins.providers.sse.client.SseEventSourceImpl.SourceBuilder;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.wildfly.testing.tools.deployments.DeploymentDescriptors;

import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(ArquillianExtension.class)
@RunAsClient
@TestMethodOrder(OrderAnnotation.class)
public class SseTest {

    private static final Logger logger = Logger.getLogger(SseTest.class);

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(SseTest.class.getSimpleName());
        war.addClass(SseTest.class);
        war.addAsResource("org/jboss/resteasy/test/providers/sse/bigmsg.json",
                "org/jboss/resteasy/test/providers/sse/bigmsg.json");
        war.addAsWebInfResource("org/jboss/resteasy/test/providers/sse/web.xml", "web.xml");
        war.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
        war.addAsManifestResource(DeploymentDescriptors.createPermissionsXmlAsset(
                new RuntimePermission("modifyThread")), "permissions.xml");
        return TestUtil.finishContainerPrepare(war, null, SseApplication.class, SseResource.class,
                AnotherSseResource.class, EscapingSseResource.class, ExecutorServletContextListener.class);

    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, SseTest.class.getSimpleName());
    }

    @Test
    @Order(1)
    public void testAddMessage() throws Exception {
        final CountDownLatch latch = new CountDownLatch(5);
        final AtomicInteger errors = new AtomicInteger(0);
        final List<String> results = new ArrayList<String>();
        final List<String> sent = new ArrayList<String>();
        Client client = ClientBuilder.newBuilder().build();
        WebTarget target = client.target(generateURL("/service/server-sent-events"));
        SseEventSource msgEventSource = SseEventSource.target(target).build();
        try (SseEventSource eventSource = msgEventSource) {
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
            Client messageClient = ((ResteasyClientBuilder) ClientBuilder.newBuilder()).connectionPoolSize(10).build();
            WebTarget messageTarget = messageClient.target(generateURL("/service/server-sent-events"));
            for (int counter = 0; counter < 5; counter++) {
                String msg = "message " + counter;
                sent.add(msg);
                messageTarget.request().post(Entity.text(msg));
            }
            boolean waitResult = latch.await(30, TimeUnit.SECONDS);
            Assertions.assertEquals(0, errors.get());
            Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
            messageTarget.request().delete();
            messageClient.close();
        }
        Assertions.assertFalse(msgEventSource.isOpen(), "SseEventSource is not closed");
        Assertions.assertTrue(results.size() == 5, () -> "5 messages are expected, but is : " + results.size());
        for (String s : sent) {
            Assertions.assertTrue(results.contains(s), () -> "Sent message \"" + s + "\" not found as result.");
        }
        client.close();
    }

    //Test for Last-Event-Id. This test uses the message items stores in testAddMessage()
    @Test
    @Order(2)
    public void testLastEventId() throws Exception {
        final CountDownLatch missedEventLatch = new CountDownLatch(3);
        final List<String> missedEvents = new ArrayList<String>();
        Client c = ((ResteasyClientBuilder) ClientBuilder.newBuilder()).connectionPoolSize(10).build();
        WebTarget lastEventTarget = c.target(generateURL("/service/server-sent-events"));
        SseEventSourceImpl lastEventSource = (SseEventSourceImpl) SseEventSource.target(lastEventTarget).build();
        lastEventSource.register(event -> {
            missedEvents.add(event.toString());
            missedEventLatch.countDown();
        }, ex -> {
            throw new RuntimeException(ex);
        });
        lastEventSource.open("1");
        Assertions.assertTrue(missedEventLatch.await(30, TimeUnit.SECONDS),
                () -> "Waiting for missed events to be delivered has timed our, received events :"
                        + missedEvents);
        Assertions.assertEquals(3, missedEvents.size(), () -> "3 messages are expected, but is : " + missedEvents);
        lastEventTarget.request().delete();
        lastEventSource.close();
        c.close();
    }

    @Test
    @Order(3)
    public void testSseEvent() throws Exception {
        final List<String> results = new ArrayList<String>();
        final CountDownLatch latch = new CountDownLatch(6);
        final AtomicInteger errors = new AtomicInteger(0);
        Client client = ((ResteasyClientBuilder) ClientBuilder.newBuilder()).connectionPoolSize(10).build();
        WebTarget target = client.target(generateURL("/service/server-sent-events")).path("domains").path("1");

        SseEventSource eventSource = SseEventSource.target(target).build();
        Assertions.assertEquals(SseEventSourceImpl.class, eventSource.getClass());
        eventSource.register(event -> {
            results.add(event.readData());
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
        Assertions.assertEquals(6, results.size(), "6 SseInboundEvent expected");
        Assertions.assertTrue(results.get(5).contains("Done"),
                () -> "Expect the last event is Done event, but it is :" + results);
        eventSource.close();
        client.close();
    }

    @Test
    @Order(4)
    public void testBroadcast() throws Exception {
        final CountDownLatch latch = new CountDownLatch(3);
        Client client = ((ResteasyClientBuilder) ClientBuilder.newBuilder()).build();
        WebTarget target = client.target(generateURL("/service/server-sent-events/subscribe"));
        final String textMessage = "This is broadcast message";
        Consumer<InboundSseEvent> checkConsumer = insse -> {
            if (latch.getCount() > 0) {
                Assertions.assertEquals(textMessage, insse.readData(), "Unexpected sever sent event data");
            }
            latch.countDown();
        };
        SseEventSource eventSource = SseEventSource.target(target).build();
        Assertions.assertEquals(SseEventSourceImpl.class, eventSource.getClass());
        eventSource.register(checkConsumer);
        eventSource.open();

        Client client2 = ClientBuilder.newClient();
        WebTarget target2 = client2.target(generateURL("/service/sse/subscribe"));

        SseEventSource eventSource2 = SseEventSource.target(target2).build();
        eventSource2.register(checkConsumer);
        eventSource2.open();
        //Test for eventSource subscriber

        client.target(generateURL("/service/server-sent-events/broadcast")).request()
                .post(Entity.entity(textMessage, MediaType.SERVER_SENT_EVENTS));
        client2.target(generateURL("/service/sse/broadcast")).request()
                .post(Entity.entity(textMessage, MediaType.SERVER_SENT_EVENTS));
        Assertions.assertTrue(latch.await(20, TimeUnit.SECONDS),
                "Waiting for broadcast event to be delivered has timed out.");

        //one subscriber is closed and test if another subscriber works
        CountDownLatch latch3 = new CountDownLatch(5);
        CountDownLatch latch4 = new CountDownLatch(5);
        eventSource.register(insse -> {
            latch3.countDown();
        });
        eventSource2.register(insse -> {
            latch4.countDown();
        });
        client2.target(generateURL("/service/server-sent-events/broadcast")).request()
                .post(Entity.entity("repeat", MediaType.SERVER_SENT_EVENTS));
        Assertions.assertTrue(latch3.await(20, TimeUnit.SECONDS),
                "Waiting for repeatable broadcast events to be delivered has timed out.");
        Assertions.assertTrue(latch4.await(20, TimeUnit.SECONDS),
                "Waiting for repeatable broadcast events to be delivered has timed out.");

        client.close();
        CountDownLatch latch5 = new CountDownLatch(5);
        CountDownLatch latch6 = new CountDownLatch(5);

        eventSource.register(insse -> {
            latch5.countDown();
        });
        eventSource2.register(insse -> {
            latch6.countDown();
        });

        Assertions.assertEquals(5, latch5.getCount(), "Eventsource should not receive event after close");
        Assertions.assertTrue(latch6.await(20, TimeUnit.SECONDS),
                "Waiting for eventsource2 receive broadcast events to be delivered has timed out.");

        client2.target(generateURL("/service/server-sent-events/broadcast")).request()
                .post(Entity.entity("close one subscriber", MediaType.SERVER_SENT_EVENTS));

        Client closeClient = ClientBuilder.newClient();
        WebTarget closeTarget = closeClient.target(generateURL("/service/sse"));
        Assertions.assertTrue(closeTarget.request().delete().readEntity(Boolean.class),
                "Subscribed eventsink is not closed");
        eventSource2.close();
        client2.close();
        closeClient.close();
    }

    //This test is checking SseEventSource reconnect ability. When request post /addMessageAndDisconnect path, server will
    //disconnect the connection, but events is continued to add to eventsStore. SseEventSource will automatically reconnect
    //with LastEventId and receive the missed events
    @Test
    @Order(5)
    public void testReconnect() throws Exception {
        int proxyPort = PortProviderUtil.findOpenPort();
        SimpleProxyServer proxy = new SimpleProxyServer(PortProviderUtil.getHost(), PortProviderUtil.getPort(), proxyPort);
        proxy.start();
        int maxWaits = 30;
        while (!proxy.isStarted()) {
            Assertions.assertTrue(maxWaits-- > 0);
            logger.info("Proxy not started yet, sleeping 100ms");
            Thread.sleep(100);
        }
        final CountDownLatch latch = new CountDownLatch(10);
        final List<String> results = new ArrayList<String>();
        final AtomicInteger errors = new AtomicInteger(0);
        ResteasyClient client = ((ResteasyClientBuilder) ClientBuilder.newBuilder()).connectionPoolSize(10).build();
        String requestPath = PortProviderUtil.generateURL("/service/server-sent-events",
                SseTest.class.getSimpleName(), PortProviderUtil.getHost(), proxyPort);
        WebTarget target = client.target(requestPath);
        try (SseEventSource eventSource = SseEventSource.target(target).reconnectingEvery(500, TimeUnit.MILLISECONDS).build()) {
            Assertions.assertEquals(SseEventSourceImpl.class, eventSource.getClass());
            eventSource.register(event -> {
                results.add(event.toString());
                latch.countDown();
                if (latch.getCount() == 8) {
                    new Thread() {
                        public void run() {
                            proxy.stop();
                            try {
                                Thread.sleep(300);
                            } catch (Exception e) {
                                logger.error("Exception thrown when sleep some time to start proxy ", e);
                            }
                            proxy.start();
                        }
                    }.start();
                }
            }, ex -> {
                errors.incrementAndGet();
                logger.error("test reconnect error", ex);
                throw new RuntimeException(ex);
            });
            eventSource.open();

            Client messageClient = ClientBuilder.newClient();
            WebTarget messageTarget = messageClient
                    .target(generateURL("/service/server-sent-events/addMessageAndDisconnect"));
            messageTarget.request().post(Entity.text("msg"));
            messageClient.close();

            boolean waitResult = latch.await(30, TimeUnit.SECONDS);
            Assertions.assertEquals(0, errors.get());
            Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
            Assertions.assertEquals(10, results.size(), () -> "10 events are expected, but is : " + results.size());
            target.request().delete();
            proxy.stop();
        }
        client.close();
    }

    @Test
    @Order(6)
    public void testEventSourceConsumer() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        Client client = ((ResteasyClientBuilder) ClientBuilder.newBuilder()).connectionPoolSize(10).build();
        WebTarget target = client.target(generateURL("/service/server-sent-events/error"));
        List<Throwable> errorList = new ArrayList<Throwable>();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try (SseEventSource eventSource = SseEventSource.target(target).build()) {
                    eventSource.register(event -> {
                        latch.countDown();
                    }, ex -> {
                        if (ex instanceof InternalServerErrorException) {
                            errorList.add(ex);
                            latch.countDown();
                        }
                    });
                    eventSource.open();
                }
            }
        });
        t.start();
        if (latch.await(30, TimeUnit.SECONDS)) {
            t.interrupt();
        }
        Assertions.assertFalse(errorList.isEmpty(), "InternalServerErrorException isn't processed in error consumer");
        client.close();
    }

    @Test
    @Order(7)
    public void testMultipleDataFields() throws Exception {
        final CountDownLatch latch = new CountDownLatch(7);
        final AtomicInteger errors = new AtomicInteger(0);
        final SortedSet<String> results = new TreeSet<String>();
        Client client = ClientBuilder.newBuilder().build();
        WebTarget target = client.target(generateURL("/service/server-sent-events"));
        SseEventSource msgEventSource = SseEventSource.target(target).build();
        try (SseEventSource eventSource = msgEventSource) {
            Assertions.assertEquals(SseEventSourceImpl.class, eventSource.getClass());
            eventSource.register(event -> {
                results.add(event.readData());
                latch.countDown();
            }, ex -> {
                errors.incrementAndGet();
                logger.error(ex.getMessage(), ex);
                throw new RuntimeException(ex);
            });
            eventSource.open();
            Client messageClient = ((ResteasyClientBuilder) ClientBuilder.newBuilder()).connectionPoolSize(10).build();
            WebTarget messageTarget = messageClient.target(generateURL("/service/server-sent-events"));
            messageTarget.request().post(Entity.text("data0a"));
            messageTarget.request().post(Entity.text("data1a\ndata1b\n\rdata1c"));
            messageTarget.request().post(Entity.text("data2a\r\ndata2b"));
            messageTarget.request().post(Entity.text("data3a\n\rdata3b"));
            messageTarget.request().post(Entity.text("data4a\r\ndata4b"));
            messageTarget.request().post(Entity.text("data5a\r\r\r\ndata5b"));
            messageTarget.request().post(Entity.text("data6a\n\n\r\r\ndata6b"));
            boolean waitResult = latch.await(30, TimeUnit.SECONDS);
            Assertions.assertEquals(0, errors.get());
            Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
            messageTarget.request().delete();
            messageClient.close();
        }
        Assertions.assertFalse(msgEventSource.isOpen(), "SseEventSource is not closed");
        Assertions.assertEquals(7, results.size(), () -> "5 messages are expected, but is : " + results.size());
        String[] lines = results.toArray(new String[] {})[1].split("\n");
        Assertions.assertEquals(3, lines.length, () -> "3 data fields are expected, but is : " + lines.length);
        Assertions.assertEquals("data1b", lines[1], () -> "expect second data field value is : " + lines[1]);
        client.close();
    }

    @Test
    @Order(8)
    public void testEscapedMessage() throws Exception {
        final CountDownLatch latch = new CountDownLatch(3);
        final AtomicInteger errors = new AtomicInteger(0);
        final List<String> results = new ArrayList<String>();
        final List<String> sent = new ArrayList<String>();
        sent.add("foo1\nbar");
        sent.add("foo2\nbar");
        sent.add("foo3\nbar");
        Client client = ClientBuilder.newBuilder().build();
        WebTarget target = client.target(generateURL("/service/sse-escaping"));
        SseEventSource msgEventSource = SseEventSource.target(target).build();
        try (SseEventSource eventSource = msgEventSource) {
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
        }
        Assertions.assertFalse(msgEventSource.isOpen(), "SseEventSource is not closed");
        Assertions.assertEquals(3, results.size(), () -> "3 messages are expected, but is : " + results.size());
        for (String s : sent) {
            Assertions.assertTrue(results.contains(s), () -> "Sent message \"" + s + "\" not found as result.");
        }
        client.close();
    }

    @Test
    @Order(10)
    public void testXmlEvent() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicInteger errors = new AtomicInteger(0);
        final List<InboundSseEvent> results = new ArrayList<InboundSseEvent>();
        Client client = ClientBuilder.newBuilder().build();
        WebTarget target = client.target(generateURL("/service/server-sent-events/xmlevent"));
        SseEventSource msgEventSource = SseEventSource.target(target).build();
        try (SseEventSource eventSource = msgEventSource) {
            Assertions.assertEquals(SseEventSourceImpl.class, eventSource.getClass());
            eventSource.register(event -> {
                results.add(event);
                latch.countDown();
            }, ex -> {
                errors.incrementAndGet();
                logger.error("Error:", ex);
                throw new RuntimeException(ex);
            });
            eventSource.open();

            boolean waitResult = latch.await(30, TimeUnit.SECONDS);
            Assertions.assertEquals(0, errors.get());
            Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
        }
        JAXBElement<String> jaxbElement = results.get(0).readData(new jakarta.ws.rs.core.GenericType<JAXBElement<String>>() {
        }, MediaType.APPLICATION_XML_TYPE);
        Assertions.assertEquals(jaxbElement.getValue(), "xmldata", "xmldata is expceted");
        client.close();
    }

    @Test
    @Order(11)
    public void testGetSseEvent() throws Exception {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(generateURL("/service/server-sent-events/events"));
        Response response = target.request().get();
        Assertions.assertEquals(response.getStatus(), 200, "response OK is expected");
        MediaType mt = response.getMediaType();
        mt = new MediaType(mt.getType(), mt.getSubtype());
        Assertions.assertEquals(mt, MediaType.SERVER_SENT_EVENTS_TYPE, "text/event-stream is expected");
        client.close();
    }

    @Test
    @Order(12)
    public void testNoReconnectAfterEventSinkClose() throws Exception {
        List<String> results = new ArrayList<String>();
        Client client = ((ResteasyClientBuilder) ClientBuilder.newBuilder()).connectionPoolSize(10).build();
        WebTarget target = client.target(generateURL("/service/server-sent-events/closeAfterSent"));
        SourceBuilder builder = (SourceBuilder) SseEventSource.target(target);
        SseEventSource sourceImpl = builder.alwaysReconnect(false).build();
        try (SseEventSource source = sourceImpl) {
            source.register(event -> results.add(event.readData()));
            source.open();
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            logger.info("Thread sleep interruped", e);
        }

        Assertions.assertEquals("[thing1, thing2, thing3]", results.toString(), "Received unexpected events");
        //test for [Resteasy-1863]:SseEventSourceImpl should not close Client instance
        results.clear();
        WebTarget target2 = client.target(generateURL("/service/server-sent-events/closeAfterSent"));
        SourceBuilder builder2 = (SourceBuilder) SseEventSource.target(target2);
        SseEventSource sourceImpl2 = builder2.alwaysReconnect(false).build();
        try (SseEventSource source = sourceImpl2) {
            source.register(event -> results.add(event.readData()));
            source.open();
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            logger.info("Thread sleep interruped", e);
        }
        Assertions.assertEquals("[thing1, thing2, thing3]", results.toString(), "Received unexpected events");
        client.close();
    }

    @Test
    @Order(13)
    public void testNoContent() throws Exception {
        Client client = ClientBuilder.newBuilder().build();
        final AtomicInteger errors = new AtomicInteger(0);
        WebTarget target = client.target(generateURL("/service/server-sent-events/noContent"));
        SourceBuilder builder = (SourceBuilder) SseEventSource.target(target);
        SseEventSource sourceImpl = builder.alwaysReconnect(false).build();
        try (SseEventSource source = sourceImpl) {
            source.register(event -> {
                logger.info(event);
            }, ex -> {
                logger.error("Error:", ex);
                errors.incrementAndGet();
            });
            source.open();
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            logger.info("Thread sleep interruped", e);
        }
        Assertions.assertEquals(0, errors.get(), "error is not expected");
        client.close();
    }

    //Test for RESTEASY-2689 which is reported in quarkus: https://github.com/quarkusio/quarkus/issues/11824
    @Test
    @Order(14)
    public void testBigMessage() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicInteger errors = new AtomicInteger(0);
        final List<String> results = new ArrayList<String>();
        Client client = ClientBuilder.newBuilder().build();
        WebTarget target = client.target(generateURL("/service/server-sent-events/bigmsg"));
        SseEventSource msgEventSource = SseEventSource.target(target).build();
        try (SseEventSource eventSource = msgEventSource) {
            Assertions.assertEquals(SseEventSourceImpl.class, eventSource.getClass());
            eventSource.register(event -> {
                results.add(event.readData());
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
        }
        Assertions.assertFalse(msgEventSource.isOpen(), "SseEventSource is not closed");
        Assertions.assertEquals(1, results.size(), () -> "1 messages are expected, but is : " + results.size());
        java.nio.file.Path filepath = Paths.get(SseTest.class.getResource("bigmsg.json").toURI());
        String bigMsg = new String(Files.readAllBytes(filepath));
        ObjectMapper om = new ObjectMapper();
        @SuppressWarnings("unchecked")
        Map<String, Object> m1 = (Map<String, Object>) (om.readValue(bigMsg, Map.class));
        @SuppressWarnings("unchecked")
        Map<String, Object> m2 = (Map<String, Object>) (om.readValue(results.get(0), Map.class));
        Assertions.assertEquals(m1, m2, "Unexpceted big size message");
        client.close();
    }

    //Test for https://issues.redhat.com/browse/RESTEASY-2695
    @Test
    @Order(15)
    public void testSetJsonType() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicInteger errors = new AtomicInteger(0);
        final List<String> results = new ArrayList<String>();
        Client client = ClientBuilder.newBuilder().build();
        WebTarget target = client.target(generateURL("/service/server-sent-events/json"));
        SseEventSource msgEventSource = SseEventSource.target(target).build();
        try (SseEventSource eventSource = msgEventSource) {
            Assertions.assertEquals(SseEventSourceImpl.class, eventSource.getClass());
            eventSource.register(event -> {
                results.add(event.readData());
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
        }
        Assertions.assertFalse(msgEventSource.isOpen(), "SseEventSource is not closed");
        Assertions.assertEquals(1, results.size(), () -> "1 messages are expected, but is : " + results.size());
        ObjectMapper om = new ObjectMapper();
        @SuppressWarnings("unchecked")
        Map<String, Object> m1 = (Map<String, Object>) (om.readValue(SseResource.jsonMessage, Map.class));
        @SuppressWarnings("unchecked")
        Map<String, Object> m2 = (Map<String, Object>) (om.readValue(results.get(0), Map.class));
        Assertions.assertEquals(m1, m2, "Unexpceted big size message");
        client.close();
    }
    //    @Test
    //    //This will open a browser and test with html sse client
    //    public void testHtmlSse() throws Exception
    //    {
    //
    //       Runtime runtime = Runtime.getRuntime();
    //       try
    //       {
    //          runtime.exec("xdg-open " + generateURL(""));
    //       }
    //       catch (IOException e)
    //       {
    //
    //       }
    //       Thread.sleep(30 * 1000);
    //    }
}
