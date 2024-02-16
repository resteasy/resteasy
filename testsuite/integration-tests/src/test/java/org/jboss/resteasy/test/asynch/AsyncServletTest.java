package org.jboss.resteasy.test.asynch;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.asynch.resource.AsyncServletResource;
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
 * @tpSubChapter Asynchronous RESTEasy
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for asyncHttpServlet module
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class AsyncServletTest {

    static ResteasyClient client;

    @BeforeEach
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
    }

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(AsyncServletTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, AsyncServletResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, AsyncServletTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Test for correct response
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testAsync() throws Exception {
        Response response = client.target(generateURL("/async")).request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("hello", response.readEntity(String.class),
                "Wrong response content");
    }

    /**
     * @tpTestDetails Service unavailable test
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testTimeout() throws Exception {
        Response response = client.target(generateURL("/async/timeout")).request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_SERVICE_UNAVAILABLE, response.getStatus());
    }
}
