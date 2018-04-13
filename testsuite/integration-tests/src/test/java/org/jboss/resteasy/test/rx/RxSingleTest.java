package org.jboss.resteasy.test.rx;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.rxjava.SingleRxInvoker;
import org.jboss.resteasy.rxjava.SingleRxInvokerProvider;
import org.jboss.resteasy.test.rx.resource.RxScheduledExecutorService;
import org.jboss.resteasy.test.rx.resource.RxSingleResourceImpl;
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

import rx.Single;


/**
 * @tpSubChapter Reactive classes
 * @tpChapter Integration tests
 * @tpSince RESTEasy 4.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class RxSingleTest {
   
   private static ResteasyClient client;
   private static List<Thing>  xThingList =  new ArrayList<Thing>();
   private static List<Thing>  aThingList =  new ArrayList<Thing>();
   private static Entity<String> aEntity = Entity.entity("a", MediaType.TEXT_PLAIN_TYPE);
   private static GenericType<List<Thing>> LIST_OF_THING = new GenericType<List<Thing>>() {};

   static {
      for (int i = 0; i < 3; i++) {xThingList.add(new Thing("x"));}
      for (int i = 0; i < 3; i++) {aThingList.add(new Thing("a"));}
   }

   @Deployment
   public static Archive<?> deploy() {
      WebArchive war = TestUtil.prepareArchive(RxSingleTest.class.getSimpleName());
      war.addClass(Thing.class);
      war.addClass(RxScheduledExecutorService.class);
      war.addAsLibrary(TestUtil.resolveDependency("io.reactivex:rxjava:1.3.2"));
      war.addAsLibrary(TestUtil.resolveDependency("org.jboss.resteasy:resteasy-rxjava:4.0.0-SNAPSHOT"));
      return TestUtil.finishContainerPrepare(war, null, RxSingleResourceImpl.class);
   }

   private static String generateURL(String path) {
      return PortProviderUtil.generateURL(path, RxSingleTest.class.getSimpleName());
   }

   //////////////////////////////////////////////////////////////////////////////
   @BeforeClass
   public static void beforeClass() throws Exception {
      client = new ResteasyClientBuilder().build();
      client.register(SingleRxInvokerProvider.class);
   }

   @AfterClass
   public static void after() throws Exception {
      client.close();
   }

   //////////////////////////////////////////////////////////////////////////////
   @Test
   public void testGet() throws Exception {
      SingleRxInvoker invoker = client.target(generateURL("/get/string")).request().rx(SingleRxInvoker.class);
      Single<Response> single = invoker.get();
      single.subscribe(
         (Response r) -> Assert.assertEquals("x", r.readEntity(String.class)),
         (Throwable t) -> Assert.fail(t.getMessage()));
   }

   @Test
   public void testGetThing() throws Exception {
      SingleRxInvoker invoker = client.target(generateURL("/get/thing")).request().rx(SingleRxInvoker.class);
      Single<Thing> single = invoker.get(Thing.class);
      single.subscribe(
         (Thing t) -> Assert.assertEquals(new Thing("x"), t),
         (Throwable t) -> Assert.fail(t.getMessage()));
   }

   @Test
   public void testGetThingList() throws Exception {
      SingleRxInvoker invoker = client.target(generateURL("/get/thing/list")).request().rx(SingleRxInvoker.class);
      Single<List<Thing>> single = invoker.get(LIST_OF_THING);
      single.subscribe(
         (List<Thing> l) -> Assert.assertEquals(xThingList, l),
         (Throwable t) -> Assert.fail(t.getMessage()));
   }

   @Test
   public void testPut() throws Exception {
      SingleRxInvoker invoker = client.target(generateURL("/put/string")).request().rx(SingleRxInvoker.class);
      Single<Response> single = invoker.put(aEntity);
      single.subscribe(
         (Response r) -> Assert.assertEquals("a", r.readEntity(String.class)),
         (Throwable t) -> Assert.fail(t.getMessage()));
   }

   @Test
   public void testPutThing() throws Exception {
      SingleRxInvoker invoker = client.target(generateURL("/put/thing")).request().rx(SingleRxInvoker.class);
      Single<Thing> single = invoker.put(aEntity, Thing.class);
      single.subscribe(
         (Thing t) -> Assert.assertEquals(new Thing("a"), t),
         (Throwable t) -> Assert.fail(t.getMessage()));
   }

   @Test
   public void testPutThingList() throws Exception {
      SingleRxInvoker invoker = client.target(generateURL("/put/thing/list")).request().rx(SingleRxInvoker.class);
      Single<List<Thing>> single = invoker.put(aEntity, LIST_OF_THING);
      single.subscribe(
         (List<Thing> l) -> Assert.assertEquals(aThingList, l),
         (Throwable t) -> Assert.fail(t.getMessage()));
   }

   @Test
   public void testPost() throws Exception {
      SingleRxInvoker invoker = client.target(generateURL("/post/string")).request().rx(SingleRxInvoker.class);
      Single<Response> single = invoker.post(aEntity);
      single.subscribe(
         (Response r) -> Assert.assertEquals("a", r.readEntity(String.class)),
         (Throwable t) -> Assert.fail(t.getMessage()));
   }

   @Test
   public void testPostThing() throws Exception {
      SingleRxInvoker invoker = client.target(generateURL("/post/thing")).request().rx(SingleRxInvoker.class);
      Single<Thing> single = invoker.post(aEntity, Thing.class);
      single.subscribe(
         (Thing t) -> Assert.assertEquals(new Thing("a"), t),
         (Throwable t) -> Assert.fail(t.getMessage()));
   }

   @Test
   public void testPostThingList() throws Exception {
      try {
      SingleRxInvoker invoker = client.target(generateURL("/post/thing/list")).request().rx(SingleRxInvoker.class);
      Single<List<Thing>> single = invoker.post(aEntity, LIST_OF_THING);
      single.subscribe(
         (List<Thing> l) -> Assert.assertEquals(aThingList, l),
         (Throwable t) -> Assert.fail(t.getMessage()));
      }catch (Exception e) {
         e.printStackTrace();
      }
   }

   @Test
   public void testDelete() throws Exception {
      SingleRxInvoker invoker = client.target(generateURL("/delete/string")).request().rx(SingleRxInvoker.class);
      Single<Response> single = invoker.delete();
      single.subscribe(
         (Response r) -> Assert.assertEquals("x", r.readEntity(String.class)),
         (Throwable t) -> Assert.fail(t.getMessage()));
   }

   @Test
   public void testDeleteThing() throws Exception {
      SingleRxInvoker invoker = client.target(generateURL("/delete/thing")).request().rx(SingleRxInvoker.class);
      Single<Thing> single = invoker.delete(Thing.class);
      single.subscribe(
         (Thing t) -> Assert.assertEquals(new Thing("x"), t),
         (Throwable t) -> Assert.fail(t.getMessage()));
   }

   @Test
   public void testDeleteThingList() throws Exception {
      SingleRxInvoker invoker = client.target(generateURL("/delete/thing/list")).request().rx(SingleRxInvoker.class);
      Single<List<Thing>> single = invoker.delete(LIST_OF_THING);
      single.subscribe(
         (List<Thing> l) -> Assert.assertEquals(xThingList, l),
         (Throwable t) -> Assert.fail(t.getMessage()));
   }

   @Test
   public void testHead() throws Exception {
      SingleRxInvoker invoker = client.target(generateURL("/head/string")).request().rx(SingleRxInvoker.class);
      Single<Response> single = invoker.head();
      single.subscribe(
         (Response r) -> Assert.assertFalse(r.hasEntity()), // HEAD - no body
         (Throwable t) -> Assert.fail(t.getMessage()));
   }

   @Test
   public void testOptions() throws Exception {
      SingleRxInvoker invoker = client.target(generateURL("/options/string")).request().rx(SingleRxInvoker.class);
      Single<Response> single = invoker.options();
      single.subscribe(
         (Response r) -> Assert.assertEquals("x", r.readEntity(String.class)),
         (Throwable t) -> Assert.fail(t.getMessage()));
   }

   @Test
   public void testOptionsThing() throws Exception {
      SingleRxInvoker invoker = client.target(generateURL("/options/thing")).request().rx(SingleRxInvoker.class);
      Single<Thing> single = invoker.options(Thing.class);
      single.subscribe(
         (Thing t) -> Assert.assertEquals(new Thing("x"), t),
         (Throwable t) -> Assert.fail(t.getMessage()));
   }

   @Test
   public void testOptionsThingList() throws Exception {
      SingleRxInvoker invoker = client.target(generateURL("/options/thing/list")).request().rx(SingleRxInvoker.class);
      Single<List<Thing>> single = invoker.options(LIST_OF_THING);
      single.subscribe(
         (List<Thing> l) -> Assert.assertEquals(xThingList, l),
         (Throwable t) -> Assert.fail(t.getMessage()));
   }

   @Test
   @Ignore // TRACE is disabled by default in Wildfly
   public void testTrace() throws Exception {
      SingleRxInvoker invoker = client.target(generateURL("/trace/string")).request().rx(SingleRxInvoker.class);
      Single<Response> single = invoker.trace();
      single.subscribe(
         (Response r) -> Assert.assertEquals("x", r.readEntity(String.class)),
         (Throwable t) -> Assert.fail(t.getMessage()));
   }

   @Test
   @Ignore // TRACE is disabled by default in Wildfly
   public void testTraceThing() throws Exception {
      SingleRxInvoker invoker = client.target(generateURL("/trace/thing")).request().rx(SingleRxInvoker.class);
      Single<Thing> single = invoker.trace(Thing.class);
      single.subscribe(
         (Thing t) -> Assert.assertEquals(new Thing("x"), t),
         (Throwable t) -> Assert.fail(t.getMessage()));
   }

   @Test
   @Ignore // TRACE is disabled by default in Wildfly
   public void testTraceThingList() throws Exception {
      SingleRxInvoker invoker = client.target(generateURL("/trace/thing/list")).request().rx(SingleRxInvoker.class);
      Single<List<Thing>> single = invoker.trace(LIST_OF_THING);
      single.subscribe(
         (List<Thing> l) -> Assert.assertEquals(xThingList, l),
         (Throwable t) -> Assert.fail(t.getMessage()));
   }

   @Test
   public void testMethodGet() throws Exception {
      SingleRxInvoker invoker = client.target(generateURL("/get/string")).request().rx(SingleRxInvoker.class);
      Single<Response> single = invoker.method("GET");
      single.subscribe(
         (Response r) -> Assert.assertEquals("x", r.readEntity(String.class)),
         (Throwable t) -> Assert.fail(t.getMessage()));
   }

   @Test
   public void testMethodGetThing() throws Exception {
      SingleRxInvoker invoker = client.target(generateURL("/get/thing")).request().rx(SingleRxInvoker.class);
      Single<Thing> single = invoker.method("GET", Thing.class);
      single.subscribe(
         (Thing t) -> Assert.assertEquals(new Thing("x"), t),
         (Throwable t) -> Assert.fail(t.getMessage()));
   }

   @Test
   public void testMethodGetThingList() throws Exception {
      SingleRxInvoker invoker = client.target(generateURL("/get/thing/list")).request().rx(SingleRxInvoker.class);
      Single<List<Thing>> single = invoker.method("GET", LIST_OF_THING);
      single.subscribe(
         (List<Thing> l) -> Assert.assertEquals(xThingList, l),
         (Throwable t) -> Assert.fail(t.getMessage()));
   }

   @Test
   public void testMethodPost() throws Exception {
      SingleRxInvoker invoker = client.target(generateURL("/post/string")).request().rx(SingleRxInvoker.class);
      Single<Response> single = invoker.method("POST", aEntity);
      single.subscribe(
         (Response r) -> Assert.assertEquals("a", r.readEntity(String.class)),
         (Throwable t) -> Assert.fail(t.getMessage()));
   }

   @Test
   public void testMethodPostThing() throws Exception {
      SingleRxInvoker invoker = client.target(generateURL("/post/thing")).request().rx(SingleRxInvoker.class);
      Single<Thing> single = invoker.method("POST", aEntity, Thing.class);
      single.subscribe(
         (Thing t) -> Assert.assertEquals(new Thing("a"), t),
         (Throwable t) -> Assert.fail(t.getMessage()));
   }

   @Test
   public void testMethodPostThingList() throws Exception {
      SingleRxInvoker invoker = client.target(generateURL("/post/thing/list")).request().rx(SingleRxInvoker.class);
      Single<List<Thing>> single = invoker.method("POST", aEntity, LIST_OF_THING);
      single.subscribe(
         (List<Thing> l) -> Assert.assertEquals(aThingList, l),
         (Throwable t) -> Assert.fail(t.getMessage()));
   }

   @Test
   public void testScheduledExecutorService () throws Exception {
      {
         RxScheduledExecutorService.used = false;
         SingleRxInvoker invoker = client.target(generateURL("/get/string")).request().rx(SingleRxInvoker.class);
         Single<Response> single = invoker.get();
         single.subscribe(
            (Response r) -> Assert.assertFalse(RxScheduledExecutorService.used),
            (Throwable t) -> Assert.fail(t.getMessage()));
      }

      {
         RxScheduledExecutorService.used = false;
         RxScheduledExecutorService executor = new RxScheduledExecutorService();
         ResteasyClient client = ((ResteasyClientBuilder) new ResteasyClientBuilder().executorService(executor)).build();
         client.register(SingleRxInvokerProvider.class);
         SingleRxInvoker invoker = client.target(generateURL("/get/string")).request().rx(SingleRxInvoker.class);
         Single<Response> single = invoker.get();
         single.subscribe(
            (Response r) -> Assert.assertTrue(RxScheduledExecutorService.used),
            (Throwable t) -> Assert.fail(t.getMessage()));
      }
   }
}