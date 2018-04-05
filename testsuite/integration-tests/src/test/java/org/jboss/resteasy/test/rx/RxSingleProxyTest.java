package org.jboss.resteasy.test.rx;

import java.util.ArrayList;
import java.util.List;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.rxjava.SingleRxInvokerProvider;
import org.jboss.resteasy.test.rx.resource.RxScheduledExecutorService;
import org.jboss.resteasy.test.rx.resource.RxSingleResource;
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
public class RxSingleProxyTest {
   
   private static ResteasyClient client;
   private static RxSingleResource proxy;
   
   private static List<Thing>  xThingList =  new ArrayList<Thing>();
   private static List<Thing>  aThingList =  new ArrayList<Thing>();
   
   static {
      for (int i = 0; i < 3; i++) {xThingList.add(new Thing("x"));}
      for (int i = 0; i < 3; i++) {aThingList.add(new Thing("a"));}
   }

   @Deployment
   public static Archive<?> deploy() {
      WebArchive war = TestUtil.prepareArchive(RxSingleProxyTest.class.getSimpleName());
      war.addClass(Thing.class);
      war.addClass(RxScheduledExecutorService.class);
      war.addAsLibrary(TestUtil.resolveDependency("io.reactivex:rxjava:1.3.2"));
      war.addAsLibrary(TestUtil.resolveDependency("org.jboss.resteasy:resteasy-rxjava:4.0.0-SNAPSHOT"));
      return TestUtil.finishContainerPrepare(war, null, RxSingleResourceImpl.class);
   }

   private static String generateURL(String path) {
      return PortProviderUtil.generateURL(path, RxSingleProxyTest.class.getSimpleName());
   }

   //////////////////////////////////////////////////////////////////////////////
   @BeforeClass
   public static void beforeClass() throws Exception {
      client = new ResteasyClientBuilder().build();
      client.register(SingleRxInvokerProvider.class);
      proxy = client.target(generateURL("/")).proxy(RxSingleResource.class);
   }

   @AfterClass
   public static void after() throws Exception {
      client.close();
   }

   //////////////////////////////////////////////////////////////////////////////
   @Test
   public void testGet() throws Exception {
      Single<String> single = proxy.get();
      single.subscribe(
         (String s)-> Assert.assertEquals("x", s),
         (Throwable t) -> Assert.fail(t.getMessage()));
   }

   @Test
   public void testGetThing() throws Exception {
      Single<Thing> single = proxy.getThing();
      single.subscribe(
         (Thing t) -> Assert.assertEquals(new Thing("x"), t),
         (Throwable t) -> Assert.fail(t.getMessage()));
   }

   @Test
   public void testGetThingList() throws Exception {
      Single<List<Thing>> single = proxy.getThingList();
      single.subscribe(
         (List<Thing> l) -> Assert.assertEquals(xThingList, l),
         (Throwable t) -> Assert.fail(t.getMessage()));
   }

   @Test
   public void testPut() throws Exception {
      Single<String> single = proxy.put("a");
      single.subscribe(
         (String s)-> Assert.assertEquals("a", s),
         (Throwable t) -> Assert.fail(t.getMessage()));
   }

   @Test
   public void testPutThing() throws Exception {
      Single<Thing> single = proxy.putThing("a");
      single.subscribe(
         (Thing t) -> Assert.assertEquals(new Thing("a"), t),
         (Throwable t) -> Assert.fail(t.getMessage()));
   }

   @Test
   public void testPutThingList() throws Exception {
      Single<List<Thing>> single = proxy.putThingList("a");
      single.subscribe(
         (List<Thing> l) -> Assert.assertEquals(aThingList, l),
         (Throwable t) -> Assert.fail(t.getMessage()));
   }

   @Test
   public void testPost() throws Exception {
      Single<String> single = proxy.post("a");
      single.subscribe(
         (String s) -> Assert.assertEquals("a", s),
         (Throwable t) -> Assert.fail(t.getMessage()));
   }

   @Test
   public void testPostThing() throws Exception {
      Single<Thing> single = proxy.postThing("a");
      single.subscribe(
         (Thing t) -> Assert.assertEquals(new Thing("a"), t),
         (Throwable t) -> Assert.fail(t.getMessage()));
   }

