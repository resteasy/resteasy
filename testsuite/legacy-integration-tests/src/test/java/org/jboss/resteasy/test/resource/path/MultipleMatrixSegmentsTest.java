package org.jboss.resteasy.test.resource.path;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.resource.path.resource.MultipleMatrixSegmentsResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Resource
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test that a locator and resource with same path params work
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class MultipleMatrixSegmentsTest {

    static Client client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(MultipleMatrixSegmentsTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, MultipleMatrixSegmentsResource.class);
    }

    @BeforeClass
    public static void init() {
        client = ClientBuilder.newClient();
    }

    @AfterClass
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
        Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        response.close();
    }

    /**
     * @tpTestDetails Test segments in the middle of path
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMultipleMiddle() throws Exception {
        Response response = client.target(generateURL("/stuff/;name=first;ssn=111/;name=second;ssn=3344/first")).request().get();
        Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        response.close();
    }
}
