package org.jboss.resteasy.test.providers.plain;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.test.providers.plain.resource.DefaultNumberWriterCustom;
import org.jboss.resteasy.test.providers.plain.resource.DefaultNumberWriterResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.lang.reflect.ReflectPermission;
import java.net.SocketPermission;
import java.util.PropertyPermission;

/**
 * @tpSubChapter Plain provider
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for DefaultNumberWriter provider.
 *                    Regression test for partial fix for JBEAP-2847.
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
public class DefaultNumberWriterTest {
    private static Logger logger = Logger.getLogger(DefaultNumberWriterTest.class);
    private static final String WRONG_RESPONSE_ERROR_MSG = "Response contains wrong response";
    private static final String WRONG_PROVIDER_USED_ERROR_MSG = "Wrong provider was used";

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(DefaultNumberWriterTest.class.getSimpleName());
        war.addClasses(TestUtil.class, PortProviderUtil.class);
        // Arquillian in the deployment
        war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
                new SocketPermission(PortProviderUtil.getHost(), "connect,resolve"),
                new PropertyPermission("org.jboss.resteasy.port", "read"),
                new RuntimePermission("getenv.RESTEASY_PORT"),
                new PropertyPermission("ipv6", "read"),
                new PropertyPermission("node", "read"),
                new ReflectPermission("suppressAccessChecks"),
                new ReflectPermission("suppressAccessChecks"),
                new RuntimePermission("accessDeclaredMembers"),
                new PropertyPermission("arquillian.*", "read")), "permissions.xml");
        return TestUtil.finishContainerPrepare(war, null, DefaultNumberWriterResource.class,
                DefaultNumberWriterCustom.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, DefaultNumberWriterTest.class.getSimpleName());
    }

    @After
    public void resetProviderFlag() {
       DefaultNumberWriterCustom.used = false;
    }
    
    /**
     * @tpTestDetails Tests Byte object
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testByte() throws Exception {
        Client client = ClientBuilder.newClient();
        Response response = client.target(generateURL("/test/Byte")).request().get();
        response.bufferEntity();
        logger.info(response.readEntity(String.class));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals(WRONG_RESPONSE_ERROR_MSG, "123", response.getEntity());
        Assert.assertTrue(WRONG_PROVIDER_USED_ERROR_MSG, DefaultNumberWriterCustom.used);
    }

    /**
     * @tpTestDetails Tests byte primitive
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testBytePrimitive() throws Exception {
        Client client = ClientBuilder.newClient();
        Response response = client.target(generateURL("/test/byte")).request().get();
        response.bufferEntity();
        logger.info(response.readEntity(String.class));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals(WRONG_RESPONSE_ERROR_MSG, "123", response.getEntity());
        Assert.assertTrue(WRONG_PROVIDER_USED_ERROR_MSG, DefaultNumberWriterCustom.used);
    }

    /**
     * @tpTestDetails Tests Double object
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testDouble() throws Exception {
        Client client = ClientBuilder.newClient();
        Response response = client.target(generateURL("/test/Double")).request().get();
        response.bufferEntity();
        logger.info(response.readEntity(String.class));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals(WRONG_RESPONSE_ERROR_MSG, "123.4", response.getEntity());
        Assert.assertTrue(WRONG_PROVIDER_USED_ERROR_MSG, DefaultNumberWriterCustom.used);
    }

    /**
     * @tpTestDetails Tests double primitive
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testDoublePrimitive() throws Exception {
        Client client = ClientBuilder.newClient();
        Response response = client.target(generateURL("/test/double")).request().get();
        response.bufferEntity();
        logger.info(response.readEntity(String.class));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals(WRONG_RESPONSE_ERROR_MSG, "123.4", response.getEntity());
        Assert.assertTrue(WRONG_PROVIDER_USED_ERROR_MSG, DefaultNumberWriterCustom.used);
    }

    /**
     * @tpTestDetails Tests Float object
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testFloat() throws Exception {
        Client client = ClientBuilder.newClient();
        Response response = client.target(generateURL("/test/Float")).request().get();
        response.bufferEntity();
        logger.info(response.readEntity(String.class));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals(WRONG_RESPONSE_ERROR_MSG, "123.4", response.getEntity());
        Assert.assertTrue(WRONG_PROVIDER_USED_ERROR_MSG, DefaultNumberWriterCustom.used);
    }

    /**
     * @tpTestDetails Tests float primitive
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testFloatPrimitive() throws Exception {
        Client client = ClientBuilder.newClient();
        Response response = client.target(generateURL("/test/float")).request().get();
        response.bufferEntity();
        logger.info(response.readEntity(String.class));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals(WRONG_RESPONSE_ERROR_MSG, "123.4", response.getEntity());
        Assert.assertTrue(WRONG_PROVIDER_USED_ERROR_MSG, DefaultNumberWriterCustom.used);
    }

    /**
     * @tpTestDetails Tests Integer object
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testInteger() throws Exception {
        Client client = ClientBuilder.newClient();
        Response response = client.target(generateURL("/test/Integer")).request().get();
        response.bufferEntity();
        logger.info(response.readEntity(String.class));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals(WRONG_RESPONSE_ERROR_MSG, "123", response.getEntity());
        Assert.assertTrue(WRONG_PROVIDER_USED_ERROR_MSG, DefaultNumberWriterCustom.used);
    }

    /**
     * @tpTestDetails Tests integer primitive
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testIntegerPrimitive() throws Exception {
        Client client = ClientBuilder.newClient();
        Response response = client.target(generateURL("/test/integer")).request().get();
        response.bufferEntity();
        logger.info(response.readEntity(String.class));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals(WRONG_RESPONSE_ERROR_MSG, "123", response.getEntity());
        Assert.assertTrue(WRONG_PROVIDER_USED_ERROR_MSG, DefaultNumberWriterCustom.used);
    }

    /**
     * @tpTestDetails Tests Long object
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testLong() throws Exception {
        Client client = ClientBuilder.newClient();
        Response response = client.target(generateURL("/test/Long")).request().get();
        response.bufferEntity();
        logger.info(response.readEntity(String.class));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals(WRONG_RESPONSE_ERROR_MSG, "123", response.getEntity());
        Assert.assertTrue(WRONG_PROVIDER_USED_ERROR_MSG, DefaultNumberWriterCustom.used);
    }

    /**
     * @tpTestDetails Tests long primitive
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testLongPrimitive() throws Exception {
        Client client = ClientBuilder.newClient();
        Response response = client.target(generateURL("/test/long")).request().get();
        response.bufferEntity();
        logger.info(response.readEntity(String.class));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals(WRONG_RESPONSE_ERROR_MSG, "123", response.getEntity());
        Assert.assertTrue(WRONG_PROVIDER_USED_ERROR_MSG, DefaultNumberWriterCustom.used);
    }

    /**
     * @tpTestDetails Tests Short object
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testShort() throws Exception {
        Client client = ClientBuilder.newClient();
        Response response = client.target(generateURL("/test/Short")).request().get();
        response.bufferEntity();
        logger.info(response.readEntity(String.class));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals(WRONG_RESPONSE_ERROR_MSG, "123", response.getEntity());
        Assert.assertTrue(WRONG_PROVIDER_USED_ERROR_MSG, DefaultNumberWriterCustom.used);
    }

    /**
     * @tpTestDetails Tests short primitive
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testShortPrimitive() throws Exception {
        Client client = ClientBuilder.newClient();
        Response response = client.target(generateURL("/test/short")).request().get();
        response.bufferEntity();
        logger.info(response.readEntity(String.class));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals(WRONG_RESPONSE_ERROR_MSG, "123", response.getEntity());
        Assert.assertTrue(WRONG_PROVIDER_USED_ERROR_MSG, DefaultNumberWriterCustom.used);
    }

    /**
     * @tpTestDetails Tests BigDecimal object
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testBigDecimal() throws Exception {
        Client client = ClientBuilder.newClient();
        Response response = client.target(generateURL("/test/bigDecimal")).request().get();
        response.bufferEntity();
        logger.info(response.readEntity(String.class));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals(WRONG_RESPONSE_ERROR_MSG, "123", response.getEntity());
        Assert.assertTrue(WRONG_PROVIDER_USED_ERROR_MSG, DefaultNumberWriterCustom.used);
    }

    /**
     * @tpTestDetails Tests BigDecimal object with register custom provider on client
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testProviderGetsUsed() throws Exception {
        Client client = ClientBuilder.newClient();
        client.register(DefaultNumberWriterCustom.class);
        Response response = client.target(generateURL("/test/bigDecimal")).request().get();
        response.bufferEntity();
        logger.info(response.readEntity(String.class));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals(WRONG_RESPONSE_ERROR_MSG, "123", response.getEntity());
        Assert.assertTrue(WRONG_PROVIDER_USED_ERROR_MSG, DefaultNumberWriterCustom.used);
    }
}
