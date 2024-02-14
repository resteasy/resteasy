package org.jboss.resteasy.test.providers.custom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.providers.custom.resource.CustomClientConstrainedFeature;
import org.jboss.resteasy.test.providers.custom.resource.CustomConstrainedFeatureResource;
import org.jboss.resteasy.test.providers.custom.resource.CustomServerConstrainedFeature;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Core
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.6.1
 * @tpTestCaseDetails Regression test for RESTEASY-1861
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class CustomConstrainedFeatureTest {

    private static final String TEST_URI = generateURL("/test-custom-feature");
    private static final Logger LOGGER = Logger.getLogger(CustomConstrainedFeatureTest.class.getName());
    private static final String CUSTOM_PROVIDERS_FILENAME = "CustomConstrainedFeature.Providers";

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(CustomConstrainedFeatureTest.class.getSimpleName());
        war.addAsResource(CustomConstrainedFeatureTest.class.getPackage(), CUSTOM_PROVIDERS_FILENAME,
                "META-INF/services/jakarta.ws.rs.ext.Providers");
        return TestUtil.finishContainerPrepare(war, null, CustomConstrainedFeatureResource.class,
                CustomServerConstrainedFeature.class, CustomClientConstrainedFeature.class);
    }

    private static String generateURL(String path) {
        return PortProviderUtil.generateURL(path, CustomConstrainedFeatureTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Call client with restricted feature for server runtime.
     * @tpSince RESTEasy 3.6.1
     */
    @Test
    public void testClientCall() {
        CustomServerConstrainedFeature.reset();
        CustomClientConstrainedFeature.reset();
        // This will register always in SERVER runtime
        // ResteasyProviderFactory providerFactory = ResteasyProviderFactory.newInstance();
        // providerFactory.register(CustomClientConstrainedFeature.class);
        // providerFactory.register(CustomServerConstrainedFeature.class);
        // ResteasyClientImpl client = new ResteasyClientBuilderImpl().build();
        // the line below does the same as if there is providers file in META-INF/services/jakarta.ws.rs.ext.Providers
        ResteasyClient client = (ResteasyClient) ClientBuilder.newBuilder().register(CustomClientConstrainedFeature.class)
                .register(CustomServerConstrainedFeature.class).build();
        assertTrue(CustomClientConstrainedFeature.wasInvoked(), CustomConstrainedFeatureResource.ERROR_CLIENT_FEATURE);
        assertFalse(CustomServerConstrainedFeature.wasInvoked(), CustomConstrainedFeatureResource.ERROR_SERVER_FEATURE);
        Response response = client.target(TEST_URI).request().get();
        LOGGER.infof("Response from server: %s", response.readEntity(String.class));
        // server must return 200 if only registered feature was for server runtime
        assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        client.close();
    }
}
