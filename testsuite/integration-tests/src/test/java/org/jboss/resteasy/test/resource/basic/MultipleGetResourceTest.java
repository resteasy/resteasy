package org.jboss.resteasy.test.resource.basic;

import java.util.HashMap;
import java.util.Map;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.test.resource.basic.resource.MultipleGetResource;
import org.jboss.resteasy.utils.LogCounter;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.wildfly.arquillian.junit.annotations.RequiresModule;

/**
 * Verify that setting resteasy config flag, resteasy_fail_fast to 'true' causes
 * resteasy to report error and not warning.
 * This feature is provided for quarkus.
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class MultipleGetResourceTest {
    static ResteasyClient client;

    @Deployment
    public static Archive<?> testReturnValuesDeploy() throws Exception {
        WebArchive war = TestUtil.prepareArchive(MultipleGetResourceTest.class.getSimpleName());
        Map<String, String> contextParam = new HashMap<>();
        contextParam.put(ResteasyContextParameters.RESTEASY_FAIL_FAST_ON_MULTIPLE_RESOURCES_MATCHING, "true");
        return TestUtil.finishContainerPrepare(war, contextParam, MultipleGetResource.class);
    }

    @BeforeAll
    public static void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterAll
    public static void close() {
        client.close();
        client = null;
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, MultipleGetResourceTest.class.getSimpleName());
    }

    @Test
    @RequiresModule(value = "org.jboss.resteasy.resteasy-core", minVersion = "7.0.6.Final")
    public void testFailFast() throws Exception {
        final LogCounter resteasy005042Log = new LogCounter("RESTEASY005042",
                false);

        WebTarget base = client.target(generateURL("/api"));
        try (Response response = base.request().get()) {
            Assertions.assertEquals(500, response.getStatus());
            // We must expect an empty body, see https://redhat.atlassian.net/browse/RESTEASY-3823
            Assertions.assertFalse(response.hasEntity(),
                    "Default exception mapper must not disclose the exception message in the response body");
            Assertions.assertTrue(resteasy005042Log.count() > 0,
                    "RESTEASY005042 should be logged in the server log");
        }
    }
}
