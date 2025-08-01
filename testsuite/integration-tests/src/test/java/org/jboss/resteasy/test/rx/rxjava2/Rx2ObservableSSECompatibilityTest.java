package org.jboss.resteasy.test.rx.rxjava2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.SseEventSource;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.plugins.providers.sse.client.SseEventSourceImpl;
import org.jboss.resteasy.rxjava2.ObservableRxInvoker;
import org.jboss.resteasy.test.rx.resource.Thing;
import org.jboss.resteasy.test.rx.rxjava2.resource.Rx2ObservableSSECompatibilityResource;
import org.jboss.resteasy.test.rx.rxjava2.resource.Rx2ObservableSSECompatibilityResourceImpl;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.MethodName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import io.reactivex.Observable;

/**
 * @tpSubChapter Reactive classes
 * @tpChapter Integration tests
 * @tpSince RESTEasy 4.0
 *
 *          These tests demonstrate compatibility between Rx and SSE clients and servers.
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
@TestMethodOrder(MethodName.class)
public class Rx2ObservableSSECompatibilityTest {

    private ResteasyClient client;
    private static final List<Thing> eThingList = new ArrayList<Thing>();
    private static ArrayList<Thing> thingList = new ArrayList<Thing>();

    static {
        for (int i = 0; i < 3; i++) {
            eThingList.add(new Thing("e" + (i + 1)));
        }
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(Rx2ObservableSSECompatibilityTest.class.getSimpleName());
        war.addClass(Thing.class);
        war.addClass(Rx2ObservableSSECompatibilityResource.class);
        war.setManifest(new StringAsset("Manifest-Version: 1.0\n"
                + "Dependencies: org.jboss.resteasy.resteasy-rxjava2 services\n"));
        return TestUtil.finishContainerPrepare(war, null, Rx2ObservableSSECompatibilityResourceImpl.class);
    }

    private static String generateURL(String path) {
        return PortProviderUtil.generateURL(path, Rx2ObservableSSECompatibilityTest.class.getSimpleName());
    }

    //////////////////////////////////////////////////////////////////////////////
    @BeforeAll
    public static void beforeClass() throws Exception {
    }

    @BeforeEach
    public void before() throws Exception {
        client = (ResteasyClient) ClientBuilder.newClient();
        thingList.clear();
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
    }

    //////////////////////////////////////////////////////////////////////////////

    @Test
    public void testSseToObservable() throws Exception {
        final CountDownLatch latch = new CountDownLatch(3);
        final AtomicInteger errors = new AtomicInteger(0);
        WebTarget target = client.target(generateURL("/observable/thing"));
        SseEventSource msgEventSource = SseEventSource.target(target).build();
        try (SseEventSource eventSource = msgEventSource) {
            eventSource.register(
                    event -> {
                        thingList.add(event.readData(Thing.class, MediaType.APPLICATION_JSON_TYPE));
                        latch.countDown();
                    },
                    ex -> errors.incrementAndGet());
            eventSource.open();

            boolean waitResult = latch.await(30, TimeUnit.SECONDS);
            Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
            Assertions.assertEquals(0, errors.get());
            Assertions.assertEquals(eThingList, thingList);
        }
    }

    @Test
    public void testSseToSse() throws Exception {
        final CountDownLatch latch = new CountDownLatch(3);
        final AtomicInteger errors = new AtomicInteger(0);
        WebTarget target = client.target(generateURL("/eventStream/thing"));
        SseEventSource msgEventSource = SseEventSource.target(target).build();
        try (SseEventSource eventSource = msgEventSource) {
            Assertions.assertEquals(SseEventSourceImpl.class, eventSource.getClass());
            eventSource.register(
                    event -> {
                        thingList.add(event.readData(Thing.class, MediaType.APPLICATION_JSON_TYPE));
                        latch.countDown();
                    },
                    ex -> errors.incrementAndGet());
            eventSource.open();

            boolean waitResult = latch.await(30, TimeUnit.SECONDS);
            Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
            Assertions.assertEquals(0, errors.get());
            Assertions.assertEquals(eThingList, thingList);
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testObservableToObservable() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        final AtomicInteger errors = new AtomicInteger(0);
        ObservableRxInvoker invoker = client.target(generateURL("/observable/thing")).request().rx(ObservableRxInvoker.class);
        Observable<Thing> observable = (Observable<Thing>) invoker.get(Thing.class);
        observable.subscribe(
                (Thing t) -> thingList.add(t),
                (Throwable t) -> errors.incrementAndGet(),
                () -> latch.countDown());
        boolean waitResult = latch.await(30, TimeUnit.SECONDS);
        Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
        Assertions.assertEquals(0, errors.get());
        Assertions.assertEquals(eThingList, thingList);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testObservableToSse() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        final AtomicInteger errors = new AtomicInteger(0);
        ObservableRxInvoker invoker = client.target(generateURL("/eventStream/thing")).request().rx(ObservableRxInvoker.class);
        Observable<Thing> observable = (Observable<Thing>) invoker.get(Thing.class);
        observable.subscribe(
                (Thing t) -> thingList.add(t),
                (Throwable t) -> errors.incrementAndGet(),
                () -> latch.countDown());
        boolean waitResult = latch.await(30, TimeUnit.SECONDS);
        Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
        Assertions.assertEquals(0, errors.get());
        Assertions.assertEquals(eThingList, thingList);
    }

    @Test
    public void testObservableToObservableProxy() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger errors = new AtomicInteger(0);
        Rx2ObservableSSECompatibilityResource proxy = client.target(generateURL("/"))
                .proxy(Rx2ObservableSSECompatibilityResource.class);
        Observable<Thing> observable = proxy.observableSSE();
        observable.subscribe(
                (Thing t) -> thingList.add(t),
                (Throwable t) -> errors.incrementAndGet(),
                () -> latch.countDown());
        boolean waitResult = latch.await(30, TimeUnit.SECONDS);
        Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
        Assertions.assertEquals(0, errors.get());
        Assertions.assertEquals(eThingList, thingList);
    }
}
