package org.jboss.resteasy.test.resource.request;

import java.util.regex.Pattern;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.resource.request.resource.RequestResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Resource
 * @tpChapter Integration tests
 * @tpTestCaseDetails Tests for ResteasyRequest
 * @tpSince RESTEasy 4.3.2
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ResteasyRequestTest {

    static Client client;
    static WebTarget requestWebTarget;

    @BeforeAll
    public static void before() throws Exception {
        client = ClientBuilder.newClient();
        requestWebTarget = client.target(generateURL("/request"));
    }

    @AfterAll
    public static void close() {
        client.close();
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ResteasyRequestTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, RequestResource.class);
    }

    private static String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ResteasyRequestTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Checks ResteasyRequest
     * @tpSince RESTEasy 4.3.2
     */
    @Test
    public void testRequest() {
        try {
            Response response = requestWebTarget.request().get();
            Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            final String val = response.readEntity(String.class);
            final String pattern = "^127.0.0.1/.+";
            Assertions.assertTrue(Pattern.matches(pattern, val),
                    String.format("Expected value '%s' to match pattern '%s'", val, pattern));
            response.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
