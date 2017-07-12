package org.jboss.resteasy.test.resource.param;


import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ProxyBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.resource.param.resource.UriParamAsPrimitiveResourceUriBoolean;
import org.jboss.resteasy.test.resource.param.resource.UriParamAsPrimitiveResourceUriBooleanInterface;
import org.jboss.resteasy.test.resource.param.resource.UriParamAsPrimitiveResourceUriBooleanWrapper;
import org.jboss.resteasy.test.resource.param.resource.UriParamAsPrimitiveResourceUriByte;
import org.jboss.resteasy.test.resource.param.resource.UriParamAsPrimitiveResourceUriByteInterface;
import org.jboss.resteasy.test.resource.param.resource.UriParamAsPrimitiveResourceUriByteWrapper;
import org.jboss.resteasy.test.resource.param.resource.UriParamAsPrimitiveResourceUriChar;
import org.jboss.resteasy.test.resource.param.resource.UriParamAsPrimitiveResourceUriCharWrapper;
import org.jboss.resteasy.test.resource.param.resource.UriParamAsPrimitiveResourceUriDouble;
import org.jboss.resteasy.test.resource.param.resource.UriParamAsPrimitiveResourceUriDoubleWrapper;
import org.jboss.resteasy.test.resource.param.resource.UriParamAsPrimitiveResourceUriFloat;
import org.jboss.resteasy.test.resource.param.resource.UriParamAsPrimitiveResourceUriFloatWrapper;
import org.jboss.resteasy.test.resource.param.resource.UriParamAsPrimitiveResourceUriInt;
import org.jboss.resteasy.test.resource.param.resource.UriParamAsPrimitiveResourceUriIntWrapper;
import org.jboss.resteasy.test.resource.param.resource.UriParamAsPrimitiveResourceUriLong;
import org.jboss.resteasy.test.resource.param.resource.UriParamAsPrimitiveResourceUriLongWrapper;
import org.jboss.resteasy.test.resource.param.resource.UriParamAsPrimitiveResourceUriShort;
import org.jboss.resteasy.test.resource.param.resource.UriParamAsPrimitiveResourceUriShortWrapper;
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

/**
 * @tpSubChapter Parameters
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test all variants of primitive URI parameters (boolean, int, long, float, etc.)
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class UriParamAsPrimitiveTest {
    public static final String ERROR_CODE = "Wrong parameter";

    private static UriParamAsPrimitiveResourceUriBooleanInterface resourceUriBoolean;
    private static UriParamAsPrimitiveResourceUriByteInterface resourceUriByte;
    private static ResteasyClient client;

    @BeforeClass
    public static void before() throws Exception {
        client = new ResteasyClientBuilder().build();
        resourceUriBoolean = ProxyBuilder.builder(UriParamAsPrimitiveResourceUriBooleanInterface.class, client.target(generateBaseUrl())).build();
        resourceUriByte = ProxyBuilder.builder(UriParamAsPrimitiveResourceUriByteInterface.class, client.target(generateBaseUrl())).build();
    }

    @Deployment
    public static Archive<?> deploy() throws Exception {
        WebArchive war = TestUtil.prepareArchive(UriParamAsPrimitiveTest.class.getSimpleName());
        war.addClass(UriParamAsPrimitiveResourceUriBooleanInterface.class);
        war.addClass(UriParamAsPrimitiveResourceUriByteInterface.class);
        war.addClass(UriParamAsPrimitiveTest.class);
        return TestUtil.finishContainerPrepare(war, null,
                UriParamAsPrimitiveResourceUriBoolean.class,
                UriParamAsPrimitiveResourceUriByte.class,
                UriParamAsPrimitiveResourceUriShort.class,
                UriParamAsPrimitiveResourceUriInt.class,
                UriParamAsPrimitiveResourceUriLong.class,
                UriParamAsPrimitiveResourceUriFloat.class,
                UriParamAsPrimitiveResourceUriDouble.class,
                UriParamAsPrimitiveResourceUriChar.class,
                UriParamAsPrimitiveResourceUriBooleanWrapper.class,
                UriParamAsPrimitiveResourceUriByteWrapper.class,
                UriParamAsPrimitiveResourceUriShortWrapper.class,
                UriParamAsPrimitiveResourceUriIntWrapper.class,
                UriParamAsPrimitiveResourceUriLongWrapper.class,
                UriParamAsPrimitiveResourceUriFloatWrapper.class,
                UriParamAsPrimitiveResourceUriDoubleWrapper.class,
                UriParamAsPrimitiveResourceUriCharWrapper.class);
    }

    private static String generateURL(String path) {
        return PortProviderUtil.generateURL(path, UriParamAsPrimitiveTest.class.getSimpleName());
    }

    private static String generateBaseUrl() {
        return PortProviderUtil.generateBaseUrl(UriParamAsPrimitiveTest.class.getSimpleName());
    }


    @AfterClass
    public static void after() throws Exception {
        client.close();
    }

    void basicTest(String type, String value) {
        {
            Invocation.Builder request = client.target(generateURL("/" + type + "/" + value)).request();
            try {
                Response response = request.get();
                Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
                response.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        {
            Invocation.Builder request = client.target(generateURL("/" + type + "/wrapper/" + value)).request();
            try {
                Response response = request.get();
                Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
                response.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * @tpTestDetails Test boolean primitive object
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetBoolean() {
        basicTest("boolean", "true");
        resourceUriBoolean.doGet(true);
    }

    /**
     * @tpTestDetails Test byte primitive object
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetByte() {
        basicTest("byte", "127");
        resourceUriByte.doGet((byte) 127);
    }

    /**
     * @tpTestDetails Test short primitive object
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetShort() {
        basicTest("short", "32767");
    }

    /**
     * @tpTestDetails Test int primitive object
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetInt() {
        basicTest("int", "2147483647");
    }

    /**
     * @tpTestDetails Test long primitive object
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetLong() {
        basicTest("long", "9223372036854775807");
    }

    /**
     * @tpTestDetails Test float primitive object
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetFloat() {
        basicTest("float", "3.14159265");
    }

    /**
     * @tpTestDetails Test double primitive object
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetDouble() {
        basicTest("double", "3.14159265358979");
    }
    
    /**
     * @tpTestDetails Test char primitive object
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetChar() {
        basicTest("char", "a");
    }
}
