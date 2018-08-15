package org.jboss.resteasy.test.rx.rxjava;

import java.util.ArrayList;
import java.util.List;
import java.util.PropertyPermission;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.SseEventSource;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.plugins.providers.sse.client.SseEventSourceImpl;
import org.jboss.resteasy.rxjava.ObservableRxInvoker;
import org.jboss.resteasy.test.rx.resource.Thing;
import org.jboss.resteasy.test.rx.rxjava.resource.RxObservableSSECompatibilityResourceImpl;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.resteasy.utils.TestUtilRxJava;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import rx.Observable;


/**
 * @tpSubChapter Reactive classes
 * @tpChapter Integration tests
 * @tpSince RESTEasy 4.0
 * 
 * These tests demonstrate compatibility between Rx and SSE clients and servers.
 */
@SuppressWarnings("deprecation")
@RunWith(Arquillian.class)
@RunAsClient
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RxObservableSSECompatibilityTest {

   private static ResteasyClient client;
   private final static List<Thing>  eThingList =  new ArrayList<Thing>();
   private static ArrayList<Thing>  thingList = new ArrayList<Thing>();

   static {
      for (int i = 0; i < 3; i++) {eThingList.add(new Thing("e" + (i + 1)));}
   }

   @Deployment
   public static Archive<?> deploy() {
      WebArchive war = TestUtil.prepareArchive(RxObservableSSECompatibilityTest.class.getSimpleName());
      war.addClass(Thing.class);
      war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
              new PropertyPermission("*", "read,write")
              ),"permissions.xml");
      TestUtilRxJava.setupRxJava(war);
      return TestUtil.finishContainerPrepare(war, null, RxObservableSSECompatibilityResourceImpl.class);
   }

   private static String generateURL(String path) {
      return PortProviderUtil.generateURL(path, RxObservableSSECompatibilityTest.class.getSimpleName());
   }

   //////////////////////////////////////////////////////////////////////////////
   @BeforeClass
   public static void beforeClass() throws Exception {
   }

   @Before
   public void before() throws Exception {
      client = new ResteasyClientBuilder().build();
      thingList.clear();
   }

   @AfterClass
   public static void after() throws Exception {
      client.close();
   }

   //////////////////////////////////////////////////////////////////////////////

   @Test
   public void testSseToObservable() throws Exception {
      final CountDownLatch latch = new CountDownLatch(3);
      final AtomicInteger errors = new AtomicInteger(0);
      WebTarget target = client.target(generateURL("/observable/thing"));
      SseEventSource msgEventSource = SseEventSource.target(target).build();
      try (SseEventSource eventSource = msgEventSource)
      {
         eventSource.register(
            event -> {thingList.add(event.readData(Thing.class, MediaType.APPLICATION_JSON_TYPE)); latch.countDown();},
            ex -> errors.incrementAndGet());
         eventSource.open();

         boolean waitResult = latch.await(30, TimeUnit.SECONDS);
         Assert.assertEquals(0, errors.get());
         Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
         Assert.assertEquals(eThingList, thingList);
      }
   }

   @Test
   public void testSseToSse() throws Exception {
      final CountDownLatch latch = new CountDownLatch(3);
      final AtomicInteger errors = new AtomicInteger(0);
      WebTarget target = client.target(generateURL("/eventStream/thing"));
      SseEventSource msgEventSource = SseEventSource.target(target).build();
      try (SseEventSource eventSource = msgEventSource)
      {
         Assert.assertEquals(SseEventSourceImpl.class, eventSource.getClass());
         eventSource.register(
            event -> {thingList.add(event.readData(Thing.class, MediaType.APPLICATION_JSON_TYPE)); latch.countDown();},
            ex -> errors.incrementAndGet());
         eventSource.open();

         boolean waitResult = latch.await(30, TimeUnit.SECONDS);
         Assert.assertEquals(0, errors.get());
         Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
         Assert.assertEquals(eThingList, thingList);
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
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertEquals(0, errors.get());
      Assert.assertEquals(eThingList, thingList);
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
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertEquals(0, errors.get());
      Assert.assertEquals(eThingList, thingList);
   }
}