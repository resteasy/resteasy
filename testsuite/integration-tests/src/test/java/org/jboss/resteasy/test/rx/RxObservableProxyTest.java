package org.jboss.resteasy.test.rx;

import java.util.ArrayList;
import java.util.List;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.rxjava.ObservableRxInvokerProvider;
import org.jboss.resteasy.test.rx.resource.RxObservableResource;
import org.jboss.resteasy.test.rx.resource.RxObservableResourceImpl;
import org.jboss.resteasy.test.rx.resource.RxScheduledExecutorService;
import org.jboss.resteasy.test.rx.resource.Thing;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import rx.Observable;


/**
 * @tpSubChapter Reactive classes
 * @tpChapter Integration tests
 * @tpSince RESTEasy 4.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class RxObservableProxyTest {
   
   private static ResteasyClient client;
   private static RxObservableResource proxy;
   
   private static List<String> stringList = new ArrayList<String>();
   private static List<Thing>  thingList = new ArrayList<Thing>();
   private static List<List<Thing>> thingListList = new ArrayList<List<Thing>>();
   
   private final static List<String> xStringList = new ArrayList<String>();
   private final static List<String> aStringList = new ArrayList<String>();
   private final static List<Thing>  xThingList =  new ArrayList<Thing>();
   private final static List<Thing>  aThingList =  new ArrayList<Thing>();
   private final static List<List<Thing>> xThingListList = new ArrayList<List<Thing>>();
   private final static List<List<Thing>> aThingListList = new ArrayList<List<Thing>>();
   
   static {
      for (int i = 0; i < 3; i++) {xStringList.add("x");}
      for (int i = 0; i < 3; i++) {aStringList.add("a");}
      for (int i = 0; i < 3; i++) {xThingList.add(new Thing("x"));}
      for (int i = 0; i < 3; i++) {aThingList.add(new Thing("a"));}
      for (int i = 0; i < 2; i++) {xThingListList.add(xThingList);}
      for (int i = 0; i < 2; i++) {aThingListList.add(aThingList);}
   }

   @Deployment
   public static Archive<?> deploy() {
      WebArchive war = TestUtil.prepareArchive(RxObservableProxyTest.class.getSimpleName());
      war.addClass(Thing.class);
      war.addClass(RxScheduledExecutorService.class);
      war.addAsLibrary(TestUtil.resolveDependency("io.reactivex:rxjava:1.3.2"));
      war.addAsLibrary(TestUtil.resolveDependency("org.jboss.resteasy:resteasy-rxjava:4.0.0-SNAPSHOT"));
      return TestUtil.finishContainerPrepare(war, null, RxObservableResourceImpl.class);
   }

   private static String generateURL(String path) {
      return PortProviderUtil.generateURL(path, RxObservableProxyTest.class.getSimpleName());
   }

   //////////////////////////////////////////////////////////////////////////////
   @BeforeClass
   public static void beforeClass() throws Exception {
      client = new ResteasyClientBuilder().build();
      client.register(ObservableRxInvokerProvider.class);
      proxy = client.target(generateURL("/")).proxy(RxObservableResource.class);
   }

   @AfterClass
   public static void after() throws Exception {
      client.close();
   }

   //////////////////////////////////////////////////////////////////////////////
   @Test
   public void testGet() throws Exception {
      Observable<String> observable = proxy.get();
      observable.subscribe((String o) -> stringList.add(o));
      observable.subscribe(
         (String o) -> stringList.add(o),
         (Throwable t) -> Assert.fail(t.getMessage()),
         () -> Assert.assertEquals(xStringList, stringList));
   }

   @Test
   public void testGetThing() throws Exception {
      Observable<Thing> observable = proxy.getThing();
      observable.subscribe(
         (Thing o) -> thingList.add(o),
         (Throwable t) -> Assert.fail(t.getMessage()),
         () -> Assert.assertEquals(xThingList, thingList));
   }

   @Test
   public void testGetThingList() throws Exception {
      Observable<List<Thing>> observable = proxy.getThingList();
      observable.subscribe(
         (List<Thing> l) -> thingListList.add(l),
         (Throwable t) -> Assert.fail(t.getMessage()),
         () -> Assert.assertEquals(xThingListList, thingListList));
   }

   @Test
   public void testPut() throws Exception {
      Observable<String> observable = proxy.put("a");
      observable.subscribe(
         (String o) -> stringList.add(o),
         (Throwable t) -> Assert.fail(t.getMessage()),
         () -> Assert.assertEquals(aStringList, stringList));
   }

   @Test
   public void testPutThing() throws Exception {
      Observable<Thing> observable = proxy.putThing("a");
      observable.subscribe(
         (Thing o) -> thingList.add(o),
         (Throwable t) -> Assert.fail(t.getMessage()),
         () -> Assert.assertEquals(aThingList, thingList));
   }

   @Test
   public void testPutThingList() throws Exception {
      Observable<List<Thing>> observable = proxy.putThingList("a");
      observable.subscribe(
         (List<Thing> l) -> thingListList.add(l),
         (Throwable t) -> Assert.fail(t.getMessage()),
         () -> Assert.assertEquals(aThingListList, thingListList));
   }

   @Test
   public void testPost() throws Exception {
      Observable<String> observable = proxy.post("a");
      observable.subscribe(
         (String o) -> stringList.add(o),
         (Throwable t) -> Assert.fail(t.getMessage()),
         () -> Assert.assertEquals(aStringList, stringList));
   }

   @Test
   public void testPostThing() throws Exception {
      Observable<Thing> observable = proxy.postThing("a");
      observable.subscribe(
         (Thing o) -> thingList.add(o),
         (Throwable t) -> Assert.fail(t.getMessage()),
         () -> Assert.assertEquals(aThingList, thingList));
   }

   @Test
   public void testPostThingList() throws Exception {
      Observable<List<Thing>> observable = proxy.postThingList("a");
      observable.subscribe(
         (List<Thing> l) -> thingListList.add(l),
         (Throwable t) -> Assert.fail(t.getMessage()),
         () -> Assert.assertEquals(aThingListList, thingListList));
   }

   @Test
   public void testDelete() throws Exception {
      Observable<String> observable = proxy.delete();
      observable.subscribe(
         (String o) -> stringList.add(o),
         (Throwable t) -> Assert.fail(t.getMessage()),
         () -> Assert.assertEquals(xStringList, stringList));
   }

   @Test
   public void testDeleteThing() throws Exception {
      Observable<Thing> observable = proxy.deleteThing();
      observable.subscribe(
         (Thing o) -> thingList.add(o),
         (Throwable t) -> Assert.fail(t.getMessage()),
         () -> Assert.assertEquals(xThingList, thingList));
   }
   
   @Test
   public void testDeleteThingList() throws Exception {
      Observable<List<Thing>> observable = proxy.deleteThingList();
      observable.subscribe(
         (List<Thing> l) -> thingListList.add(l),
         (Throwable t) -> Assert.fail(t.getMessage()),
         () -> Assert.assertEquals(xThingListList, thingListList));
   }

   @Test
   public void testHead() throws Exception {
      Observable<String> observable = proxy.head();
      observable.subscribe(
         (String s) -> System.out.println(s), // HEAD - no body
         (Throwable t) -> throwableContains(t, "Input stream was empty"));
   }
   
   @Test
   public void testOptions() throws Exception {
      Observable<String> observable = proxy.options();
      observable.subscribe(
         (String o) -> stringList.add(o),
         (Throwable t) -> Assert.fail(t.getMessage()),
         () -> Assert.assertEquals(xStringList, stringList));
   }

   @Test
   public void testOptionsThing() throws Exception {
      Observable<Thing> observable = proxy.optionsThing();
      observable.subscribe(
         (Thing o) -> thingList.add(o),
         (Throwable t) -> Assert.fail(t.getMessage()),
         () -> Assert.assertEquals(xThingList, thingList));
   }

   @Test
   public void testOptionsThingList() throws Exception {
      Observable<List<Thing>> observable = proxy.optionsThingList();
      observable.subscribe(
         (List<Thing> l) -> thingListList.add(l),
         (Throwable t) -> Assert.fail(t.getMessage()),
         () -> Assert.assertEquals(xThingListList, thingListList));
   }
   
   @Test
   @Ignore // TRACE is disabled by default in Wildfly
   public void testTrace() throws Exception {
      Observable<String> observable = proxy.trace();
      observable.subscribe(
         (String o) -> stringList.add(o),
         (Throwable t) -> Assert.fail(t.getMessage()),
         () -> Assert.assertEquals(xStringList, stringList));
   }

   @Test
   @Ignore // TRACE is disabled by default in Wildfly
   public void testTraceThing() throws Exception {
      Observable<Thing> observable = proxy.traceThing();
      observable.subscribe(
         (Thing o) -> thingList.add(o),
         (Throwable t) -> Assert.fail(t.getMessage()),
         () -> Assert.assertEquals(xThingList, thingList));
   }

   @Test
   @Ignore // TRACE is disabled by default in Wildfly
   public void testTraceThingList() throws Exception {
      Observable<List<Thing>> observable = proxy.traceThingList();
      observable.subscribe(
         (List<Thing> l) -> thingListList.add(l),
         (Throwable t) -> Assert.fail(t.getMessage()),
         () -> Assert.assertEquals(xThingListList, thingListList));
   }
   
   @Test
   public void testScheduledExecutorService () throws Exception {
      {
         Observable<String> observable = proxy.get();
         observable.subscribe(
            (String o) -> stringList.add(o),
            (Throwable t) -> Assert.fail(t.getMessage()),
            () -> Assert.assertFalse(RxScheduledExecutorService.used));
      }
      
      {
         RxScheduledExecutorService executor = new RxScheduledExecutorService();
         ResteasyClient client = ((ResteasyClientBuilder) new ResteasyClientBuilder().executorService(executor)).build();
         client.register(ObservableRxInvokerProvider.class);
         RxObservableResource proxy = client.target(generateURL("/")).proxy(RxObservableResource.class);
         Observable<String> observable = proxy.get();
         observable.subscribe(
            (String o) -> stringList.add(o),
            (Throwable t) -> Assert.fail(t.getMessage()),
            () -> Assert.assertTrue(RxScheduledExecutorService.used));
      }
   }
   
   private static boolean throwableContains(Throwable t, String s) {
      while (t != null) {
         if (t.getMessage().contains(s))
         {
            return true;
         }
         t = t.getCause();
      }
      return false;
   }
}