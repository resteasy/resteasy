package org.jboss.resteasy.test.rx;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import jakarta.ws.rs.client.ClientBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.internal.CompletionStageRxInvokerProvider;
import org.jboss.resteasy.test.rx.resource.RxCompletionStageResource;
import org.jboss.resteasy.test.rx.resource.RxCompletionStageResourceImpl;
import org.jboss.resteasy.test.rx.resource.RxScheduledExecutorService;
import org.jboss.resteasy.test.rx.resource.TRACE;
import org.jboss.resteasy.test.rx.resource.TestException;
import org.jboss.resteasy.test.rx.resource.TestExceptionMapper;
import org.jboss.resteasy.test.rx.resource.Thing;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Reactive classes
 * @tpChapter Integration tests
 * @tpSince RESTEasy 4.0
 *
 *          These tests run asynchronously on client, calling a proxy which calls a CompletionStageRxInvoker.
 *          The server creates and returns CompletionStages which run asynchronously.
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class RxCompletionStageProxyTest {

    private static ResteasyClient client;
    private static RxCompletionStageResource proxy;

    private static List<Thing> xThingList = new ArrayList<Thing>();
    private static List<Thing> aThingList = new ArrayList<Thing>();

    static {
        for (int i = 0; i < 3; i++) {
            xThingList.add(new Thing("x"));
        }
        for (int i = 0; i < 3; i++) {
            aThingList.add(new Thing("a"));
        }
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(RxCompletionStageProxyTest.class.getSimpleName());
        war.addClass(Thing.class);
        war.addClass(RxScheduledExecutorService.class);
        war.addClass(TestException.class);
        war.addClass(TRACE.class);
        war.setManifest(new StringAsset("Manifest-Version: 1.0\n"
                + "Dependencies: org.jboss.resteasy.resteasy-rxjava2 services\n"));
        return TestUtil.finishContainerPrepare(war, null, RxCompletionStageResourceImpl.class, TestExceptionMapper.class);
    }

    private static String generateURL(String path) {
        return PortProviderUtil.generateURL(path, RxCompletionStageProxyTest.class.getSimpleName());
    }

    //////////////////////////////////////////////////////////////////////////////
    @BeforeAll
    public static void beforeClass() throws Exception {
        client = (ResteasyClient) ClientBuilder.newClient();
        proxy = client.target(generateURL("/")).proxy(RxCompletionStageResource.class);
    }

    @AfterAll
    public static void after() throws Exception {
        client.close();
    }

    //////////////////////////////////////////////////////////////////////////////

    @Test
    public void testGet() throws Exception {
        CompletionStage<String> completionStage = proxy.get();
        Assertions.assertEquals("x", completionStage.toCompletableFuture().get());
    }

    @Test
    public void testGetThing() throws Exception {
        CompletionStage<Thing> completionStage = proxy.getThing();
        Assertions.assertEquals(new Thing("x"), completionStage.toCompletableFuture().get());
    }

    @Test
    public void testGetThingList() throws Exception {
        CompletionStage<List<Thing>> completionStage = proxy.getThingList();
        Assertions.assertEquals(xThingList, completionStage.toCompletableFuture().get());
    }

    @Test
    public void testPut() throws Exception {
        CompletionStage<String> completionStage = proxy.put("a");
        Assertions.assertEquals("a", completionStage.toCompletableFuture().get());
    }

    @Test
    public void testPutThing() throws Exception {
        CompletionStage<Thing> completionStage = proxy.putThing("a");
        Assertions.assertEquals(new Thing("a"), completionStage.toCompletableFuture().get());
    }

    @Test
    public void testPutThingList() throws Exception {
        CompletionStage<List<Thing>> completionStage = proxy.putThingList("a");
        Assertions.assertEquals(aThingList, completionStage.toCompletableFuture().get());
    }

    @Test
    public void testPost() throws Exception {
        CompletionStage<String> completionStage = proxy.post("a");
        Assertions.assertEquals("a", completionStage.toCompletableFuture().get());
    }

    @Test
    public void testPostThing() throws Exception {
        CompletionStage<Thing> completionStage = proxy.postThing("a");
        Assertions.assertEquals(new Thing("a"), completionStage.toCompletableFuture().get());
    }

    @Test
    public void testPostThingList() throws Exception {
        CompletionStage<List<Thing>> completionStage = proxy.postThingList("a");
        Assertions.assertEquals(aThingList, completionStage.toCompletableFuture().get());
    }

    @Test
    public void testDelete() throws Exception {
        CompletionStage<String> completionStage = proxy.delete();
        Assertions.assertEquals("x", completionStage.toCompletableFuture().get());
    }

    @Test
    public void testDeleteThing() throws Exception {
        CompletionStage<Thing> completionStage = proxy.deleteThing();
        Assertions.assertEquals(new Thing("x"), completionStage.toCompletableFuture().get());
    }

    @Test
    public void testDeleteThingList() throws Exception {
        CompletionStage<List<Thing>> completionStage = proxy.deleteThingList();
        Assertions.assertEquals(xThingList, completionStage.toCompletableFuture().get());
    }

    @Test
    public void testHead() throws Exception {
        CompletionStage<String> completionStage = proxy.head();
        try {
            completionStage.toCompletableFuture().get();
        } catch (Exception e) {
            Assertions.assertTrue(throwableContains(e, "Input stream was empty, there is no entity"));
        }
    }

    @Test
    public void testOptions() throws Exception {
        CompletionStage<String> completionStage = proxy.options();
        Assertions.assertEquals("x", completionStage.toCompletableFuture().get());
    }

    @Test
    public void testOptionsThing() throws Exception {
        CompletionStage<Thing> completionStage = proxy.optionsThing();
        Assertions.assertEquals(new Thing("x"), completionStage.toCompletableFuture().get());
    }

    @Test
    public void testOptionsThingList() throws Exception {
        CompletionStage<List<Thing>> completionStage = proxy.optionsThingList();
        Assertions.assertEquals(xThingList, completionStage.toCompletableFuture().get());
    }

    @Test
    public void testTrace() throws Exception {
        CompletionStage<String> completionStage = proxy.trace();
        Assertions.assertEquals("x", completionStage.toCompletableFuture().get());
    }

    @Test
    public void testTraceThing() throws Exception {
        CompletionStage<Thing> completionStage = proxy.traceThing();
        Assertions.assertEquals(new Thing("x"), completionStage.toCompletableFuture().get());
    }

    @Test
    public void testTraceThingList() throws Exception {
        CompletionStage<List<Thing>> completionStage = proxy.traceThingList();
        Assertions.assertEquals(xThingList, completionStage.toCompletableFuture().get());
    }

    @Test
    public void testScheduledExecutorService() throws Exception {
        {
            RxScheduledExecutorService.used = false;
            CompletionStage<String> completionStage = proxy.get();
            Assertions.assertEquals("x", completionStage.toCompletableFuture().get());
            Assertions.assertFalse(RxScheduledExecutorService.used);
        }

        {
            RxScheduledExecutorService.used = false;
            RxScheduledExecutorService executor = new RxScheduledExecutorService();
            ResteasyClient client = ((ResteasyClientBuilder) ClientBuilder.newBuilder()).executorService(executor).build();
            client.register(CompletionStageRxInvokerProvider.class);
            RxCompletionStageResource proxy = client.target(generateURL("/")).proxy(RxCompletionStageResource.class);
            CompletionStage<String> completionStage = proxy.get();
            Assertions.assertEquals("x", completionStage.toCompletableFuture().get());
            Assertions.assertTrue(RxScheduledExecutorService.used);
            client.close();
        }
    }

    @Test
    public void testUnhandledException() throws Exception {
        CompletionStage<Thing> completionStage = proxy.exceptionUnhandled();
        AtomicReference<Throwable> value = new AtomicReference<Throwable>();
        CountDownLatch latch = new CountDownLatch(1);
        completionStage.whenComplete((Thing t1, Throwable t2) -> {
            value.set(t2);
            latch.countDown();
        });
        boolean waitResult = latch.await(30, TimeUnit.SECONDS);
        Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
        Assertions.assertTrue(value.get().getMessage().contains("500"));
    }

    @Test
    public void testHandledException() throws Exception {
        CompletionStage<Thing> completionStage = proxy.exceptionHandled();
        AtomicReference<Throwable> value = new AtomicReference<Throwable>();
        CountDownLatch latch = new CountDownLatch(1);
        completionStage.whenComplete((Thing t1, Throwable t2) -> {
            value.set(t2);
            latch.countDown();
        });
        boolean waitResult = latch.await(30, TimeUnit.SECONDS);
        Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
        Assertions.assertTrue(value.get().getMessage().contains("444"));
    }

    @Test
    public void testGetTwoClients() throws Exception {
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();

        ResteasyClient client1 = (ResteasyClient) ClientBuilder.newClient();
        client1.register(CompletionStageRxInvokerProvider.class);
        RxCompletionStageResource proxy1 = client1.target(generateURL("/")).proxy(RxCompletionStageResource.class);
        CompletionStage<String> completionStage1 = proxy1.get();

        ResteasyClient client2 = (ResteasyClient) ClientBuilder.newClient();
        client2.register(CompletionStageRxInvokerProvider.class);
        RxCompletionStageResource proxy2 = client2.target(generateURL("/")).proxy(RxCompletionStageResource.class);
        CompletionStage<String> completionStage2 = proxy2.get();

        list.add(completionStage1.toCompletableFuture().get());
        list.add(completionStage2.toCompletableFuture().get());
        Assertions.assertEquals(2, list.size());
        for (int i = 0; i < 2; i++) {
            Assertions.assertEquals("x", list.get(i));
        }
        client1.close();
        client2.close();
    }

    @Test
    public void testGetTwoProxies() throws Exception {
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();

        RxCompletionStageResource proxy1 = client.target(generateURL("/")).proxy(RxCompletionStageResource.class);
        CompletionStage<String> completionStage1 = proxy1.get();

        RxCompletionStageResource proxy2 = client.target(generateURL("/")).proxy(RxCompletionStageResource.class);
        CompletionStage<String> completionStage2 = proxy2.get();

        list.add(completionStage1.toCompletableFuture().get());
        list.add(completionStage2.toCompletableFuture().get());
        Assertions.assertEquals(2, list.size());
        for (int i = 0; i < 2; i++) {
            Assertions.assertEquals("x", list.get(i));
        }
    }

    @Test
    public void testGetTwoCompletionStages() throws Exception {
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();

        CompletionStage<String> completionStage1 = proxy.get();
        CompletionStage<String> completionStage2 = proxy.get();

        list.add(completionStage1.toCompletableFuture().get());
        list.add(completionStage2.toCompletableFuture().get());
        Assertions.assertEquals(2, list.size());
        for (int i = 0; i < 2; i++) {
            Assertions.assertEquals("x", list.get(i));
        }
    }

    private static boolean throwableContains(Throwable t, String s) {
        while (t != null) {
            if (t.getMessage().contains(s)) {
                return true;
            }
            t = t.getCause();
        }
        return false;
    }
}
