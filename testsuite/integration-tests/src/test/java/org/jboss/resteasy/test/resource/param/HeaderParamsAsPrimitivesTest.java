package org.jboss.resteasy.test.resource.param;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.resource.param.resource.HeaderParamsAsPrimitivesArrayDefaultNullProxy;
import org.jboss.resteasy.test.resource.param.resource.HeaderParamsAsPrimitivesArrayDefaultOverrideProxy;
import org.jboss.resteasy.test.resource.param.resource.HeaderParamsAsPrimitivesArrayDefaultProxy;
import org.jboss.resteasy.test.resource.param.resource.HeaderParamsAsPrimitivesArrayProxy;
import org.jboss.resteasy.test.resource.param.resource.HeaderParamsAsPrimitivesDefaultNullProxy;
import org.jboss.resteasy.test.resource.param.resource.HeaderParamsAsPrimitivesDefaultOverrideProxy;
import org.jboss.resteasy.test.resource.param.resource.HeaderParamsAsPrimitivesDefaultProxy;
import org.jboss.resteasy.test.resource.param.resource.HeaderParamsAsPrimitivesListDefaultNullProxy;
import org.jboss.resteasy.test.resource.param.resource.HeaderParamsAsPrimitivesListDefaultOverrideProxy;
import org.jboss.resteasy.test.resource.param.resource.HeaderParamsAsPrimitivesListDefaultProxy;
import org.jboss.resteasy.test.resource.param.resource.HeaderParamsAsPrimitivesListProxy;
import org.jboss.resteasy.test.resource.param.resource.HeaderParamsAsPrimitivesPrimitivesProxy;
import org.jboss.resteasy.test.resource.param.resource.HeaderParamsAsPrimitivesResourceArray;
import org.jboss.resteasy.test.resource.param.resource.HeaderParamsAsPrimitivesResourceArrayDefault;
import org.jboss.resteasy.test.resource.param.resource.HeaderParamsAsPrimitivesResourceArrayDefaultNull;
import org.jboss.resteasy.test.resource.param.resource.HeaderParamsAsPrimitivesResourceArrayDefaultOverride;
import org.jboss.resteasy.test.resource.param.resource.HeaderParamsAsPrimitivesResourceList;
import org.jboss.resteasy.test.resource.param.resource.HeaderParamsAsPrimitivesResourceListDefault;
import org.jboss.resteasy.test.resource.param.resource.HeaderParamsAsPrimitivesResourceListDefaultNull;
import org.jboss.resteasy.test.resource.param.resource.HeaderParamsAsPrimitivesResourceListDefaultOverride;
import org.jboss.resteasy.test.resource.param.resource.HeaderParamsAsPrimitivesResourceSet;
import org.jboss.resteasy.test.resource.param.resource.HeaderParamsAsPrimitivesResourceWrappers;
import org.jboss.resteasy.test.resource.param.resource.HeaderParamsAsPrimitivesResourceWrappersDefault;
import org.jboss.resteasy.test.resource.param.resource.HeaderParamsAsPrimitivesResourceWrappersDefaultNull;
import org.jboss.resteasy.test.resource.param.resource.HeaderParamsAsPrimitivesResourceWrappersDefaultOverride;
import org.jboss.resteasy.test.resource.param.resource.HeaderParamsAsPrimitivesResourcePrimitives;
import org.jboss.resteasy.test.resource.param.resource.HeaderParamsAsPrimitivesResourceDefault;
import org.jboss.resteasy.test.resource.param.resource.HeaderParamsAsPrimitivesResourceDefaultNull;
import org.jboss.resteasy.test.resource.param.resource.HeaderParamsAsPrimitivesResourceDefaultOverride;
import org.jboss.resteasy.test.resource.param.resource.HeaderParamsAsPrimitivesSetProxy;
import org.jboss.resteasy.test.resource.param.resource.HeaderParamsAsPrimitivesSortedSetProxy;
import org.jboss.resteasy.test.resource.param.resource.HeaderParamsAsPrimitivesWrappersDefaultNullProxy;
import org.jboss.resteasy.test.resource.param.resource.HeaderParamsAsPrimitivesWrappersDefaultOverrideProxy;
import org.jboss.resteasy.test.resource.param.resource.HeaderParamsAsPrimitivesWrappersDefaultProxy;
import org.jboss.resteasy.test.resource.param.resource.HeaderParamsAsPrimitivesWrappersProxy;
import org.jboss.resteasy.test.resource.param.resource.HeaderParamsAsPrimitivesResourceSortedSet;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import javax.ws.rs.core.Response;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

