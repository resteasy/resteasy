package org.jboss.resteasy.test.rx.rxjava2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.ResponseProcessingException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.test.rx.resource.Bytes;
import org.jboss.resteasy.test.rx.resource.RxScheduledExecutorService;
import org.jboss.resteasy.test.rx.resource.TRACE;
import org.jboss.resteasy.test.rx.resource.TestException;
import org.jboss.resteasy.test.rx.resource.TestExceptionMapper;
import org.jboss.resteasy.test.rx.resource.Thing;
import org.jboss.resteasy.test.rx.rxjava2.resource.Rx2ListNoStreamResource;
import org.jboss.resteasy.test.rx.rxjava2.resource.Rx2ObservableResourceNoStreamImpl;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.MethodName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Reactive classes
 * @tpChapter Integration tests
 * @tpSince RESTEasy 4.0
 *
 *          In these tests, the server uses Observables to build objects asynchronously, then collects the
 *          results and returns then in one transmission.
 *
 *          The client uses a proxy to make synchronous calls.
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
@TestMethodOrder(MethodName.class)
public class Rx2ObservableProxyServerAsyncTest {

    private static ResteasyClient client;
    private static Rx2ListNoStreamResource proxy;

    private static final List<String> xStringList = new ArrayList<String>();
    private static final List<String> aStringList = new ArrayList<String>();
    private static final List<Thing> xThingList = new ArrayList<Thing>();
    private static final List<Thing> aThingList = new ArrayList<Thing>();
    private static final List<List<Thing>> xThingListList = new ArrayList<List<Thing>>();
    private static final List<List<Thing>> aThingListList = new ArrayList<List<Thing>>();

    static {
        for (int i = 0; i < 3; i++) {
            xStringList.add("x");
        }
        for (int i = 0; i < 3; i++) {
            aStringList.add("a");
        }
        for (int i = 0; i < 3; i++) {
            xThingList.add(new Thing("x"));
        }
        for (int i = 0; i < 3; i++) {
            aThingList.add(new Thing("a"));
        }
        for (int i = 0; i < 2; i++) {
            xThingListList.add(xThingList);
        }
        for (int i = 0; i < 2; i++) {
            aThingListList.add(aThingList);
        }
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(Rx2ObservableProxyServerAsyncTest.class.getSimpleName())
                .addAsManifestResource(
                        // Required until WFLY-17051 is resolved
                        PermissionUtil.createPermissionsXmlAsset(PermissionUtil.addModuleFilePermission("org.eclipse.yasson")),
                        "permissions.xml");
        war.addClass(Thing.class);
        war.addClass(Bytes.class);
        war.addClass(TRACE.class);
        war.addClass(RxScheduledExecutorService.class);
        war.addClass(TestException.class);
        war.setManifest(new StringAsset("Manifest-Version: 1.0\n"
                + "Dependencies: org.jboss.resteasy.resteasy-rxjava2 services, org.jboss.resteasy.resteasy-json-binding-provider services\n"));
        return TestUtil.finishContainerPrepare(war, null, Rx2ObservableResourceNoStreamImpl.class, TestExceptionMapper.class);
    }

    private static String generateURL(String path) {
        return PortProviderUtil.generateURL(path, Rx2ObservableProxyServerAsyncTest.class.getSimpleName());
    }

    //////////////////////////////////////////////////////////////////////////////
    @BeforeAll
    public static void beforeClass() throws Exception {
        client = (ResteasyClient) ClientBuilder.newClient();
        proxy = client.target(generateURL("/")).proxy(Rx2ListNoStreamResource.class);
    }

    @BeforeEach
    public void before() throws Exception {
    }

    @AfterAll
    public static void after() throws Exception {
        client.close();
    }

    //////////////////////////////////////////////////////////////////////////////

    @Test
    public void testGet() throws Exception {
        List<String> list = proxy.get();
        Assertions.assertEquals(xStringList, list);
    }

    @Test
    public void testGetThing() throws Exception {
        List<Thing> list = proxy.getThing();
        Assertions.assertEquals(xThingList, list);
    }

    @Test
    public void testGetThingList() throws Exception {
        List<List<Thing>> list = proxy.getThingList();
        Assertions.assertEquals(xThingListList, list);
    }

    @Test
    public void testGetBytes() throws Exception {
        List<byte[]> list = proxy.getBytes();
        Assertions.assertEquals(3, list.size());
        for (byte[] b : list) {
            Assertions.assertTrue(Arrays.equals(Bytes.BYTES, b));
        }
    }

    @Test
    public void testPut() throws Exception {
        List<String> list = proxy.put("a");
        Assertions.assertEquals(aStringList, list);
    }

    @Test
    public void testPutThing() throws Exception {
        List<Thing> list = proxy.putThing("a");
        Assertions.assertEquals(aThingList, list);
    }

    @Test
    public void testPutThingList() throws Exception {
        List<List<Thing>> list = proxy.putThingList("a");
        Assertions.assertEquals(aThingListList, list);
    }

    @Test
    public void testPutBytes() throws Exception {
        List<byte[]> list = proxy.putBytes("3");
        Assertions.assertEquals(3, list.size());
        for (byte[] b : list) {
            Assertions.assertTrue(Arrays.equals(Bytes.BYTES, b));
        }
    }

    @Test
    public void testPost() throws Exception {
        List<String> list = proxy.post("a");
        Assertions.assertEquals(aStringList, list);
    }

    @Test
    public void testPostThing() throws Exception {
        List<Thing> list = proxy.postThing("a");
        Assertions.assertEquals(aThingList, list);
    }

