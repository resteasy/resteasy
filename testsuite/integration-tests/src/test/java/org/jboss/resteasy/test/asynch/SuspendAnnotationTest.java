package org.jboss.resteasy.test.asynch;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.asynch.resource.LegacySuspendResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Asynchronous RESTEasy
 * @tpChapter Integration tests
 * @tpTestCaseDetails Basic asynchronous test for suspended response.
 *                    Test for org.jboss.resteasy.annotations.Suspend annotation
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class SuspendAnnotationTest {

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(JaxrsAsyncTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, LegacySuspendResource.class);
    }

    private static String generateURL(String path) {
        return PortProviderUtil.generateURL(path, JaxrsAsyncTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Server is able to answer in requested time.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testPositive() throws Exception {
        Client client = ClientBuilder.newClient();
        Response response = client.target(generateURL("")).request().get();

        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("hello", response.readEntity(String.class), "Wrong content of response");

        response.close();
        client.close();
    }

    /**
     * @tpTestDetails Server is not able to answer in requested time.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testTimeout() throws Exception {
        Client client = ClientBuilder.newClient();
        Response response = client.target(generateURL("/timeout")).request().get();

        Assertions.assertEquals(HttpResponseCodes.SC_SERVICE_UNAVAILABLE, response.getStatus());

        response.close();
        client.close();
    }
}