import static org.junit.Assert.fail;

/**
 * @tpSubChapter Parameters
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test primitive header parameters
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class HeaderParamsAsPrimitivesTest {

    public static final String ERROR_MESSAGE = "Wrong content of header parameter";

    private static HeaderParamsAsPrimitivesPrimitivesProxy resourceHeaderPrimitives;
    private static HeaderParamsAsPrimitivesDefaultProxy resourceHeaderPrimitivesDefault;
    private static HeaderParamsAsPrimitivesDefaultOverrideProxy resourceHeaderPrimitivesDefaultOverride;
    private static HeaderParamsAsPrimitivesDefaultNullProxy resourceHeaderPrimitivesDefaultNull;
    private static HeaderParamsAsPrimitivesWrappersProxy resourceHeaderPrimitiveWrappers;
    private static HeaderParamsAsPrimitivesWrappersDefaultProxy resourceHeaderPrimitiveWrappersDefault;
    private static HeaderParamsAsPrimitivesWrappersDefaultOverrideProxy resourceHeaderPrimitiveWrappersDefaultOverride;
    private static HeaderParamsAsPrimitivesWrappersDefaultNullProxy resourceHeaderPrimitiveWrappersDefaultNull;
    private static HeaderParamsAsPrimitivesListProxy resourceHeaderPrimitiveList;
    private static HeaderParamsAsPrimitivesListDefaultProxy resourceHeaderPrimitiveListDefault;
    private static HeaderParamsAsPrimitivesListDefaultOverrideProxy resourceHeaderPrimitiveListDefaultOverride;
    private static HeaderParamsAsPrimitivesListDefaultNullProxy resourceHeaderPrimitiveListDefaultNull;
    private static HeaderParamsAsPrimitivesArrayProxy resourceHeaderPrimitiveArray;
    private static HeaderParamsAsPrimitivesArrayDefaultProxy resourceHeaderPrimitiveArrayDefault;
    private static HeaderParamsAsPrimitivesArrayDefaultOverrideProxy resourceHeaderPrimitiveArrayDefaultOverride;
    private static HeaderParamsAsPrimitivesArrayDefaultNullProxy resourceHeaderPrimitiveArrayDefaultNull;

    private ResteasyClient client;
    private static ResteasyClient proxyClient;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(HeaderParamsAsPrimitivesTest.class.getSimpleName());
        war.addClasses(HeaderParamsAsPrimitivesPrimitivesProxy.class,
                HeaderParamsAsPrimitivesDefaultProxy.class,
                HeaderParamsAsPrimitivesDefaultOverrideProxy.class,
                HeaderParamsAsPrimitivesDefaultNullProxy.class,
                HeaderParamsAsPrimitivesWrappersProxy.class,
                HeaderParamsAsPrimitivesWrappersDefaultProxy.class,
                HeaderParamsAsPrimitivesWrappersDefaultNullProxy.class,
                HeaderParamsAsPrimitivesWrappersDefaultOverrideProxy.class,
                HeaderParamsAsPrimitivesListProxy.class,
                HeaderParamsAsPrimitivesSetProxy.class,
                HeaderParamsAsPrimitivesSortedSetProxy.class,
                HeaderParamsAsPrimitivesListDefaultProxy.class,
                HeaderParamsAsPrimitivesListDefaultNullProxy.class,
                HeaderParamsAsPrimitivesListDefaultOverrideProxy.class,
                HeaderParamsAsPrimitivesArrayProxy.class,
                HeaderParamsAsPrimitivesArrayDefaultProxy.class,
                HeaderParamsAsPrimitivesArrayDefaultNullProxy.class,
                HeaderParamsAsPrimitivesArrayDefaultOverrideProxy.class);
        return TestUtil.finishContainerPrepare(war, null,
                HeaderParamsAsPrimitivesResourcePrimitives.class,
                HeaderParamsAsPrimitivesResourceDefault.class,
                HeaderParamsAsPrimitivesResourceDefaultOverride.class,
                HeaderParamsAsPrimitivesResourceDefaultNull.class,
                HeaderParamsAsPrimitivesResourceWrappers.class,
                HeaderParamsAsPrimitivesResourceWrappersDefault.class,
                HeaderParamsAsPrimitivesResourceWrappersDefaultNull.class,
                HeaderParamsAsPrimitivesResourceWrappersDefaultOverride.class,
                HeaderParamsAsPrimitivesResourceList.class,
                HeaderParamsAsPrimitivesResourceSet.class,
                HeaderParamsAsPrimitivesResourceSortedSet.class,
                HeaderParamsAsPrimitivesResourceListDefault.class,
                HeaderParamsAsPrimitivesResourceListDefaultNull.class,
                HeaderParamsAsPrimitivesResourceListDefaultOverride.class,
                HeaderParamsAsPrimitivesResourceArray.class,
                HeaderParamsAsPrimitivesResourceArrayDefault.class,
                HeaderParamsAsPrimitivesResourceArrayDefaultNull.class,
                HeaderParamsAsPrimitivesResourceArrayDefaultOverride.class);
    }

    private static String generateBaseUrl() {
        return PortProviderUtil.generateBaseUrl(HeaderParamsAsPrimitivesTest.class.getSimpleName());
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, HeaderParamsAsPrimitivesTest.class.getSimpleName());
    }

    @BeforeClass
    public static void before() throws Exception {
        proxyClient = new ResteasyClientBuilder().build();
        resourceHeaderPrimitives = proxyClient.target(generateBaseUrl())
                .proxyBuilder(HeaderParamsAsPrimitivesPrimitivesProxy.class).build();
        resourceHeaderPrimitivesDefault = proxyClient.target(generateBaseUrl())
                .proxyBuilder(HeaderParamsAsPrimitivesDefaultProxy.class).build();
        resourceHeaderPrimitivesDefaultOverride = proxyClient.target(generateBaseUrl())
                .proxyBuilder(HeaderParamsAsPrimitivesDefaultOverrideProxy.class).build();
        resourceHeaderPrimitivesDefaultNull = proxyClient.target(generateBaseUrl())
                .proxyBuilder(HeaderParamsAsPrimitivesDefaultNullProxy.class).build();
        resourceHeaderPrimitiveWrappers = proxyClient.target(generateBaseUrl())
                .proxyBuilder(HeaderParamsAsPrimitivesWrappersProxy.class).build();
        resourceHeaderPrimitiveWrappersDefault = proxyClient.target(generateBaseUrl())
                .proxyBuilder(HeaderParamsAsPrimitivesWrappersDefaultProxy.class).build();
        resourceHeaderPrimitiveWrappersDefaultOverride = proxyClient.target(generateBaseUrl())
                .proxyBuilder(HeaderParamsAsPrimitivesWrappersDefaultOverrideProxy.class).build();
        resourceHeaderPrimitiveWrappersDefaultNull = proxyClient.target(generateBaseUrl())
                .proxyBuilder(HeaderParamsAsPrimitivesWrappersDefaultNullProxy.class).build();
        resourceHeaderPrimitiveList = proxyClient.target(generateBaseUrl())
                .proxyBuilder(HeaderParamsAsPrimitivesListProxy.class).build();
        resourceHeaderPrimitiveListDefault = proxyClient.target(generateBaseUrl())
                .proxyBuilder(HeaderParamsAsPrimitivesListDefaultProxy.class).build();
        resourceHeaderPrimitiveListDefaultOverride = proxyClient.target(generateBaseUrl())
                .proxyBuilder(HeaderParamsAsPrimitivesListDefaultOverrideProxy.class).build();
        resourceHeaderPrimitiveListDefaultNull = proxyClient.target(generateBaseUrl())
                .proxyBuilder(HeaderParamsAsPrimitivesListDefaultNullProxy.class).build();
        resourceHeaderPrimitiveArray = proxyClient.target(generateBaseUrl())
                .proxyBuilder(HeaderParamsAsPrimitivesArrayProxy.class).build();
        resourceHeaderPrimitiveArrayDefault = proxyClient.target(generateBaseUrl())
                .proxyBuilder(HeaderParamsAsPrimitivesArrayDefaultProxy.class).build();
        resourceHeaderPrimitiveArrayDefaultOverride = proxyClient.target(generateBaseUrl())
                .proxyBuilder(HeaderParamsAsPrimitivesArrayDefaultOverrideProxy.class).build();
        resourceHeaderPrimitiveArrayDefaultNull = proxyClient.target(generateBaseUrl())
                .proxyBuilder(HeaderParamsAsPrimitivesArrayDefaultNullProxy.class).build();
    }

    @AfterClass
    public static void after() throws Exception {
        proxyClient.close();
    }

    public void basicTest(String type, String value) {
        {
            client = new ResteasyClientBuilder().build();
            Response response = client.target(generateURL("/")).request()
                    .header(HttpHeaderNames.ACCEPT, "application/" + type)
                    .header(type, value)
                    .get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            response.close();
            client.close();
        }

        {
            client = new ResteasyClientBuilder().build();
            Response response = client.target(generateURL("/wrappers")).request()
                    .header(HttpHeaderNames.ACCEPT, "application/" + type)
                    .header(type, value)
                    .get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            response.close();
            client.close();
        }

        {
            client = new ResteasyClientBuilder().build();
            Response response = client.target(generateURL("/list")).request()
                    .header(HttpHeaderNames.ACCEPT, "application/" + type)
                    .header(type, value)
                    .header(type, value)
                    .header(type, value)
                    .get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            response.close();
            client.close();
        }
    }

    public void testDefault(String base, String type, String value) {
        {
            client = new ResteasyClientBuilder().build();
            Response response = client.target(generateURL(base + "default/null")).request()
                    .header(HttpHeaderNames.ACCEPT, "application/" + type)
                    .get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            response.close();
            client.close();
        }

        {
            client = new ResteasyClientBuilder().build();
            Response response = client.target(generateURL(base + "default")).request()
                    .header(HttpHeaderNames.ACCEPT, "application/" + type)
                    .get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            response.close();
            client.close();
        }

        {
            client = new ResteasyClientBuilder().build();
            Response response = client.target(generateURL(base + "default/override")).request()
                    .header(HttpHeaderNames.ACCEPT, "application/" + type)
                    .header(type, value)
                    .get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            response.close();
            client.close();
        }
    }

    public void testDefault(String type, String value) {
        testDefault("/", type, value);
    }

    public void testWrappersDefault(String type, String value) {
        testDefault("/wrappers/", type, value);
    }

    public void testListDefault(String type, String value) {
        testDefault("/list/", type, value);
    }

    /**
     * @tpTestDetails Test set of boolean
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testSet() {
        {
            client = new ResteasyClientBuilder().build();
            Response response = client.target(generateURL("/set")).request()
                    .header(HttpHeaderNames.ACCEPT, "application/boolean")
                    .header("header", "one")
                    .header("header", "one")
                    .header("header", "one")
                    .header("header", "two")
                    .get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            response.close();
            client.close();

            client = new ResteasyClientBuilder().build();
            HeaderParamsAsPrimitivesSetProxy setClient = client.target(generateBaseUrl())
                    .proxyBuilder(HeaderParamsAsPrimitivesSetProxy.class).build();
            HashSet<String> set = new HashSet<>();
            set.add("one");
            set.add("two");
            setClient.doGetBoolean(set);
            client.close();
        }

        {
            client = new ResteasyClientBuilder().build();
            Response response = client.target(generateURL("/sortedset")).request()
                    .header(HttpHeaderNames.ACCEPT, "application/boolean")
                    .header("header", "one")
                    .header("header", "one")
                    .header("header", "one")
                    .header("header", "two")
                    .get();

            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            response.close();
            client.close();


            client = new ResteasyClientBuilder().build();
            HeaderParamsAsPrimitivesSortedSetProxy setClient = client.target(generateBaseUrl())
                    .proxyBuilder(HeaderParamsAsPrimitivesSortedSetProxy.class).build();
            TreeSet<String> set = new TreeSet<String>();
            set.add("one");
            set.add("two");
            setClient.doGetBoolean(set);
            client.close();
        }
    }

    /**
     * @tpTestDetails Test list of boolean with GET method
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetBoolean() {
        basicTest("boolean", "true");
        resourceHeaderPrimitives.doGet(true);
        resourceHeaderPrimitiveWrappers.doGet(Boolean.TRUE);
        ArrayList<Boolean> list = new ArrayList<>();
        list.add(Boolean.TRUE);
        list.add(Boolean.TRUE);
        list.add(Boolean.TRUE);
        resourceHeaderPrimitiveList.doGetBoolean(list);
        boolean[] array =
                {true, true, true};
        resourceHeaderPrimitiveArray.doGetBoolean(array);
    }

    /**
     * @tpTestDetails Basic test for boolean
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetBooleanPrimitivesDefault() {
        testDefault("boolean", "true");
        resourceHeaderPrimitivesDefault.doGetBoolean();
        resourceHeaderPrimitivesDefaultNull.doGetBoolean();
        resourceHeaderPrimitivesDefaultOverride.doGet(true);
    }

    /**
     * @tpTestDetails Boolean test by proxy
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetBooleanPrimitiveWrapperDefault() {
        testWrappersDefault("boolean", "true");
        resourceHeaderPrimitiveWrappersDefault.doGetBoolean();
        resourceHeaderPrimitiveWrappersDefaultNull.doGetBoolean();
        resourceHeaderPrimitiveWrappersDefaultOverride.doGet(Boolean.TRUE);
    }

    /**
     * @tpTestDetails Proxy test for list of boolean
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetBooleanPrimitiveListDefault() {
        testListDefault("boolean", "true");
        resourceHeaderPrimitiveListDefault.doGetBoolean();
        resourceHeaderPrimitiveListDefaultNull.doGetBoolean();
        List<Boolean> list = new ArrayList<>();
        list.add(Boolean.TRUE);
        resourceHeaderPrimitiveListDefaultOverride.doGetBoolean(list);
        resourceHeaderPrimitiveArrayDefault.doGetBoolean();
        resourceHeaderPrimitiveArrayDefaultNull.doGetBoolean();
        boolean[] array = {true};
        resourceHeaderPrimitiveArrayDefaultOverride.doGetBoolean(array);
    }

    /**
     * @tpTestDetails Basic test for byte
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetByte() {
        basicTest("byte", "127");
        try {
            resourceHeaderPrimitives.doGet((byte) 127);
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            fail("resourceHeaderPrimitives.doGet() failed:\n" + errors.toString());
        }
        resourceHeaderPrimitiveWrappers.doGet(new Byte((byte) 127));
        ArrayList<Byte> list = new ArrayList<Byte>();
        list.add(new Byte((byte) 127));
        list.add(new Byte((byte) 127));
        list.add(new Byte((byte) 127));
        try {
            resourceHeaderPrimitiveList.doGetByte(list);
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            fail("resourceHeaderPrimitiveList.doGetByte() failed:\n" + errors.toString());
        }
    }

    /**
     * @tpTestDetails Proxy test for byte
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetBytePrimitivesDefault() {
        testDefault("byte", "127");
        resourceHeaderPrimitivesDefault.doGetByte();
        resourceHeaderPrimitivesDefaultNull.doGetByte();
        resourceHeaderPrimitivesDefaultOverride.doGet((byte) 127);
    }

    /**
     * @tpTestDetails Proxy test for byte with wrapper
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetBytePrimitiveWrappersDefault() {
        testWrappersDefault("byte", "127");
        resourceHeaderPrimitiveWrappersDefault.doGetByte();
        resourceHeaderPrimitiveWrappersDefaultNull.doGetByte();
        resourceHeaderPrimitiveWrappersDefaultOverride.doGet(new Byte((byte) 127));
    }

    /**
     * @tpTestDetails Basic test for byte list
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetBytePrimitiveListDefault() {
        testListDefault("byte", "127");
        resourceHeaderPrimitiveListDefault.doGetByte();
        resourceHeaderPrimitiveListDefaultNull.doGetByte();
        List<Byte> list = new ArrayList<Byte>();
        list.add(new Byte((byte) 127));
        resourceHeaderPrimitiveListDefaultOverride.doGetByte(list);
    }

    /**
     * @tpTestDetails Basic test for short, use proxy
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetShort() {
        basicTest("short", "32767");
        resourceHeaderPrimitives.doGet((short) 32767);
        resourceHeaderPrimitiveWrappers.doGet(new Short((short) 32767));
        ArrayList<Short> list = new ArrayList<Short>();
        list.add(new Short((short) 32767));
        list.add(new Short((short) 32767));
        list.add(new Short((short) 32767));
        resourceHeaderPrimitiveList.doGetShort(list);
    }

    /**
     * @tpTestDetails Basic test for short, test default value
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetShortPrimtivesDefault() {
        testDefault("short", "32767");
    }

    /**
     * @tpTestDetails Short type test, use wrapper
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetShortPrimtiveWrappersDefault() {
        testWrappersDefault("short", "32767");
    }

    /**
     * @tpTestDetails Short test, test default value
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetShortPrimtiveListDefault() {
        testListDefault("short", "32767");
    }

    /**
     * @tpTestDetails Basic test for int
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetInt() {
        basicTest("int", "2147483647");
    }

    /**
     * @tpTestDetails Check default value for int
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetIntPrimitivesDefault() {
        testDefault("int", "2147483647");
    }

    /**
     * @tpTestDetails Test int with wrapper
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetIntPrimitiveWrappersDefault() {
        testWrappersDefault("int", "2147483647");
    }

    /**
     * @tpTestDetails Test list of int
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetIntPrimitiveListDefault() {
        testListDefault("int", "2147483647");
    }

    /**
     * @tpTestDetails Basic test for long
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetLong() {
        basicTest("long", "9223372036854775807");
    }

    /**
     * @tpTestDetails Test default value for long
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetLongPrimitivesDefault() {
        testDefault("long", "9223372036854775807");
    }

    /**
     * @tpTestDetails Test default value for long, use wrapper
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetLongPrimitiveWrappersDefault() {
        testWrappersDefault("long", "9223372036854775807");
    }

    /**
     * @tpTestDetails Test default value for list of long, do not use wrapper
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetLongPrimitiveListDefault() {
        testListDefault("long", "9223372036854775807");
    }

    /**
     * @tpTestDetails Basic test for float
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetFloat() {
        basicTest("float", "3.14159265");
    }

    /**
     * @tpTestDetails Test default value for float
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetFloatPrimitivesDefault() {
        testDefault("float", "3.14159265");
    }

    /**
     * @tpTestDetails Test default value for float, use wrapper
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetFloatPrimitiveWrappersDefault() {
        testWrappersDefault("float", "3.14159265");
    }

    /**
     * @tpTestDetails Test default value for list of float, use wrapper
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetFloatPrimitiveListDefault() {
        testListDefault("float", "3.14159265");
    }

    /**
     * @tpTestDetails Basic test for double
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetDouble() {
        basicTest("double", "3.14159265358979");
    }

    /**
     * @tpTestDetails Basic test for double, test default value
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetDoublePrimitivesDefault() {
        testDefault("double", "3.14159265358979");
    }

    /**
     * @tpTestDetails Basic test for double, use wrapper
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetDoublePrimitiveWrappersDefault() {
        testWrappersDefault("double", "3.14159265358979");
    }

    /**
     * @tpTestDetails Basic test for list of double, do not use wrapper
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetDoublePrimitiveListDefault() {
        testListDefault("double", "3.14159265358979");
    }

    /**
     * @tpTestDetails Negative test for int
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testBadPrimitiveValue() {
        client = new ResteasyClientBuilder().build();
        Response response = client.target(generateURL("/")).request()
                .header(HttpHeaderNames.ACCEPT, "application/int")
                .header("int", "abcdef")
                .get();
        Assert.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
        response.close();
        client.close();
    }

    /**
     * @tpTestDetails Negative test for int, use wrapper
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testBadPrimitiveWrapperValue() {
        client = new ResteasyClientBuilder().build();
        Response response = client.target(generateURL("/wrappers")).request()
                .header(HttpHeaderNames.ACCEPT, "application/int")
                .header("int", "abcdef")
                .get();
        Assert.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
        response.close();
        client.close();
    }

    /**
     * @tpTestDetails Negative test for list of int
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testBadPrimitiveListValue() {
        client = new ResteasyClientBuilder().build();
        Response response = client.target(generateURL("/list")).request()
                .header(HttpHeaderNames.ACCEPT, "application/int")
                .header("int", "abcdef")
                .header("int", "abcdef")
                .header("int", "abcdef")
                .get();
        Assert.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
        response.close();
        client.close();
    }
}
