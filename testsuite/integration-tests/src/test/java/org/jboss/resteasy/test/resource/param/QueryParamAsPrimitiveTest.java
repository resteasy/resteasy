package org.jboss.resteasy.test.resource.param;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ProxyBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.resource.param.resource.QueryParamAsPrimitiveResource;
import org.jboss.resteasy.test.resource.param.resource.QueryParamAsPrimitiveResourceArray;
import org.jboss.resteasy.test.resource.param.resource.QueryParamAsPrimitiveResourceArrayDefault;
import org.jboss.resteasy.test.resource.param.resource.QueryParamAsPrimitiveResourceArrayDefaultNull;
import org.jboss.resteasy.test.resource.param.resource.QueryParamAsPrimitiveResourceArrayDefaultOverride;
import org.jboss.resteasy.test.resource.param.resource.QueryParamAsPrimitiveResourceDefault;
import org.jboss.resteasy.test.resource.param.resource.QueryParamAsPrimitiveResourceDefaultNull;
import org.jboss.resteasy.test.resource.param.resource.QueryParamAsPrimitiveResourceDefaultOverride;
import org.jboss.resteasy.test.resource.param.resource.QueryParamAsPrimitiveResourceList;
import org.jboss.resteasy.test.resource.param.resource.QueryParamAsPrimitiveResourceListDefault;
import org.jboss.resteasy.test.resource.param.resource.QueryParamAsPrimitiveResourceListDefaultNull;
import org.jboss.resteasy.test.resource.param.resource.QueryParamAsPrimitiveResourceListDefaultOverride;
import org.jboss.resteasy.test.resource.param.resource.QueryParamAsPrimitiveResourceQueryPrimitivesInterface;
import org.jboss.resteasy.test.resource.param.resource.QueryParamAsPrimitiveResourceResourceArray;
import org.jboss.resteasy.test.resource.param.resource.QueryParamAsPrimitiveResourceResourceListInterface;
import org.jboss.resteasy.test.resource.param.resource.QueryParamAsPrimitiveResourceResourceWrappersInterface;
import org.jboss.resteasy.test.resource.param.resource.QueryParamAsPrimitiveResourceWrappers;
import org.jboss.resteasy.test.resource.param.resource.QueryParamAsPrimitiveResourceWrappersDefault;
import org.jboss.resteasy.test.resource.param.resource.QueryParamAsPrimitiveResourceWrappersDefaultNull;
import org.jboss.resteasy.test.resource.param.resource.QueryParamAsPrimitiveResourceWrappersDefaultOverride;
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

import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import static org.jboss.resteasy.util.HttpClient4xUtils.updateQuery;

