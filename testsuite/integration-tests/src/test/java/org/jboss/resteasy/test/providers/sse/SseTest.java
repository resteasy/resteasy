package org.jboss.resteasy.test.providers.sse;

import java.net.http.HttpClient;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.plugins.providers.sse.client.SseEventSourceImpl;
import org.jboss.resteasy.plugins.providers.sse.client.SseEventSourceImpl.SourceBuilder;
import org.jboss.resteasy.spi.config.Options;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(Arquillian.class)
@RunAsClient
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
        war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
                new RuntimePermission("modifyThread")), "permissions.xml");
        return TestUtil.finishContainerPrepare(war, null, SseApplication.class, SseResource.class,
                AnotherSseResource.class, EscapingSseResource.class, ExecutorServletContextListener.class);

    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, SseTest.class.getSimpleName());
    }

    @Test
    @InSequence(1)
    public void testAddMessage() throws Exception {
        final CountDownLatch latch = new CountDownLatch(5);
        final AtomicInteger errors = new AtomicInteger(0);
        final List<String> results = new ArrayList<String>();
        final List<String> sent = new ArrayList<String>();
        Client client = ClientBuilder.newBuilder().build();
        WebTarget target = client.target(generateURL("/service/server-sent-events"));
        SseEventSource msgEventSource = SseEventSource.target(target).build();
        try (SseEventSource eventSource = msgEventSource) {
            Assert.assertEquals(SseEventSourceImpl.class, eventSource.getClass());
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
            Assert.assertEquals(0, errors.get());
            Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
            messageTarget.request().delete();
            messageClient.close();
        }
        Assert.assertFalse("SseEventSource is not closed", msgEventSource.isOpen());
        Assert.assertTrue("5 messages are expected, but is : " + results.size(), results.size() == 5);
        for (String s : sent) {
            Assert.assertTrue("Sent message \"" + s + "\" not found as result.", results.contains(s));
        }
        client.close();
    }

    //Test for Last-Event-Id. This test uses the message items stores in testAddMessage()
    @Test
    @InSequence(2)
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
        Assert.assertTrue(
                "Waiting for missed events to be delivered has timed our, received events :"
                        + Arrays.toString(missedEvents.toArray(new String[] {})),
                missedEventLatch.await(30, TimeUnit.SECONDS));
        Assert.assertTrue("3 messages are expected, but is : " + missedEvents.toArray(new String[] {}),
                missedEvents.size() == 3);
        lastEventTarget.request().delete();
        lastEventSource.close();
        c.close();
    }

    @Test
    @InSequence(3)
    public void testSseEvent() throws Exception {
        final List<String> results = new ArrayList<String>();
        final CountDownLatch latch = new CountDownLatch(6);
        final AtomicInteger errors = new AtomicInteger(0);
        Client client = ((ResteasyClientBuilder) ClientBuilder.newBuilder()).connectionPoolSize(10).build();
        WebTarget target = client.target(generateURL("/service/server-sent-events")).path("domains").path("1");

        SseEventSource eventSource = SseEventSource.target(target).build();
        Assert.assertEquals(SseEventSourceImpl.class, eventSource.getClass());
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
        Assert.assertEquals(0, errors.get());
        Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
        Assert.assertTrue("6 SseInboundEvent expected", results.size() == 6);
        Assert.assertTrue("Expect the last event is Done event, but it is :" + results.toArray(new String[] {})[5],
                results.toArray(new String[] {})[5].indexOf("Done") > -1);
        eventSource.close();
        client.close();
    }

    @Test
    @InSequence(4)
    public void testBroadcast() throws Exception {
        final CountDownLatch latch = new CountDownLatch(3);
        Client client = ((ResteasyClientBuilder) ClientBuilder.newBuilder()).build();
        WebTarget target = client.target(generateURL("/service/server-sent-events/subscribe"));
        final String textMessage = "This is broadcast message";
        Consumer<InboundSseEvent> checkConsumer = insse -> {
            if (latch.getCount() > 0) {
                Assert.assertTrue("Unexpected sever sent event data", textMessage.equals(insse.readData()));
            }
            latch.countDown();
        };
        SseEventSource eventSource = SseEventSource.target(target).build();
        Assert.assertEquals(SseEventSourceImpl.class, eventSource.getClass());
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
        Assert.assertTrue("Waiting for broadcast event to be delivered has timed out.", latch.await(20, TimeUnit.SECONDS));

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
        Assert.assertTrue("Waiting for repeatable broadcast events to be delivered has timed out.",
                latch3.await(20, TimeUnit.SECONDS));
        Assert.assertTrue("Waiting for repeatable broadcast events to be delivered has timed out.",
                latch4.await(20, TimeUnit.SECONDS));

        client.close();
        CountDownLatch latch5 = new CountDownLatch(5);
        CountDownLatch latch6 = new CountDownLatch(5);

        eventSource.register(insse -> {
            latch5.countDown();
        });
        eventSource2.register(insse -> {
            latch6.countDown();
        });

        Assert.assertTrue("Eventsource should not receive event after close", latch5.getCount() == 5);
        Assert.assertTrue("Waiting for eventsource2 receive broadcast events to be delivered has timed out.",
                latch6.await(20, TimeUnit.SECONDS));

        client2.target(generateURL("/service/server-sent-events/broadcast")).request()
                .post(Entity.entity("close one subscriber", MediaType.SERVER_SENT_EVENTS));

        Client closeClient = ClientBuilder.newClient();
        WebTarget closeTarget = closeClient.target(generateURL("/service/sse"));
        Assert.assertTrue("Subscribed eventsink is not closed", closeTarget.request().delete().readEntity(Boolean.class));
        eventSource2.close();
        client2.close();
        closeClient.close();
    }

    //This test is checking SseEventSource reconnect ability. When request post /addMessageAndDisconnect path, server will
    //disconnect the connection, but events is continued to add to eventsStore. SseEventSource will automatically reconnect
    //with LastEventId and receive the missed events
    @Test
    @InSequence(5)
    public void testReconnect() throws Exception {
        int proxyPort = 9090;
        SimpleProxyServer proxy = new SimpleProxyServer(PortProviderUtil.getHost(), PortProviderUtil.getPort(), proxyPort);
        proxy.start();
        int maxWaits = 30;
        while (!proxy.isStarted()) {
            Assert.assertTrue(maxWaits-- > 0);
            logger.info("Proxy not started yet, sleeping 100ms");
            Thread.sleep(100);
        }
        final CountDownLatch latch = new CountDownLatch(10);
        final List<String> results = new ArrayList<String>();
        final AtomicInteger errors = new AtomicInteger(0);
        // TODO (jrp) this needs to be looked at as it only seems to work with HTTP/1.1
        Client client = ((ResteasyClientBuilder) ClientBuilder.newBuilder()).connectionPoolSize(10)
                .property(Options.HTTP_CLIENT_VERSION.name(), HttpClient.Version.HTTP_1_1).build();
        String requestPath = PortProviderUtil.generateURL("/service/server-sent-events",
                SseTest.class.getSimpleName(), PortProviderUtil.getHost(), proxyPort);
        WebTarget target = client.target(requestPath);
        try (SseEventSource eventSource = SseEventSource.target(target).reconnectingEvery(500, TimeUnit.MILLISECONDS).build()) {
            Assert.assertEquals(SseEventSourceImpl.class, eventSource.getClass());
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
            Assert.assertEquals(0, errors.get());
            Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
            Assert.assertTrue("10 events are expected, but is : " + results.size(), results.size() == 10);
            target.request().delete();
            proxy.stop();
        }
        client.close();
    }

    @Test
    @InSequence(6)
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
        Assert.assertFalse("InternalServerErrorException isn't processed in error consumer", errorList.isEmpty());
        client.close();
    }

    @Test
    @InSequence(7)
    public void testMultipleDataFields() throws Exception {
        final CountDownLatch latch = new CountDownLatch(7);
        final AtomicInteger errors = new AtomicInteger(0);
        final SortedSet<String> results = new TreeSet<String>();
        Client client = ClientBuilder.newBuilder().build();
        WebTarget target = client.target(generateURL("/service/server-sent-events"));
        SseEventSource msgEventSource = SseEventSource.target(target).build();
        try (SseEventSource eventSource = msgEventSource) {
            Assert.assertEquals(SseEventSourceImpl.class, eventSource.getClass());
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
            Assert.assertEquals(0, errors.get());
            Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
            messageTarget.request().delete();
            messageClient.close();
        }
        Assert.assertFalse("SseEventSource is not closed", msgEventSource.isOpen());
        Assert.assertTrue("5 messages are expected, but is : " + results.size(), results.size() == 7);
        String[] lines = results.toArray(new String[] {})[1].split("\n");
        Assert.assertTrue("3 data fields are expected, but is : " + lines.length, lines.length == 3);
        Assert.assertEquals("expect second data field value is : " + lines[1], "data1b", lines[1]);
        client.close();
    }

    @Test
    @InSequence(8)
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
            Assert.assertEquals(SseEventSourceImpl.class, eventSource.getClass());
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
            Assert.assertEquals(0, errors.get());
            Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
        }
        Assert.assertFalse("SseEventSource is not closed", msgEventSource.isOpen());
        Assert.assertTrue("3 messages are expected, but is : " + results.size(), results.size() == 3);
        for (String s : sent) {
            Assert.assertTrue("Sent message \"" + s + "\" not found as result.", results.contains(s));
        }
        client.close();
    }

    @Test
    @InSequence(10)
    public void testXmlEvent() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicInteger errors = new AtomicInteger(0);
        final List<InboundSseEvent> results = new ArrayList<InboundSseEvent>();
        Client client = ClientBuilder.newBuilder().build();
        WebTarget target = client.target(generateURL("/service/server-sent-events/xmlevent"));
        SseEventSource msgEventSource = SseEventSource.target(target).build();
        try (SseEventSource eventSource = msgEventSource) {
            Assert.assertEquals(SseEventSourceImpl.class, eventSource.getClass());
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
            Assert.assertEquals(0, errors.get());
            Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
        }
        JAXBElement<String> jaxbElement = results.get(0).readData(new jakarta.ws.rs.core.GenericType<JAXBElement<String>>() {
        }, MediaType.APPLICATION_XML_TYPE);
        Assert.assertEquals("xmldata is expceted", jaxbElement.getValue(), "xmldata");
        client.close();
    }

    @Test
    @InSequence(11)
    public void testGetSseEvent() throws Exception {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(generateURL("/service/server-sent-events/events"));
        Response response = target.request().get();
        Assert.assertEquals("response OK is expected", response.getStatus(), 200);
        MediaType mt = response.getMediaType();
        mt = new MediaType(mt.getType(), mt.getSubtype());
        Assert.assertEquals("text/event-stream is expected", mt, MediaType.SERVER_SENT_EVENTS_TYPE);
        client.close();
    }

    @Test
    @InSequence(12)
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

        Assert.assertEquals("Received unexpected events", "[thing1, thing2, thing3]", results.toString());
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
        Assert.assertEquals("Received unexpected events", "[thing1, thing2, thing3]", results.toString());
        client.close();
    }

    @Test
    @InSequence(13)
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
        Assert.assertTrue("error is not expected", errors.get() == 0);
        client.close();
    }

    //Test for RESTEASY-2689 which is reported in quarkus: https://github.com/quarkusio/quarkus/issues/11824
    @Test
    @InSequence(14)
    public void testBigMessage() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicInteger errors = new AtomicInteger(0);
        final List<String> results = new ArrayList<String>();
        Client client = ClientBuilder.newBuilder().build();
        WebTarget target = client.target(generateURL("/service/server-sent-events/bigmsg"));
        SseEventSource msgEventSource = SseEventSource.target(target).build();
        try (SseEventSource eventSource = msgEventSource) {
            Assert.assertEquals(SseEventSourceImpl.class, eventSource.getClass());
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
            Assert.assertEquals(0, errors.get());
            Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
        }
        Assert.assertFalse("SseEventSource is not closed", msgEventSource.isOpen());
        Assert.assertTrue("1 messages are expected, but is : " + results.size(), results.size() == 1);
        java.nio.file.Path filepath = Paths.get(SseTest.class.getResource("bigmsg.json").toURI());
        String bigMsg = new String(Files.readAllBytes(filepath));
        ObjectMapper om = new ObjectMapper();
        @SuppressWarnings("unchecked")
        Map<String, Object> m1 = (Map<String, Object>) (om.readValue(bigMsg, Map.class));
        @SuppressWarnings("unchecked")
        Map<String, Object> m2 = (Map<String, Object>) (om.readValue(results.get(0), Map.class));
        Assert.assertTrue("Unexpceted big size message", m1.equals(m2));
        client.close();
    }

    //Test for https://issues.redhat.com/browse/RESTEASY-2695
    @Test
    @InSequence(15)
    public void testSetJsonType() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicInteger errors = new AtomicInteger(0);
        final List<String> results = new ArrayList<String>();
        Client client = ClientBuilder.newBuilder().build();
        WebTarget target = client.target(generateURL("/service/server-sent-events/json"));
        SseEventSource msgEventSource = SseEventSource.target(target).build();
        try (SseEventSource eventSource = msgEventSource) {
            Assert.assertEquals(SseEventSourceImpl.class, eventSource.getClass());
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
            Assert.assertEquals(0, errors.get());
            Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
        }
        Assert.assertFalse("SseEventSource is not closed", msgEventSource.isOpen());
        Assert.assertTrue("1 messages are expected, but is : " + results.size(), results.size() == 1);
        ObjectMapper om = new ObjectMapper();
        @SuppressWarnings("unchecked")
        Map<String, Object> m1 = (Map<String, Object>) (om.readValue(SseResource.jsonMessage, Map.class));
        @SuppressWarnings("unchecked")
        Map<String, Object> m2 = (Map<String, Object>) (om.readValue(results.get(0), Map.class));
        Assert.assertTrue("Unexpceted big size message", m1.equals(m2));
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
