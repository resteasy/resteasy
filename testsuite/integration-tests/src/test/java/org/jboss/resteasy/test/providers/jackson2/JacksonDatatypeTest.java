package org.jboss.resteasy.test.providers.jackson2;

import java.util.HashMap;
import java.util.Map;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.core.basic.resource.ApplicationTestScannedApplication;
import org.jboss.resteasy.test.providers.jackson2.resource.JacksonDatatypeEndPoint;
import org.jboss.resteasy.test.providers.jackson2.resource.JacksonDatatypeJacksonProducer;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Jackson2 provider
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for WFLY-5916. Integration tests for jackson-datatype-jsr310 and jackson-datatype-jdk8 modules
 * @tpSince RESTEasy 3.1.0.CR3
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class JacksonDatatypeTest {
    private static final String DEFAULT_DEPLOYMENT = String.format("%sDefault",
            JacksonDatatypeTest.class.getSimpleName());
    private static final String DEPLOYMENT_WITH_DATATYPE = String.format("%sWithDatatypeSupport",
            JacksonDatatypeTest.class.getSimpleName());

    static ResteasyClient client;
    protected static final Logger logger = Logger.getLogger(JacksonDatatypeTest.class.getName());

    @BeforeAll
    public static void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterAll
    public static void close() {
        client.close();
    }

    @Deployment(name = "default")
    public static Archive<?> deploy() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, DEFAULT_DEPLOYMENT + ".war");
        Map<String, String> contextParam = new HashMap<>();
        contextParam.put(ResteasyContextParameters.RESTEASY_PREFER_JACKSON_OVER_JSONB, "true");
        return TestUtil.finishContainerPrepare(war, contextParam, ApplicationTestScannedApplication.class,
                JacksonDatatypeEndPoint.class);
    }

    @Deployment(name = "withDatatype")
    public static Archive<?> deployJackson() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, DEPLOYMENT_WITH_DATATYPE + ".war");
        Map<String, String> contextParam = new HashMap<>();
        contextParam.put(ResteasyContextParameters.RESTEASY_PREFER_JACKSON_OVER_JSONB, "true");
        return TestUtil.finishContainerPrepare(war, contextParam, JacksonDatatypeEndPoint.class,
                JacksonDatatypeJacksonProducer.class, ApplicationTestScannedApplication.class);
    }

    private String requestHelper(String endPath, String deployment) {
        String url = PortProviderUtil.generateURL(String.format("/scanned/%s", endPath), deployment);
        WebTarget base = client.target(url);
        Response response = base.request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String strResponse = response.readEntity(String.class);
        logger.info(String.format("Url: %s", url));
        logger.info(String.format("Response: %s", strResponse));
        return strResponse;
    }

    /**
     * @tpTestDetails Check string type without datatype supported
     * @tpSince RESTEasy 3.1.0.CR3
     */
    @Test
    public void testDatatypeNotSupportedString() throws Exception {
        String strResponse = requestHelper("string", DEFAULT_DEPLOYMENT);
        Assertions.assertTrue(strResponse.contains("someString"), "Wrong conversion of String");
    }

    /**
     * @tpTestDetails Check date type without datatype supported
     * @tpSince RESTEasy 3.1.0.CR3
     */
    @Test
    public void testDatatypeNotSupportedDate() throws Exception {
        String strResponse = requestHelper("date", DEFAULT_DEPLOYMENT);
        Assertions.assertTrue(strResponse.matches("^[0-9]*$"), "Wrong conversion of Date");
    }

    /**
     * @tpTestDetails Check null optional type without datatype supported
     * @tpSince RESTEasy 3.1.0.CR3
     */
    @Test
    public void testDatatypeNotSupportedOptionalNull() throws Exception {
        String strResponse = requestHelper("optional/true", DEFAULT_DEPLOYMENT);
        Assertions.assertFalse(strResponse.contains("null"), "Wrong conversion of Optional (null)");
    }

    /**
     * @tpTestDetails Check not null optional type without datatype supported
     * @tpSince RESTEasy 3.1.0.CR3
     */
    @Test
    public void testDatatypeNotSupportedOptionalNotNull() throws Exception {
        String strResponse = requestHelper("optional/false", DEFAULT_DEPLOYMENT);
        Assertions.assertFalse(strResponse.contains("info@example.com"),
                "Wrong conversion of Optional (not null)");
    }

    /**
     * @tpTestDetails Check string type with datatype supported
     * @tpSince RESTEasy 3.1.0.CR3
     */
    @Test
    public void testDatatypeSupportedString() throws Exception {
        String strResponse = requestHelper("string", DEPLOYMENT_WITH_DATATYPE);
        Assertions.assertTrue(strResponse.contains("someString"), "Wrong conversion of String");
    }

    /**
     * @tpTestDetails Check date type with datatype supported
     * @tpSince RESTEasy 3.1.0.CR3
     */
    @Test
    public void testDatatypeSupportedDate() throws Exception {
        String strResponse = requestHelper("date", DEPLOYMENT_WITH_DATATYPE);
        Assertions.assertFalse(strResponse.matches("^[0-9]*$"), "Wrong conversion of Date");
    }

    /**
     * @tpTestDetails Check duration type with datatype supported
     * @tpSince RESTEasy 3.1.0.CR3
     */
    @Test
    public void testDatatypeSupportedDuration() throws Exception {
        String strResponse = requestHelper("duration", DEPLOYMENT_WITH_DATATYPE);
        Assertions.assertTrue(strResponse.contains("5.000000006"), "Wrong conversion of Duration");
    }

    /**
     * @tpTestDetails Check null optional type with datatype supported
     * @tpSince RESTEasy 3.1.0.CR3
     */
    @Test
    public void testDatatypeSupportedOptionalNull() throws Exception {
        String strResponse = requestHelper("optional/true", DEPLOYMENT_WITH_DATATYPE);
        Assertions.assertTrue(strResponse.contains("null"), "Wrong conversion of Optional (null)");
    }

    /**
     * @tpTestDetails Check not null optional type with datatype supported
     * @tpSince RESTEasy 3.1.0.CR3
     */
    @Test
    public void testDatatypeSupportedOptionalNotNull() throws Exception {
        String strResponse = requestHelper("optional/false", DEPLOYMENT_WITH_DATATYPE);
        Assertions.assertTrue(strResponse.contains("info@example.com"),
                "Wrong conversion of Optional (not null)");
    }
}
