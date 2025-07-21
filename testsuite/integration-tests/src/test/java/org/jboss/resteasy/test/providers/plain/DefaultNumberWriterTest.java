package org.jboss.resteasy.test.providers.plain;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.providers.plain.resource.DefaultNumberWriterCustom;
import org.jboss.resteasy.test.providers.plain.resource.DefaultNumberWriterResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Plain provider
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for DefaultNumberWriter provider.
 *                    Regression test for partial fix for JBEAP-2847.
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
public class DefaultNumberWriterTest {
    private static Logger logger = Logger.getLogger(DefaultNumberWriterTest.class);
    private static final String WRONG_RESPONSE_ERROR_MSG = "Response contains wrong response";
    private static final String WRONG_PROVIDER_USED_ERROR_MSG = "Wrong provider was used";

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(DefaultNumberWriterTest.class.getSimpleName());
        war.addClasses(TestUtil.class, PortProviderUtil.class);
        return TestUtil.finishContainerPrepare(war, null, DefaultNumberWriterResource.class,
                DefaultNumberWriterCustom.class);
    }

    protected Client client;

    @BeforeEach
    public void beforeTest() {
        client = ClientBuilder.newClient();
    }

    @AfterEach
    public void afterTest() {
        client.close();
        client = null;
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, DefaultNumberWriterTest.class.getSimpleName());
    }

    @AfterEach
    public void resetProviderFlag() {
        DefaultNumberWriterCustom.used = false;
    }

    /**
     * @tpTestDetails Tests Byte object
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testByte() throws Exception {
        Response response = client.target(generateURL("/test/Byte")).request().get();
        response.bufferEntity();
        logger.info(response.readEntity(String.class));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("123", response.getEntity(), WRONG_RESPONSE_ERROR_MSG);
        Assertions.assertTrue(DefaultNumberWriterCustom.used, WRONG_PROVIDER_USED_ERROR_MSG);
    }

    /**
     * @tpTestDetails Tests byte primitive
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testBytePrimitive() throws Exception {
        Response response = client.target(generateURL("/test/byte")).request().get();
        response.bufferEntity();
        logger.info(response.readEntity(String.class));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("123", response.getEntity(), WRONG_RESPONSE_ERROR_MSG);
        Assertions.assertTrue(DefaultNumberWriterCustom.used, WRONG_PROVIDER_USED_ERROR_MSG);
    }

    /**
     * @tpTestDetails Tests Double object
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testDouble() throws Exception {
        Response response = client.target(generateURL("/test/Double")).request().get();
        response.bufferEntity();
        logger.info(response.readEntity(String.class));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("123.4", response.getEntity(), WRONG_RESPONSE_ERROR_MSG);
        Assertions.assertTrue(DefaultNumberWriterCustom.used, WRONG_PROVIDER_USED_ERROR_MSG);
    }

    /**
     * @tpTestDetails Tests double primitive
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testDoublePrimitive() throws Exception {
        Response response = client.target(generateURL("/test/double")).request().get();
        response.bufferEntity();
        logger.info(response.readEntity(String.class));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("123.4", response.getEntity(), WRONG_RESPONSE_ERROR_MSG);
        Assertions.assertTrue(DefaultNumberWriterCustom.used, WRONG_PROVIDER_USED_ERROR_MSG);
    }

    /**
     * @tpTestDetails Tests Float object
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testFloat() throws Exception {
        Response response = client.target(generateURL("/test/Float")).request().get();
        response.bufferEntity();
        logger.info(response.readEntity(String.class));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("123.4", response.getEntity(), WRONG_RESPONSE_ERROR_MSG);
        Assertions.assertTrue(DefaultNumberWriterCustom.used, WRONG_PROVIDER_USED_ERROR_MSG);
    }

    /**
     * @tpTestDetails Tests float primitive
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testFloatPrimitive() throws Exception {
        Response response = client.target(generateURL("/test/float")).request().get();
        response.bufferEntity();
        logger.info(response.readEntity(String.class));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("123.4", response.getEntity(), WRONG_RESPONSE_ERROR_MSG);
        Assertions.assertTrue(DefaultNumberWriterCustom.used, WRONG_PROVIDER_USED_ERROR_MSG);
    }

    /**
     * @tpTestDetails Tests Integer object
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testInteger() throws Exception {
        Response response = client.target(generateURL("/test/Integer")).request().get();
        response.bufferEntity();
        logger.info(response.readEntity(String.class));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("123", response.getEntity(), WRONG_RESPONSE_ERROR_MSG);
        Assertions.assertTrue(DefaultNumberWriterCustom.used, WRONG_PROVIDER_USED_ERROR_MSG);
    }

    /**
     * @tpTestDetails Tests integer primitive
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testIntegerPrimitive() throws Exception {
        Response response = client.target(generateURL("/test/integer")).request().get();
        response.bufferEntity();
        logger.info(response.readEntity(String.class));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("123", response.getEntity(), WRONG_RESPONSE_ERROR_MSG);
        Assertions.assertTrue(DefaultNumberWriterCustom.used, WRONG_PROVIDER_USED_ERROR_MSG);
    }

    /**
     * @tpTestDetails Tests Long object
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testLong() throws Exception {
        Response response = client.target(generateURL("/test/Long")).request().get();
        response.bufferEntity();
        logger.info(response.readEntity(String.class));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("123", response.getEntity(), WRONG_RESPONSE_ERROR_MSG);
        Assertions.assertTrue(DefaultNumberWriterCustom.used, WRONG_PROVIDER_USED_ERROR_MSG);
    }

    /**
     * @tpTestDetails Tests long primitive
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testLongPrimitive() throws Exception {
        Response response = client.target(generateURL("/test/long")).request().get();
        response.bufferEntity();
        logger.info(response.readEntity(String.class));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("123", response.getEntity(), WRONG_RESPONSE_ERROR_MSG);
        Assertions.assertTrue(DefaultNumberWriterCustom.used, WRONG_PROVIDER_USED_ERROR_MSG);
    }

    /**
     * @tpTestDetails Tests Short object
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testShort() throws Exception {
        Response response = client.target(generateURL("/test/Short")).request().get();
        response.bufferEntity();
        logger.info(response.readEntity(String.class));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("123", response.getEntity(), WRONG_RESPONSE_ERROR_MSG);
        Assertions.assertTrue(DefaultNumberWriterCustom.used, WRONG_PROVIDER_USED_ERROR_MSG);
    }

    /**
     * @tpTestDetails Tests short primitive
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testShortPrimitive() throws Exception {
        Response response = client.target(generateURL("/test/short")).request().get();
        response.bufferEntity();
        logger.info(response.readEntity(String.class));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("123", response.getEntity(), WRONG_RESPONSE_ERROR_MSG);
        Assertions.assertTrue(DefaultNumberWriterCustom.used, WRONG_PROVIDER_USED_ERROR_MSG);
    }

    /**
     * @tpTestDetails Tests BigDecimal object
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testBigDecimal() throws Exception {
        Response response = client.target(generateURL("/test/bigDecimal")).request().get();
        response.bufferEntity();
        logger.info(response.readEntity(String.class));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("123", response.getEntity(), WRONG_RESPONSE_ERROR_MSG);
        Assertions.assertTrue(DefaultNumberWriterCustom.used, WRONG_PROVIDER_USED_ERROR_MSG);
    }

    /**
     * @tpTestDetails Tests BigDecimal object with register custom provider on client
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testProviderGetsUsed() throws Exception {
        client.register(DefaultNumberWriterCustom.class);
        Response response = client.target(generateURL("/test/bigDecimal")).request().get();
        response.bufferEntity();
        logger.info(response.readEntity(String.class));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("123", response.getEntity(), WRONG_RESPONSE_ERROR_MSG);
        Assertions.assertTrue(DefaultNumberWriterCustom.used, WRONG_PROVIDER_USED_ERROR_MSG);
    }
}
