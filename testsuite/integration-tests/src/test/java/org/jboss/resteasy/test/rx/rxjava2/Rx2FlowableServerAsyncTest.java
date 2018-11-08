package org.jboss.resteasy.test.rx.rxjava2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.rx.resource.Bytes;
import org.jboss.resteasy.test.rx.resource.RxScheduledExecutorService;
import org.jboss.resteasy.test.rx.resource.TRACE;
import org.jboss.resteasy.test.rx.resource.TestException;
import org.jboss.resteasy.test.rx.resource.TestExceptionMapper;
import org.jboss.resteasy.test.rx.resource.Thing;
import org.jboss.resteasy.test.rx.rxjava2.resource.Rx2FlowableResourceNoStreamImpl;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;


/**
 * @tpSubChapter Reactive classes
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.6
 *
 * In these tests, the server uses Flowables to build objects asynchronously, then collects the
 * results and returns then in one transmission.
 *
 * The client makes synchronous calls.
 */
@RunWith(Arquillian.class)
@RunAsClient
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Rx2FlowableServerAsyncTest {

   private static ResteasyClient client;

   private static final List<String> xStringList = new ArrayList<String>();
   private static final List<String> aStringList = new ArrayList<String>();
   private static final List<Thing>  xThingList =  new ArrayList<Thing>();
   private static final List<Thing>  aThingList =  new ArrayList<Thing>();
   private static final List<List<Thing>> xThingListList = new ArrayList<List<Thing>>();
   private static final List<List<Thing>> aThingListList = new ArrayList<List<Thing>>();
   private static final Entity<String> aEntity = Entity.entity("a", MediaType.TEXT_PLAIN_TYPE);
   private static final Entity<String> threeEntity = Entity.entity("3", MediaType.TEXT_PLAIN_TYPE);

   private static ArrayList<String> stringList = new ArrayList<String>();
   private static ArrayList<Thing>  thingList = new ArrayList<Thing>();
   private static ArrayList<List<?>> thingListList = new ArrayList<List<?>>();
   private static ArrayList<byte[]> bytesList = new ArrayList<byte[]>();
   private static GenericType<List<String>> LIST_OF_STRING = new GenericType<List<String>>() {};
   private static GenericType<List<Thing>> LIST_OF_THING = new GenericType<List<Thing>>() {};
   private static GenericType<List<List<Thing>>> LIST_OF_LIST_OF_THING = new GenericType<List<List<Thing>>>() {};
   private static GenericType<List<byte[]>> LIST_OF_BYTE_ARRAYS = new GenericType<List<byte[]>>() {};

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
      WebArchive war = TestUtil.prepareArchive(Rx2FlowableServerAsyncTest.class.getSimpleName());
      war.addClass(Thing.class);
      war.addClass(Bytes.class);
      war.addClass(TRACE.class);
      war.addClass(RxScheduledExecutorService.class);
      war.addClass(TestException.class);
      war.setManifest(new StringAsset("Manifest-Version: 1.0\n"
         + "Dependencies: org.jboss.resteasy.resteasy-rxjava2 services, org.jboss.resteasy.resteasy-json-binding-provider services\n"));
      return TestUtil.finishContainerPrepare(war, null, Rx2FlowableResourceNoStreamImpl.class, TestExceptionMapper.class);
   }

   private static String generateURL(String path) {
      return PortProviderUtil.generateURL(path, Rx2FlowableServerAsyncTest.class.getSimpleName());
   }

   //////////////////////////////////////////////////////////////////////////////
   @BeforeClass
   public static void beforeClass() throws Exception {
      client = new ResteasyClientBuilder().build();
   }

   @Before
   public void before() throws Exception {
      stringList.clear();
      thingList.clear();
      thingListList.clear();
      bytesList.clear();
   }

   @AfterClass
   public static void after() throws Exception {
      client.close();
   }

   //////////////////////////////////////////////////////////////////////////////

   @Test
   public void testGet() throws Exception {
      Builder request = client.target(generateURL("/get/string")).request();
      Response response = request.get();
      Assert.assertEquals(xStringList, response.readEntity(LIST_OF_STRING));
   }

   @Test
   public void testGetThing() throws Exception {
      Builder request = client.target(generateURL("/get/thing")).request();
      List<Thing> list = request.get(LIST_OF_THING);
      Assert.assertEquals(xThingList, list);
   }

   @Test
   public void testGetThingList() throws Exception {
      Builder request = client.target(generateURL("/get/thing/list")).request();
      List<List<Thing>> list = request.get(LIST_OF_LIST_OF_THING);
      Assert.assertEquals(xThingListList, list);
   }

   @Test
   public void testGetBytes() throws Exception {
      Builder request = client.target(generateURL("/get/bytes")).request();
      List<byte[]> list = request.get(LIST_OF_BYTE_ARRAYS);
      Assert.assertEquals(3, list.size());
      for (byte[] b : list) {
         Assert.assertTrue(Arrays.equals(Bytes.BYTES, b));
      }
   }

   @Test
   public void testPut() throws Exception {
      Builder request = client.target(generateURL("/put/string")).request();
      Response response = request.put(aEntity);
      Assert.assertEquals(aStringList, response.readEntity(LIST_OF_STRING));
   }

   @Test
   public void testPutThing() throws Exception {
      Builder request = client.target(generateURL("/put/thing")).request();
      List<Thing> list = request.put(aEntity, LIST_OF_THING);
      Assert.assertEquals(aThingList, list);
   }

   @Test
   public void testPutThingList() throws Exception {
      Builder request = client.target(generateURL("/put/thing/list")).request();
      List<List<Thing>> list = request.put(aEntity, LIST_OF_LIST_OF_THING);
      Assert.assertEquals(aThingListList, list);
   }

   @Test
   public void testPutBytes() throws Exception {
      Builder request = client.target(generateURL("/put/bytes")).request();
      List<byte[]> list = request.put(threeEntity, LIST_OF_BYTE_ARRAYS);
      Assert.assertEquals(3, list.size());
      for (byte[] b : list) {
         Assert.assertTrue(Arrays.equals(Bytes.BYTES, b));
      }
   }

   @Test
   public void testPost() throws Exception {
      Builder request = client.target(generateURL("/post/string")).request();
      Response response = request.post(aEntity);
      Assert.assertEquals(aStringList, response.readEntity(LIST_OF_STRING));
   }

   @Test
   public void testPostThing() throws Exception {
      Builder request = client.target(generateURL("/post/thing")).request();
      List<Thing> list = request.post(aEntity, LIST_OF_THING);
      Assert.assertEquals(aThingList, list);
   }

   @Test
   public void testPostThingList() throws Exception {
      Builder request = client.target(generateURL("/post/thing/list")).request();
      List<List<Thing>> list = request.post(aEntity, LIST_OF_LIST_OF_THING);
      Assert.assertEquals(aThingListList, list);
   }

   @Test
   public void testPostBytes() throws Exception {
      Builder request = client.target(generateURL("/post/bytes")).request();
      List<byte[]> list = request.post(threeEntity, LIST_OF_BYTE_ARRAYS);
      Assert.assertEquals(3, list.size());
      for (byte[] b : list) {
         Assert.assertTrue(Arrays.equals(Bytes.BYTES, b));
      }
   }

   @Test
   public void testDelete() throws Exception {
      Builder request = client.target(generateURL("/delete/string")).request();
      Response response = request.delete();
      Assert.assertEquals(xStringList, response.readEntity(LIST_OF_STRING));
   }

   @Test
   public void testDeleteThing() throws Exception {
      Builder request = client.target(generateURL("/delete/thing")).request();
      List<Thing> list = request.delete(LIST_OF_THING);
      Assert.assertEquals(xThingList, list);
   }

   @Test
   public void testDeleteThingList() throws Exception {
      Builder request = client.target(generateURL("/delete/thing/list")).request();
      List<List<Thing>> list = request.delete(LIST_OF_LIST_OF_THING);
      Assert.assertEquals(xThingListList, list);
   }

   @Test
   public void testDeleteBytes() throws Exception {
      Builder request = client.target(generateURL("/delete/bytes")).request();
      List<byte[]> list = request.delete(LIST_OF_BYTE_ARRAYS);
      Assert.assertEquals(3, list.size());
      for (byte[] b : list) {
         Assert.assertTrue(Arrays.equals(Bytes.BYTES, b));
      }
   }

   @Test
   public void testHead() throws Exception {
      Builder request = client.target(generateURL("/head/string")).request();
      Response response = request.head();
      Assert.assertEquals(200, response.getStatus());
   }

   @Test
   public void testOptions() throws Exception {
      Builder request = client.target(generateURL("/options/string")).request();
      Response response = request.options();
      Assert.assertEquals(xStringList, response.readEntity(LIST_OF_STRING));
   }

   @Test
   public void testOptionsThing() throws Exception {
      Builder request = client.target(generateURL("/options/thing")).request();
      List<Thing> list = request.options(LIST_OF_THING);
      Assert.assertEquals(xThingList, list);
   }

   @Test
   public void testOptionsThingList() throws Exception {
      Builder request = client.target(generateURL("/options/thing/list")).request();
      List<List<Thing>> list = request.options(LIST_OF_LIST_OF_THING);
      Assert.assertEquals(xThingListList, list);
   }

   @Test
   public void testOptionsBytes() throws Exception {
      Builder request = client.target(generateURL("/options/bytes")).request();
      List<byte[]> list = request.options(LIST_OF_BYTE_ARRAYS);
      Assert.assertEquals(3, list.size());
      for (byte[] b : list) {
         Assert.assertTrue(Arrays.equals(Bytes.BYTES, b));
      }
   }

   @Test
   @Ignore // TRACE turned off by default in Wildfly
   public void testTrace() throws Exception {
      Builder request = client.target(generateURL("/trace/string")).request();
      Response response = request.trace();
      Assert.assertEquals(xStringList, response.readEntity(LIST_OF_STRING));
   }

   @Test
   @Ignore // TRACE turned off by default in Wildfly
   public void testTraceThing() throws Exception {
      Builder request = client.target(generateURL("/trace/thing")).request();
      List<Thing> list = request.trace(LIST_OF_THING);
      Assert.assertEquals(xThingList, list);
   }

   @Test
   @Ignore // TRACE turned off by default in Wildfly
   public void testTraceThingList() throws Exception {
      Builder request = client.target(generateURL("/trace/thing/list")).request();
      List<List<Thing>> list = request.trace(LIST_OF_LIST_OF_THING);
      Assert.assertEquals(xThingListList, list);
   }

   @Test
   @Ignore // TRACE turned off by default in Wildfly
   public void testTraceBytes() throws Exception {
      Builder request = client.target(generateURL("/trace/bytes")).request();
      List<byte[]> list = request.trace(LIST_OF_BYTE_ARRAYS);
      Assert.assertEquals(3, list.size());
      for (byte[] b : list) {
         Assert.assertTrue(Arrays.equals(Bytes.BYTES, b));
      }
   }

   @Test
   public void testMethodGet() throws Exception {
      Builder request = client.target(generateURL("/get/string")).request();
      Response response = request.method("GET");
      Assert.assertEquals(xStringList, response.readEntity(LIST_OF_STRING));
   }

   @Test
   public void testMethodGetThing() throws Exception {
      Builder request = client.target(generateURL("/get/thing")).request();
      List<Thing> list = request.method("GET", LIST_OF_THING);
      Assert.assertEquals(xThingList, list);
   }

   @Test
   public void testMethodGetThingList() throws Exception {
      Builder request = client.target(generateURL("/get/thing/list")).request();
      List<List<Thing>> list = request.method("GET", LIST_OF_LIST_OF_THING);
      Assert.assertEquals(xThingListList, list);
   }

   @Test
   public void testMethodGetBytes() throws Exception {
      Builder request = client.target(generateURL("/get/bytes")).request();
      List<byte[]> list = request.method("GET", LIST_OF_BYTE_ARRAYS);
      Assert.assertEquals(3, list.size());
      for (byte[] b : list) {
         Assert.assertTrue(Arrays.equals(Bytes.BYTES, b));
      }
   }

   @Test
   public void testMethodPost() throws Exception {
      Builder request = client.target(generateURL("/post/string")).request();
      Response response = request.method("POST", aEntity);
      Assert.assertEquals(aStringList, response.readEntity(LIST_OF_STRING));
   }

   @Test
   public void testMethodPostThing() throws Exception {
      Builder request = client.target(generateURL("/post/thing")).request();
      List<Thing> list = request.method("POST", aEntity, LIST_OF_THING);
      Assert.assertEquals(aThingList, list);
   }

   @Test
   public void testMethodPostThingList() throws Exception {
      Builder request = client.target(generateURL("/post/thing/list")).request();
      List<List<Thing>> list = request.method("POST", aEntity, LIST_OF_LIST_OF_THING);
      Assert.assertEquals(aThingListList, list);
   }

   @Test
   public void testMethodPostBytes() throws Exception {
      Builder request = client.target(generateURL("/post/bytes")).request();
      List<byte[]> list = request.method("POST", threeEntity, LIST_OF_BYTE_ARRAYS);
      Assert.assertEquals(3, list.size());
      for (byte[] b : list) {
         Assert.assertTrue(Arrays.equals(Bytes.BYTES, b));
      }
   }

   @Test
   public void testUnhandledException() throws Exception {
      Builder request = client.target(generateURL("/exception/unhandled")).request();
      try
      {
         request.get(Thing.class);
         Assert.fail("expecting Exception");
      } catch (Exception e) {
         Assert.assertEquals(InternalServerErrorException.class, e.getClass());
         Assert.assertTrue(e.getMessage().contains("500"));
      }
   }

   @Test
   public void testHandledException() throws Exception {
      Builder request = client.target(generateURL("/exception/handled")).request();
      try
      {
         request.get(Thing.class);
         Assert.fail("expecting Exception");
      } catch (Exception e) {
         Assert.assertEquals(ClientErrorException.class, e.getClass());
         Assert.assertTrue(e.getMessage().contains("444"));
      }
   }

   @Test
   public void testGetTwoClients() throws Exception {
      ResteasyClient client1 = new ResteasyClientBuilder().build();
      Builder request1 = client1.target(generateURL("/get/string")).request();
      Response response1 = request1.get();
      List<String> list1 = response1.readEntity(LIST_OF_STRING);

      ResteasyClient client2 = new ResteasyClientBuilder().build();
      Builder request2 = client2.target(generateURL("/get/string")).request();
      Response response2 = request2.get();
      List<String> list2 = response2.readEntity(LIST_OF_STRING);

      list1.addAll(list2);
      Assert.assertEquals(6, list1.size());
      for (int i = 0; i < 6; i++) {
         Assert.assertEquals("x", list1.get(i));
      }
   }

   @Test
   public void testGetTwoRequests() throws Exception {
      Builder request1 = client.target(generateURL("/get/string")).request();
      Response response1 = request1.get();
      List<String> list1 = response1.readEntity(LIST_OF_STRING);

      Builder request2 = client.target(generateURL("/get/string")).request();
      Response response2 = request2.get();
      List<String> list2 = response2.readEntity(LIST_OF_STRING);

      list1.addAll(list2);
      Assert.assertEquals(6, list1.size());
      for (int i = 0; i < 6; i++) {
         Assert.assertEquals("x", list1.get(i));
      }
   }

   @Test
   public void testGetTwoLists() throws Exception {
      Builder request = client.target(generateURL("/get/string")).request();
      Response response1 = request.get();
      List<String> list1 = response1.readEntity(LIST_OF_STRING);

      Response response2 = request.get();
      List<String> list2 = response2.readEntity(LIST_OF_STRING);

      list1.addAll(list2);
      Assert.assertEquals(6, list1.size());
      for (int i = 0; i < 6; i++) {
         Assert.assertEquals("x", list1.get(i));
      }
   }
}
