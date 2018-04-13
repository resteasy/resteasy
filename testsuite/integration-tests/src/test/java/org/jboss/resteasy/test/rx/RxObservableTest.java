package org.jboss.resteasy.test.rx;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.i18n.LogMessages;
import org.jboss.resteasy.rxjava.ObservableRxInvoker;
import org.jboss.resteasy.rxjava.ObservableRxInvokerProvider;
import org.jboss.resteasy.test.rx.resource.RxObservableResourceImpl;
import org.jboss.resteasy.test.rx.resource.RxScheduledExecutorService;
import org.jboss.resteasy.test.rx.resource.Thing;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
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
public class RxObservableTest {
   
   private static ResteasyClient client;

   private final static List<String> xStringList = new ArrayList<String>();
   private final static List<String> aStringList = new ArrayList<String>();	
   private final static List<Thing>  xThingList =  new ArrayList<Thing>();
   private final static List<Thing>  aThingList =  new ArrayList<Thing>();
   private final static List<List<Thing>> xThingListList = new ArrayList<List<Thing>>();
   private final static List<List<Thing>> aThingListList = new ArrayList<List<Thing>>();
   private final static Entity<String> aEntity = Entity.entity("a", MediaType.TEXT_PLAIN_TYPE);

   private static ArrayList<String> stringList = new ArrayList<String>();
   private static ArrayList<Thing>  thingList = new ArrayList<Thing>();
   private static ArrayList<List<?>> thingListList = new ArrayList<List<?>>();
   private static GenericType<List<Thing>> LIST_OF_THING = new GenericType<List<Thing>>() {};

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
      WebArchive war = TestUtil.prepareArchive(RxObservableTest.class.getSimpleName());
      war.addClass(Thing.class);
      war.addClass(RxScheduledExecutorService.class);
      war.addAsLibrary(TestUtil.resolveDependency("io.reactivex:rxjava:1.3.2"));
      war.addAsLibrary(TestUtil.resolveDependency("org.jboss.resteasy:resteasy-rxjava:4.0.0-SNAPSHOT"));
      return TestUtil.finishContainerPrepare(war, null, RxObservableResourceImpl.class);
   }


   @HttpMethod("TRACE")
   @Target(value = ElementType.METHOD)
   @Retention(value = RetentionPolicy.RUNTIME)
   public @interface TRACE {
   }

   private static String generateURL(String path) {
      return PortProviderUtil.generateURL(path, RxObservableTest.class.getSimpleName());
   }

   //////////////////////////////////////////////////////////////////////////////
   @BeforeClass
   public static void beforeClass() throws Exception {
      client = new ResteasyClientBuilder().build();
      client.register(ObservableRxInvokerProvider.class);
   }

   @Before
   public void before() throws Exception {
      stringList.clear();
      thingList.clear();
      thingListList.clear();
   }

   @AfterClass
   public static void after() throws Exception {
      client.close();
   }

   //////////////////////////////////////////////////////////////////////////////

   @SuppressWarnings("unchecked")
   @Test
   public void testGet() throws Exception {
      ObservableRxInvoker invoker = client.target(generateURL("/get/string")).request().rx(ObservableRxInvoker.class);
      Observable<String> observable = (Observable<String>) invoker.get();
      observable.subscribe(
         (String o) -> stringList.add(o),
         (Throwable t) -> Assert.fail(t.getMessage()),
         () -> Assert.assertEquals(xStringList, stringList));
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testGetThing() throws Exception {
      ObservableRxInvoker invoker = client.target(generateURL("/get/thing")).request().rx(ObservableRxInvoker.class);
      Observable<Thing> observable = (Observable<Thing>) invoker.get(Thing.class);
      observable.subscribe(
         (Thing o) -> thingList.add(o),
         (Throwable t) -> Assert.fail(t.getMessage()),
         () -> Assert.assertEquals(xThingList, thingList));
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testGetThingList() throws Exception {
      ObservableRxInvoker invoker = client.target(generateURL("/get/thing/list")).request().rx(ObservableRxInvoker.class);
      Observable<List<Thing>> observable = (Observable<List<Thing>>) invoker.get(LIST_OF_THING);
      observable.subscribe(
         (List<?> l) -> thingListList.add(l),
         (Throwable t) -> Assert.fail(t.getMessage()),
         () -> Assert.assertEquals(xThingListList, thingListList));
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testPut() throws Exception {
      ObservableRxInvoker invoker = client.target(generateURL("/put/string")).request().rx(ObservableRxInvoker.class);
      Observable<String> observable = (Observable<String>) invoker.put(aEntity);
      observable.subscribe(
         (String o) -> stringList.add(o),
         (Throwable t) -> Assert.fail(t.getMessage()),
         () -> Assert.assertEquals(aStringList, stringList));
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testPutThing() throws Exception {
      ObservableRxInvoker invoker = client.target(generateURL("/put/thing")).request().rx(ObservableRxInvoker.class);
      Observable<Thing> observable = (Observable<Thing>) invoker.put(aEntity, Thing.class);
      observable.subscribe(
         (Thing o) -> thingList.add(o),
         (Throwable t) -> Assert.fail(t.getMessage()),
         () -> Assert.assertEquals(aThingList, thingList));
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testPutThingList() throws Exception {
      ObservableRxInvoker invoker = client.target(generateURL("/put/thing/list")).request().rx(ObservableRxInvoker.class);
      Observable<List<Thing>> observable = (Observable<List<Thing>>) invoker.put(aEntity, LIST_OF_THING);
      observable.subscribe(
         (List<?> l) -> thingListList.add(l),
         (Throwable t) -> Assert.fail(t.getMessage()),
         () -> Assert.assertEquals(aThingListList, thingListList));
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testPost() throws Exception {
      LogMessages.LOGGER.error("testPost()");
      ObservableRxInvoker invoker = client.target(generateURL("/post/string")).request().rx(ObservableRxInvoker.class);
      Observable<String> observable = (Observable<String>) invoker.post(aEntity);
      observable.subscribe(
         (String o) -> stringList.add(o),
         (Throwable t) -> Assert.fail(t.getMessage()),
         () -> Assert.assertEquals(aStringList, stringList));
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testPostThing() throws Exception {
      ObservableRxInvoker invoker = client.target(generateURL("/post/thing")).request().rx(ObservableRxInvoker.class);
      Observable<Thing> observable = (Observable<Thing>) invoker.post(aEntity, Thing.class);
      observable.subscribe(
         (Thing o) -> thingList.add(o),
         (Throwable t) -> Assert.fail(t.getMessage()),
         () -> Assert.assertEquals(aThingList, thingList));
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testPostThingList() throws Exception {
      ObservableRxInvoker invoker = client.target(generateURL("/post/thing/list")).request().rx(ObservableRxInvoker.class);
      Observable<List<Thing>> observable = (Observable<List<Thing>>) invoker.post(aEntity, LIST_OF_THING);
      observable.subscribe(
         (List<?> l) -> thingListList.add(l),
         (Throwable t) -> Assert.fail(t.getMessage()),
         () -> Assert.assertEquals(aThingListList, thingListList));
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testDelete() throws Exception {
      ObservableRxInvoker invoker = client.target(generateURL("/delete/string")).request().rx(ObservableRxInvoker.class);
      Observable<String> observable = (Observable<String>) invoker.delete();
      observable.subscribe(
         (String o) -> stringList.add(o),
         (Throwable t) -> Assert.fail(t.getMessage()),
         () -> Assert.assertEquals(xStringList, stringList));
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testDeleteThing() throws Exception {
      ObservableRxInvoker invoker = client.target(generateURL("/delete/thing")).request().rx(ObservableRxInvoker.class);
      Observable<Thing> observable = (Observable<Thing>) invoker.delete(Thing.class);
      observable.subscribe(
         (Thing o) -> thingList.add(o),
         (Throwable t) -> Assert.fail(t.getMessage()),
         () -> Assert.assertEquals(xThingList, thingList));
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testDeleteThingList() throws Exception {
      ObservableRxInvoker invoker = client.target(generateURL("/delete/thing/list")).request().rx(ObservableRxInvoker.class);
      Observable<List<Thing>> observable = (Observable<List<Thing>>) invoker.delete(LIST_OF_THING);
      observable.subscribe(
         (List<?> l) -> thingListList.add(l),
         (Throwable t) -> Assert.fail(t.getMessage()),
         () -> Assert.assertEquals(xThingListList, thingListList));
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testHead() throws Exception {
      LogMessages.LOGGER.error("testHead()");
      ObservableRxInvoker invoker = client.target(generateURL("/head/string")).request().rx(ObservableRxInvoker.class);
      Observable<String> observable = (Observable<String>) invoker.head();
      observable.subscribe(
         (String s) -> System.out.println(s), // HEAD - no body
         (Throwable t) -> throwableContains(t, "Input stream was empty"));
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testOptions() throws Exception {
      ObservableRxInvoker invoker = client.target(generateURL("/options/string")).request().rx(ObservableRxInvoker.class);
      Observable<String> observable = (Observable<String>) invoker.options();
      observable.subscribe(
         (String o) -> stringList.add(o),
         (Throwable t) -> Assert.fail(t.getMessage()),
         () -> Assert.assertEquals(xStringList, stringList));
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testOptionsThing() throws Exception {
      ObservableRxInvoker invoker = client.target(generateURL("/options/thing")).request().rx(ObservableRxInvoker.class);
      Observable<Thing> observable = (Observable<Thing>) invoker.options(Thing.class);
      observable.subscribe(
         (Thing o) -> thingList.add(o),
         (Throwable t) -> Assert.fail(t.getMessage()),
         () -> Assert.assertEquals(xThingList, thingList));
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testOptionsThingList() throws Exception {
      ObservableRxInvoker invoker = client.target(generateURL("/options/thing/list")).request().rx(ObservableRxInvoker.class);
      Observable<List<Thing>> observable = (Observable<List<Thing>>) invoker.options(LIST_OF_THING);
      observable.subscribe(
         (List<?> l) -> thingListList.add(l),
         (Throwable t) -> Assert.fail(t.getMessage()),
         () -> Assert.assertEquals(xThingListList, thingListList));
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testTrace() throws Exception {
      ObservableRxInvoker invoker = client.target(generateURL("/trace/string")).request().rx(ObservableRxInvoker.class);
      Observable<String> observable = (Observable<String>) invoker.trace();
      observable.subscribe(
         (String o) -> stringList.add(o),
         (Throwable t) -> Assert.fail(t.getMessage()),
         () -> Assert.assertEquals(xStringList, stringList));
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testTraceThing() throws Exception {
      ObservableRxInvoker invoker = client.target(generateURL("/trace/thing")).request().rx(ObservableRxInvoker.class);
      Observable<Thing> observable = (Observable<Thing>) invoker.trace(Thing.class);
      observable.subscribe(
         (Thing o) -> thingList.add(o),
         (Throwable t) -> Assert.fail(t.getMessage()),
         () -> Assert.assertEquals(xThingList, thingList));
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testTraceThingList() throws Exception {
      ObservableRxInvoker invoker = client.target(generateURL("/trace/thing/list")).request().rx(ObservableRxInvoker.class);
      Observable<List<Thing>> observable = (Observable<List<Thing>>) invoker.trace(LIST_OF_THING);
      observable.subscribe(
         (List<?> l) -> thingListList.add(l),
         (Throwable t) -> Assert.fail(t.getMessage()),
         () -> Assert.assertEquals(xThingListList, thingListList));
   }


   @SuppressWarnings("unchecked")
   @Test
   public void testMethodGet() throws Exception {
      ObservableRxInvoker invoker = client.target(generateURL("/get/string")).request().rx(ObservableRxInvoker.class);
      Observable<String> observable = (Observable<String>) invoker.method("GET");
      observable.subscribe(
         (String o) -> stringList.add(o),
         (Throwable t) -> Assert.fail(t.getMessage()),
         () -> Assert.assertEquals(xStringList, stringList));
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testMethodGetThing() throws Exception {
      ObservableRxInvoker invoker = client.target(generateURL("/get/thing")).request().rx(ObservableRxInvoker.class);
      Observable<Thing> observable = (Observable<Thing>) invoker.method("GET", Thing.class);
      observable.subscribe(
         (Thing o) -> thingList.add(o),
         (Throwable t) -> Assert.fail(t.getMessage()),
         () -> Assert.assertEquals(xThingList, thingList));
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testMethodGetThingList() throws Exception {
      ObservableRxInvoker invoker = client.target(generateURL("/get/thing/list")).request().rx(ObservableRxInvoker.class);
      Observable<List<Thing>> observable = (Observable<List<Thing>>) invoker.method("GET", LIST_OF_THING);
      observable.subscribe(
         (List<?> l) -> thingListList.add(l),
         (Throwable t) -> Assert.fail(t.getMessage()),
         () -> Assert.assertEquals(xThingListList, thingListList));
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testMethodPost() throws Exception {
      ObservableRxInvoker invoker = client.target(generateURL("/post/string")).request().rx(ObservableRxInvoker.class);
      Observable<String> observable = (Observable<String>) invoker.method("POST", aEntity);
      observable.subscribe(
         (String o) -> stringList.add(o),
         (Throwable t) -> Assert.fail(t.getMessage()),
         () -> Assert.assertEquals(aStringList, stringList));
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testMethodPostThing() throws Exception {
      ObservableRxInvoker invoker = client.target(generateURL("/post/thing")).request().rx(ObservableRxInvoker.class);
      Observable<Thing> observable = (Observable<Thing>) invoker.method("POST", aEntity, Thing.class);
      observable.subscribe(
         (Thing o) -> thingList.add(o),
         (Throwable t) -> Assert.fail(t.getMessage()),
         () -> Assert.assertEquals(aThingList, thingList));
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testMethodPostThingList() throws Exception {
      ObservableRxInvoker invoker = client.target(generateURL("/post/thing/list")).request().rx(ObservableRxInvoker.class);
      Observable<List<Thing>> observable = (Observable<List<Thing>>) invoker.method("POST", aEntity, LIST_OF_THING);
      observable.subscribe(
         (List<?> l) -> thingListList.add(l),
         (Throwable t) -> Assert.fail(t.getMessage()),
         () -> Assert.assertEquals(aThingListList, thingListList));
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testScheduledExecutorService () throws Exception {
      {
         ObservableRxInvoker invoker = client.target(generateURL("/get/string")).request().rx(ObservableRxInvoker.class);
         Observable<String> observable = (Observable<String>) invoker.get();
         observable.subscribe(
            (String o) -> stringList.add(o),
            (Throwable t) -> Assert.fail(t.getMessage()),
            () -> {Assert.assertEquals(xStringList, stringList);
                   Assert.assertFalse(RxScheduledExecutorService.used);});
         
      }

      {
         RxScheduledExecutorService executor = new RxScheduledExecutorService();
         ResteasyClient client = ((ResteasyClientBuilder) new ResteasyClientBuilder().executorService(executor)).build();
         client.register(ObservableRxInvokerProvider.class);
         ObservableRxInvoker invoker = client.target(generateURL("/get/string")).request().rx(ObservableRxInvoker.class);
         Observable<String> observable = (Observable<String>) invoker.get();
         stringList.clear();
         observable.subscribe(
            (String o) -> stringList.add(o),
            (Throwable t) -> Assert.fail(t.getMessage()),
            () -> {Assert.assertEquals(xStringList, stringList);
                   Assert.assertTrue(RxScheduledExecutorService.used);});
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