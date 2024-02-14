package org.jboss.resteasy.test.asynch;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.asynch.resource.JaxrsAsyncResource;
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
 * @tpTestCaseDetails Basic asynchronous test. Resource creates new threads.
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class JaxrsAsyncTest {

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(JaxrsAsyncTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, JaxrsAsyncResource.class);
    }

    private static String generateURL(String path) {
        return PortProviderUtil.generateURL(path, JaxrsAsyncTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Correct response excepted.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testSuccess() throws Exception {
        Client client = ClientBuilder.newClient();
        Response response = client.target(generateURL("")).request().get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("hello", response.readEntity(String.class), "Wrong response");
        response.close();
        client.close();
    }

    /**
     * @tpTestDetails Timeout exception should be thrown.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testTimeout() throws Exception {
        Client client = ClientBuilder.newClient();
        Response response = client.target(generateURL("/timeout")).request().get();
        Assertions.assertEquals(503, response.getStatus());
        response.close();
        client.close();
    }

    /**
     * @tpTestDetails Negative timeout value is set to response in end-point. Regression test for JBEAP-4695.
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    public void testNegativeTimeout() throws Exception {
        Client client = ClientBuilder.newClient();
        Response response = client.target(generateURL("/negative")).request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("hello", response.readEntity(String.class), "Wrong response");
        response.close();
        client.close();
    }

    /**
     * @tpTestDetails Zero timeout value is set to response in end-point. Regression test for JBEAP-4695.
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    public void testZeroTimeout() throws Exception {
        Client client = ClientBuilder.newClient();
        Response response = client.target(generateURL("/zero")).request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("hello", response.readEntity(String.class), "Wrong response");
        response.close();
        client.close();
    }
}