   @Test
   public void testPostThingList() throws Exception {
      Single<List<Thing>> single = proxy.postThingList("a");
      single.subscribe(
         (List<Thing> l) -> Assert.assertEquals(aThingList, l),
         (Throwable t) -> Assert.fail(t.getMessage()));
   }

   @Test
   public void testDelete() throws Exception {
      Single<String> single = proxy.delete();
      single.subscribe(
         (String s) -> Assert.assertEquals("x", s),
         (Throwable t) -> Assert.fail(t.getMessage()));
   }

   @Test
   public void testDeleteThing() throws Exception {
      Single<Thing> single = proxy.deleteThing();
      single.subscribe(
         (Thing t) -> Assert.assertEquals(new Thing("x"), t),
         (Throwable t) -> Assert.fail(t.getMessage()));
   }

   @Test
   public void testDeleteThingList() throws Exception {
      Single<List<Thing>> single = proxy.deleteThingList();
      single.subscribe(
         (List<Thing> l) -> Assert.assertEquals(xThingList, l),
         (Throwable t) -> Assert.fail(t.getMessage()));
   }

   @Test
   public void testHead() throws Exception {
      Single<String> single = proxy.head();
      single.subscribe(
         (String s) -> System.out.println(s), // HEAD - no body
         (Throwable t) -> throwableContains(t, "Input stream was empty"));
   }

   @Test
   public void testOptions() throws Exception {
      Single<String> single = proxy.options();
      single.subscribe(
         (String s) -> Assert.assertEquals("x", s),
         (Throwable t) -> System.out.println("t: " + t.getMessage()));
   }

   @Test
   public void testOptionsThing() throws Exception {
      Single<Thing> single = proxy.optionsThing();
      single.subscribe(
         (Thing t) -> Assert.assertEquals(new Thing("x"), t),
         (Throwable t) -> Assert.fail(t.getMessage()));
   }

   @Test
   public void testOptionsThingList() throws Exception {
      Single<List<Thing>> single = proxy.optionsThingList();
      single.subscribe(
         (List<Thing> l) -> Assert.assertEquals(xThingList, l),
         (Throwable t) -> Assert.fail(t.getMessage()));
   }

   @Test
   @Ignore // TRACE is disabled by default in Wildfly
   public void testTrace() throws Exception {
      Single<String> single = proxy.trace();
      single.subscribe(
         (String s) -> Assert.assertEquals("x", s),
         (Throwable t) -> Assert.fail(t.getMessage()));
   }

   @Test
   @Ignore // TRACE is disabled by default in Wildfly
   public void testTraceThing() throws Exception {
      Single<Thing> single = proxy.traceThing();
      single.subscribe(
         (Thing t) -> Assert.assertEquals(new Thing("x"), t),
         (Throwable t) -> Assert.fail(t.getMessage()));
   }

   @Test
   @Ignore // TRACE is disabled by default in Wildfly
   public void testTraceThingList() throws Exception {
      Single<List<Thing>> single = proxy.traceThingList();
      single.subscribe(
         (List<Thing> l) -> Assert.assertEquals(xThingList, l),
         (Throwable t) -> Assert.fail(t.getMessage()));
   }
   
   @Test
   public void testScheduledExecutorService () throws Exception {
      {
         RxScheduledExecutorService.used = false;
         Single<String> single = proxy.get();
         single.subscribe(
            (String s) -> Assert.assertFalse(RxScheduledExecutorService.used),
            (Throwable t) -> Assert.fail(t.getMessage()));
      }

      {
         RxScheduledExecutorService.used = false;
         RxScheduledExecutorService executor = new RxScheduledExecutorService();
         ResteasyClient client = ((ResteasyClientBuilder) new ResteasyClientBuilder().executorService(executor)).build();
         client.register(SingleRxInvokerProvider.class);
         RxSingleResource proxy = client.target(generateURL("/")).proxy(RxSingleResource.class);
         Single<String> single = proxy.get();
         single.subscribe(
            (String s) -> Assert.assertTrue(RxScheduledExecutorService.used),
            (Throwable t) -> Assert.fail(t.getMessage()));
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