/**
 * @tpSubChapter Parameters
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test all variants of primitive query parameters (boolean, int, long, float, etc.)
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class QueryParamAsPrimitiveTest {

    public static final String ERROR_MESSAGE = "Wrong object received";

    private static QueryParamAsPrimitiveResourceQueryPrimitivesInterface resourceQueryPrimitives;

    private static QueryParamAsPrimitiveResourceResourceWrappersInterface resourceQueryPrimitiveWrappers;

    private static QueryParamAsPrimitiveResourceResourceListInterface resourceQueryPrimitiveList;

    private static QueryParamAsPrimitiveResourceResourceArray resourceQueryPrimitiveArray;
    private static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() throws Exception {
        WebArchive war = TestUtil.prepareArchive(QueryParamAsPrimitiveTest.class.getSimpleName());
        war.addClass(QueryParamAsPrimitiveResourceQueryPrimitivesInterface.class);
        war.addClass(QueryParamAsPrimitiveResourceResourceArray.class);
        war.addClass(QueryParamAsPrimitiveResourceResourceListInterface.class);
        war.addClass(QueryParamAsPrimitiveResourceResourceWrappersInterface.class);
        war.addClass(QueryParamAsPrimitiveTest.class);
        return TestUtil.finishContainerPrepare(war, null,
                QueryParamAsPrimitiveResource.class,
                QueryParamAsPrimitiveResourceDefault.class,
                QueryParamAsPrimitiveResourceDefaultOverride.class,
                QueryParamAsPrimitiveResourceDefaultNull.class,
                QueryParamAsPrimitiveResourceWrappers.class,
                QueryParamAsPrimitiveResourceWrappersDefault.class,
                QueryParamAsPrimitiveResourceWrappersDefaultNull.class,
                QueryParamAsPrimitiveResourceWrappersDefaultOverride.class,
                QueryParamAsPrimitiveResourceList.class,
                QueryParamAsPrimitiveResourceListDefault.class,
                QueryParamAsPrimitiveResourceListDefaultNull.class,
                QueryParamAsPrimitiveResourceListDefaultOverride.class,
                QueryParamAsPrimitiveResourceArray.class,
                QueryParamAsPrimitiveResourceArrayDefault.class,
                QueryParamAsPrimitiveResourceArrayDefaultNull.class,
                QueryParamAsPrimitiveResourceArrayDefaultOverride.class);
    }

    @BeforeClass
    public static void before() throws Exception {
        client = new ResteasyClientBuilder().build();
        resourceQueryPrimitives = ProxyBuilder.builder(QueryParamAsPrimitiveResourceQueryPrimitivesInterface.class, client.target(generateBaseUrl())).build();
        resourceQueryPrimitiveWrappers = ProxyBuilder.builder(QueryParamAsPrimitiveResourceResourceWrappersInterface.class, client.target(generateBaseUrl())).build();
        resourceQueryPrimitiveList = ProxyBuilder.builder(QueryParamAsPrimitiveResourceResourceListInterface.class, client.target(generateBaseUrl())).build();
        resourceQueryPrimitiveArray = ProxyBuilder.builder(QueryParamAsPrimitiveResourceResourceArray.class, client.target(generateBaseUrl())).build();
    }

    @AfterClass
    public static void after() throws Exception {
        client.close();
    }

    private static String generateURL(String path) {
        return PortProviderUtil.generateURL(path, QueryParamAsPrimitiveTest.class.getSimpleName());
    }

    private static String generateBaseUrl() {
        return PortProviderUtil.generateBaseUrl(QueryParamAsPrimitiveTest.class.getSimpleName());
    }

    public void basicTest(String type, String value) {
        String param = type + "=" + value;

        {
            String uri = updateQuery(generateURL("/"), param);
            Invocation.Builder request = client.target(uri).request();
            request.header(HttpHeaderNames.ACCEPT, "application/" + type);
            try {
                Response response = request.get();
                Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
                response.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        {
            String uri = updateQuery(generateURL("/wrappers"), param);
            Invocation.Builder request = client.target(uri).request();
            request.header(HttpHeaderNames.ACCEPT, "application/" + type);
            try {
                Response response = request.get();
                Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
                response.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        {
            String uri = updateQuery(generateURL("/list"), param + "&" + param + "&" + param);
            Invocation.Builder request = client.target(uri).request();
            request.header(HttpHeaderNames.ACCEPT, "application/" + type);
            try {
                Response response = request.get();
                Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
                response.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        {
            String uri = updateQuery(generateURL("/array"), param + "&" + param + "&" + param);
            Invocation.Builder request = client.target(uri).request();
            request.header(HttpHeaderNames.ACCEPT, "application/" + type);
            try {
                Response response = request.get();
                Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
                response.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void testDefault(String base, String type, String value) {
        {
            Invocation.Builder request = client.target(generateURL("" + base + "default/null")).request();
            request.header(HttpHeaderNames.ACCEPT, "application/" + type);
            try {
                Response response = request.get();
                Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
                response.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        {
            Invocation.Builder request = client.target(generateURL("" + base + "default")).request();
            request.header(HttpHeaderNames.ACCEPT, "application/" + type);
            try {
                Response response = request.get();
                Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
                response.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        String param = type + "=" + value;
        {
            String uri = updateQuery(generateURL("" + base + "default/override"), param);
            Invocation.Builder request = client.target(uri).request();
            request.header(HttpHeaderNames.ACCEPT, "application/" + type);
            try {
                Response response = request.get();
                Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
                response.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
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

    public void testArrayDefault(String type, String value) {
        testDefault("/array/", type, value);
    }

    /**
     * @tpTestDetails Test boolean primitive object for get request
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetBoolean() {
        basicTest("boolean", "true");
        resourceQueryPrimitives.doGet(true);
        resourceQueryPrimitiveWrappers.doGet(true);
        List<Boolean> list = new ArrayList<Boolean>();
        list.add(Boolean.TRUE);
        list.add(Boolean.TRUE);
        list.add(Boolean.TRUE);
        resourceQueryPrimitiveList.doGetBoolean(list);
        boolean[] array =
                {true, true, true};
        resourceQueryPrimitiveArray.doGetBoolean(array);
    }

    /**
     * @tpTestDetails Test boolean primitive object
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetBooleanPrimitivesDefault() {
        testDefault("boolean", "true");
    }

    /**
     * @tpTestDetails Test boolean primitive object with proxy
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetBooleanPrimitiveWrapperDefault() {
        testWrappersDefault("boolean", "true");
    }

    /**
     * @tpTestDetails Test boolean primitive objects in list
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetBooleanPrimitiveListDefault() {
        testListDefault("boolean", "true");
    }

    /**
     * @tpTestDetails Test boolean primitive objects in array
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetBooleanPrimitiveArrayDefault() {
        testArrayDefault("boolean", "true");
    }

    /**
     * @tpTestDetails Test byte primitive object for get method
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetByte() {
        basicTest("byte", "127");
        resourceQueryPrimitives.doGet((byte) 127);
        resourceQueryPrimitiveWrappers.doGet((byte) 127);
        List<Byte> list = new ArrayList<Byte>();
        list.add(new Byte((byte) 127));
        list.add(new Byte((byte) 127));
        list.add(new Byte((byte) 127));
        resourceQueryPrimitiveList.doGetByte(list);
        byte[] array =
                {(byte) 127, (byte) 127, (byte) 127};
        resourceQueryPrimitiveArray.doGetByte(array);
    }

    /**
     * @tpTestDetails Test byte primitive object
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetBytePrimitivesDefault() {
        testDefault("byte", "127");
    }

    /**
     * @tpTestDetails Test byte primitive object with proxy
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetBytePrimitiveWrappersDefault() {
        testWrappersDefault("byte", "127");
    }

    /**
     * @tpTestDetails Test byte primitive objects in list
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetBytePrimitiveListDefault() {
        testListDefault("byte", "127");
    }

    /**
     * @tpTestDetails Test byte primitive objects in array
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetBytePrimitiveArrayDefault() {
        testArrayDefault("byte", "127");
    }

    /**
     * @tpTestDetails Test short primitive object for get method
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetShort() {
        basicTest("short", "32767");
    }

    /**
     * @tpTestDetails Test short primitive object
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetShortPrimtivesDefault() {
        testDefault("short", "32767");
    }

    /**
     * @tpTestDetails Test short primitive object with proxy
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetShortPrimtiveWrappersDefault() {
        testWrappersDefault("short", "32767");
    }

    /**
     * @tpTestDetails Test short primitive objects in list
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetShortPrimtiveListDefault() {
        testListDefault("short", "32767");
    }

    /**
     * @tpTestDetails Test int primitive object for get method
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetInt() {
        basicTest("int", "2147483647");
    }

    /**
     * @tpTestDetails Test int primitive object
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetIntPrimitivesDefault() {
        testDefault("int", "2147483647");
    }

    /**
     * @tpTestDetails Test int primitive object with proxy
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetIntPrimitiveWrappersDefault() {
        testWrappersDefault("int", "2147483647");
    }

    /**
     * @tpTestDetails Test int primitive objects in list
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetIntPrimitiveListDefault() {
        testListDefault("int", "2147483647");
    }

    /**
     * @tpTestDetails Test long primitive object with get method
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetLong() {
        basicTest("long", "9223372036854775807");
    }

    /**
     * @tpTestDetails Test long primitive object
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetLongPrimitivesDefault() {
        testDefault("long", "9223372036854775807");
    }

    /**
     * @tpTestDetails Test long primitive object with proxy
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetLongPrimitiveWrappersDefault() {
        testWrappersDefault("long", "9223372036854775807");
    }

    /**
     * @tpTestDetails Test long primitive objects in list
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetLongPrimitiveListDefault() {
        testListDefault("long", "9223372036854775807");
    }

    /**
     * @tpTestDetails Test float primitive object with get method
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetFloat() {
        basicTest("float", "3.14159265");
    }

    /**
     * @tpTestDetails Test float primitive object
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetFloatPrimitivesDefault() {
        testDefault("float", "3.14159265");
    }

    /**
     * @tpTestDetails Test float primitive object with proxy
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetFloatPrimitiveWrappersDefault() {
        testWrappersDefault("float", "3.14159265");
    }

    /**
     * @tpTestDetails Test float primitive objects in list
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetFloatPrimitiveListDefault() {
        testListDefault("float", "3.14159265");
    }

    /**
     * @tpTestDetails Test double primitive object with get method
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetDouble() {
        basicTest("double", "3.14159265358979");
    }

    /**
     * @tpTestDetails Test double primitive object
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetDoublePrimitivesDefault() {
        testDefault("double", "3.14159265358979");
    }

    /**
     * @tpTestDetails Test double primitive object with proxy
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetDoublePrimitiveWrappersDefault() {
        testWrappersDefault("double", "3.14159265358979");
    }

    /**
     * @tpTestDetails Test double primitive objects in list
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetDoublePrimitiveListDefault() {
        testListDefault("double", "3.14159265358979");
    }
    
    /**
     * @tpTestDetails Test char primitive object for get method
     * @tpSince RESTEasy 3.0.24
     */
    @Test
    public void testGetChar() {
        basicTest("char", "a");
    }

    /**
     * @tpTestDetails Test char primitive object
     * @tpSince RESTEasy 3.0.24
     */
    @Test
    public void testGetCharPrimitivesDefault() {
        testDefault("char", "a");
    }

    /**
     * @tpTestDetails Test char primitive object with proxy
     * @tpSince RESTEasy 3.0.24
     */
    @Test
    public void testGetCharPrimitiveWrappersDefault() {
        testWrappersDefault("char", "a");
    }

    /**
     * @tpTestDetails Test char primitive objects in list
     * @tpSince RESTEasy 3.0.24
     */
    @Test
    public void testGetCharPrimitiveListDefault() {
        testListDefault("char", "a");
    }
    
    /**
     * @tpTestDetails Test char primitive objects in array
     * @tpSince RESTEasy 3.0.24
     */
    @Test
    public void testGetCharPrimitiveArrayDefault() {
        testArrayDefault("char", "a");
    }

    /**
     * @tpTestDetails Negative testing: accept only int, but string is provided. Error is excepted.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testBadPrimitiveValue() {
        {
            String uri = updateQuery(generateURL("/"), "int=abcdef");
            Invocation.Builder request = client.target(uri).request();
            request.header(HttpHeaderNames.ACCEPT, "application/int");
            try {
                Response response = request.get();
                Assert.assertEquals(404, response.getStatus());
                response.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * @tpTestDetails Negative testing: accept only int, but string is provided. Error is excepted.
     *                Proxy is used.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testBadPrimitiveWrapperValue() {
        {
            String uri = updateQuery(generateURL("/wrappers"), "int=abcdef");
            Invocation.Builder request = client.target(uri).request();
            request.header(HttpHeaderNames.ACCEPT, "application/int");
            try {
                Response response = request.get();
                Assert.assertEquals(404, response.getStatus());
                response.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * @tpTestDetails Negative testing: accept only int, but string is provided. Error is excepted.
     *                List is tested
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testBadPrimitiveListValue() {
        {
            String uri = updateQuery(generateURL("/list"), "int=abcdef&int=abcdef");
            Invocation.Builder request = client.target(uri).request();
            request.header(HttpHeaderNames.ACCEPT, "application/int");
            try {
                Response response = request.get();
                Assert.assertEquals(404, response.getStatus());
                response.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}
