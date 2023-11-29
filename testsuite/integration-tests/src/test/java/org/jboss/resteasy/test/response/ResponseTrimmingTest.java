package org.jboss.resteasy.test.response;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.jboss.resteasy.test.response.resource.ResponseTrimmingResource;
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
 * @tpSubChapter Response
 * @tpChapter Integration tests
 * @tpTestCaseDetails Ensures that response is not too long after endpoint consumes big invalid data (see
 *                    https://issues.jboss.org/browse/JBEAP-6316)
 * @tpSince RESTEasy 3.6.1
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ResponseTrimmingTest {

    static Client client;
    private static String original;
    private static String trimmed;
    private static final String DEFAULT = "war_default";
    private static final String NO_JSON_B = "war_no_json_b";

    /**
     * Prepare deployment with default configuration. JSON-B will be used.
     */
    @Deployment(name = DEFAULT)
    public static Archive<?> deployDefault() {
        WebArchive war = TestUtil.prepareArchive(DEFAULT);
        return TestUtil.finishContainerPrepare(war, null, ResponseTrimmingResource.class);
    }

    /**
     * Prepare deployment with jboss-deployment-structure-no-json-b.xml. Jackson will be used.
     */
    @Deployment(name = NO_JSON_B)
    public static Archive<?> deployNoJsonB() {
        WebArchive war = TestUtil.prepareArchive(NO_JSON_B);
        war.addAsManifestResource("jboss-deployment-structure-no-json-b.xml", "jboss-deployment-structure.xml");
        return TestUtil.finishContainerPrepare(war, null, ResponseTrimmingResource.class);
    }

    /**
     * Prepare string for tests and its trimmed version.
     */
    @BeforeClass
    public static void init() {
        client = new ResteasyClientBuilderImpl().build();

        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 1024; i++) {
            sb.append("A");
        }
        original = sb.toString();

        StringBuilder sb2 = new StringBuilder();
        for (int i = 1; i <= 256; i++) {
            sb2.append("A");
        }
        trimmed = sb2.toString();
    }

    @AfterClass
    public static void after() throws Exception {
        client.close();
    }

    /**
     * @tpTestDetails Test long error message trimming with JsonB
     * @tpSince RESTEasy 3.6.1.Final
     */
    @Test
    public void testDefault() {
        test(DEFAULT);
    }

    /**
     * @tpTestDetails Test long error message trimming with Jackson
     * @tpSince RESTEasy 3.6.1.Final
     */
    @Test
    public void testNoJsonB() {
        test(NO_JSON_B);
    }

    /**
     * Send long string to the endpoint that expects int so error message is returned in response.
     * Check that response does not contain full string and has reasonable length.
     *
     * @param deployment DEFAULT (use JSON-B) or NO_JSON_B (use Jackson)
     */
    private void test(String deployment) {
        Response response = client.target(PortProviderUtil.generateURL("/json", deployment)).request()
                .post(Entity.entity(original, "application/json"));
        String responseText = response.readEntity(String.class);

        if (deployment.equals(NO_JSON_B)) {
            Assert.assertTrue(
                    "Expected response to contain \"Not able to deserialize data provided\" but was \"" + response + "\"",
                    responseText.contains("Not able to deserialize data provided"));
        }
        Assert.assertTrue("Response is longer than 550 characters", responseText.length() <= 550);

        response.close();
    }
}
