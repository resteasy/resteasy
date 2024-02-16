package org.jboss.resteasy.test.resource.path;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.test.resource.path.resource.MultipleMatrixSegmentsResource;
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
 * @tpTestCaseDetails Test that a locator and resource with same path params work
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class MultipleMatrixSegmentsTest {

    static Client client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(MultipleMatrixSegmentsTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, MultipleMatrixSegmentsResource.class);
    }

    @BeforeAll
    public static void init() {
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void after() throws Exception {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, MultipleMatrixSegmentsTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Test segments on start and on end of path
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMultipleStartAndEnd() throws Exception {
        Response response = client.target(generateURL("/;name=bill;ssn=111/children/;name=skippy;ssn=3344")).request().get();
        Assertions.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        response.close();
    }

    /**
     * @tpTestDetails Test segments in the middle of path
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMultipleMiddle() throws Exception {
        Response response = client.target(generateURL("/stuff/;name=first;ssn=111/;name=second;ssn=3344/first")).request()
                .get();
        Assertions.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        response.close();
    }
}
