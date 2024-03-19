package org.jboss.resteasy.test.providers.jackson2;

import java.util.HashMap;
import java.util.Map;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.test.providers.jackson2.resource.MyEntity;
import org.jboss.resteasy.test.providers.jackson2.resource.PreferJacksonOverJsonBClientResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Check that resteasy.preferJacksonOverJsonB property works correctly on client used on server deployment.
 * @tpChapter Integration test
 * @tpSince RESTEasy 3.3
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class PreferJacksonOverJsonBClientTest {

    protected static final Logger LOG = Logger.getLogger(PreferJacksonOverJsonBClientTest.class.getName());

    static Client client;

    private static final String WAR_WITH_JSONB = "war_with_jsonb";
    private static final String WAR_WITH_JACKSON2 = "war_with_jackson2";

    /**
     * Prepare deployment with resteasy.preferJacksonOverJsonB = false
     */
    @Deployment(name = WAR_WITH_JSONB)
    public static Archive<?> deployWithJsonB() {
        return deploy(WAR_WITH_JSONB, false);
    }

    /**
     * Prepare deployment with resteasy.preferJacksonOverJsonB = true
     */
    @Deployment(name = WAR_WITH_JACKSON2)
    public static Archive<?> deployWithoutJsonB() {
        return deploy(WAR_WITH_JACKSON2, true);
    }

    /**
     * Prepare deployment with specific archive name and specific resteasy.preferJacksonOverJsonB value
     */
    public static Archive<?> deploy(String archiveName, Boolean useJackson) {
        WebArchive war = TestUtil.prepareArchive(archiveName);
        war.addClass(MyEntity.class);
        Map<String, String> contextParams = new HashMap<>();
        contextParams.put(ResteasyContextParameters.RESTEASY_PREFER_JACKSON_OVER_JSONB, useJackson.toString());
        war.setManifest(new StringAsset("Manifest-Version: 1.0\n"
                + "Dependencies: org.jboss.resteasy.resteasy-json-binding-provider services\n"));
        return TestUtil.finishContainerPrepare(war, contextParams, PreferJacksonOverJsonBClientResource.class);
    }

    @BeforeAll
    public static void setup() {
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void cleanup() {
        client.close();
    }

    /**
     * @tpTestDetails Set resteasy.preferJacksonOverJsonB=false, check that JsonB is used on client
     * @tpSince RESTEasy 3.6.1.Final
     */
    @Test
    public void testJsonB() {
        test(WAR_WITH_JSONB);
    }

    /**
     * @tpTestDetails Set resteasy.preferJacksonOverJsonB=true, check that Jackson is used on client
     * @tpSince RESTEasy 3.6.1.Final
     */
    @Test
    public void testJackson() {
        test(WAR_WITH_JACKSON2);
    }

    /**
     * Perform HTTP call, end-point performs another HTTP call and receives Date object, converts it to String and send back in
     * plain text
     * Json-B returns "2018-10-12T15:40:30.485Z[UTC]" but Jackson2 does not support Date so it returns unix time in seconds e.g.
     * 1539358801324
     */
    private void test(String deployment) {

        WebTarget target = client.target(PortProviderUtil.generateURL("/call", deployment));
        Response response = target.request()
                .header("clientURL", PortProviderUtil.generateURL("/core", deployment))
                .get();
        String responseText = response.readEntity(String.class);
        LOG.info("Response: " + responseText);

        if (deployment.equals(WAR_WITH_JACKSON2)) {
            Assertions.assertTrue(responseText.matches("^[0-9]*$"), "Jackson2 not used.");
        } else {
            Assertions.assertFalse(responseText.matches("^[0-9]*$"), "Json-B not used.");
        }
    }
}