    @Test
    public void testPostThingList() throws Exception {
        List<List<Thing>> list = proxy.postThingList("a");
        Assertions.assertEquals(aThingListList, list);
    }

    @Test
    public void testPostBytes() throws Exception {
        List<byte[]> list = proxy.postBytes("3");
        Assertions.assertEquals(3, list.size());
        for (byte[] b : list) {
            Assertions.assertTrue(Arrays.equals(Bytes.BYTES, b));
        }
    }

    @Test
    public void testDelete() throws Exception {
        List<String> list = proxy.delete();
        Assertions.assertEquals(xStringList, list);
    }

    @Test
    public void testDeleteThing() throws Exception {
        List<Thing> list = proxy.deleteThing();
        Assertions.assertEquals(xThingList, list);
    }

    @Test
    public void testDeleteThingList() throws Exception {
        List<List<Thing>> list = proxy.deleteThingList();
        Assertions.assertEquals(xThingListList, list);
    }

    @Test
    public void testDeleteBytes() throws Exception {
        List<byte[]> list = proxy.deleteBytes();
        Assertions.assertEquals(3, list.size());
        for (byte[] b : list) {
            Assertions.assertTrue(Arrays.equals(Bytes.BYTES, b));
        }
    }

    @Test
    public void testHead() throws Exception {
        try {
            List<String> list = proxy.head();
        } catch (ResponseProcessingException ex) {
            Assertions.assertTrue(ex.getMessage().contains("Input stream was empty"));
        }
    }

    @Test
    public void testOptions() throws Exception {
        List<String> list = proxy.options();
        Assertions.assertEquals(xStringList, list);
    }

    @Test
    public void testOptionsThing() throws Exception {
        List<Thing> list = proxy.optionsThing();
        Assertions.assertEquals(xThingList, list);
    }

    @Test
    public void testOptionsThingList() throws Exception {
        List<List<Thing>> list = proxy.optionsThingList();
        Assertions.assertEquals(xThingListList, list);
    }

    @Test
    public void testOptionsBytes() throws Exception {
        List<byte[]> list = proxy.optionsBytes();
        Assertions.assertEquals(3, list.size());
        for (byte[] b : list) {
            Assertions.assertTrue(Arrays.equals(Bytes.BYTES, b));
        }
    }

    @Test
    public void testTrace() throws Exception {
        List<String> list = proxy.trace();
        Assertions.assertEquals(xStringList, list);
    }

    @Test
    public void testTraceThing() throws Exception {
        List<Thing> list = proxy.traceThing();
        Assertions.assertEquals(xThingList, list);
    }

    @Test
    public void testTraceThingList() throws Exception {
        List<List<Thing>> list = proxy.traceThingList();
        Assertions.assertEquals(xThingListList, list);
    }

    @Test
    public void testTraceBytes() throws Exception {
        List<byte[]> list = proxy.traceBytes();
        Assertions.assertEquals(3, list.size());
        for (byte[] b : list) {
            Assertions.assertTrue(Arrays.equals(Bytes.BYTES, b));
        }
    }

    @Test
    public void testUnhandledException() throws Exception {
        try {
            proxy.exceptionUnhandled();
            Assertions.fail("expecting Exception");
        } catch (Exception e) {
            Assertions.assertEquals(InternalServerErrorException.class, e.getClass());
            Assertions.assertTrue(e.getMessage().contains("500"));
        }
    }

    @Test
    public void testHandledException() throws Exception {
        try {
            proxy.exceptionHandled();
            Assertions.fail("expecting Exception");
        } catch (Exception e) {
            Assertions.assertEquals(ClientErrorException.class, e.getClass());
            Assertions.assertTrue(e.getMessage().contains("444"));
        }
    }

    @Test
    public void testGetTwoClients() throws Exception {
        ResteasyClient client1 = (ResteasyClient) ClientBuilder.newClient();
        Rx2ListNoStreamResource proxy1 = client1.target(generateURL("/")).proxy(Rx2ListNoStreamResource.class);
        List<String> list1 = proxy1.get();

        ResteasyClient client2 = (ResteasyClient) ClientBuilder.newClient();
        Rx2ListNoStreamResource proxy2 = client2.target(generateURL("/")).proxy(Rx2ListNoStreamResource.class);
        List<String> list2 = proxy2.get();

        list1.addAll(list2);
        Assertions.assertEquals(6, list1.size());
        for (int i = 0; i < 6; i++) {
            Assertions.assertEquals("x", list1.get(i));
        }
        client1.close();
        client2.close();
    }

    @Test
    public void testGetTwoProxies() throws Exception {
        Rx2ListNoStreamResource proxy1 = client.target(generateURL("/")).proxy(Rx2ListNoStreamResource.class);
        List<String> list1 = proxy1.get();

        Rx2ListNoStreamResource proxy2 = client.target(generateURL("/")).proxy(Rx2ListNoStreamResource.class);
        List<String> list2 = proxy2.get();

        list1.addAll(list2);
        Assertions.assertEquals(6, list1.size());
        for (int i = 0; i < 6; i++) {
            Assertions.assertEquals("x", list1.get(i));
        }
    }

    @Test
    public void testGetTwoLists() throws Exception {
        List<String> list1 = proxy.get();
        List<String> list2 = proxy.get();

        list1.addAll(list2);
        Assertions.assertEquals(6, list1.size());
        for (int i = 0; i < 6; i++) {
            Assertions.assertEquals("x", list1.get(i));
        }
    }
}
