package org.jboss.resteasy.test.resource.path;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.resource.path.resource.ResourceLocatorWithBaseNoExpressionResource;
import org.jboss.resteasy.test.resource.path.resource.ResourceLocatorWithBaseNoExpressionSubresource;
import org.jboss.resteasy.test.resource.path.resource.ResourceLocatorWithBaseNoExpressionSubresource2;
import org.jboss.resteasy.test.resource.path.resource.ResourceLocatorWithBaseNoExpressionSubresource3;
import org.jboss.resteasy.test.resource.path.resource.ResourceLocatorWithBaseNoExpressionSubresource3Interface;
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
 * @tpTestCaseDetails Check resources with locator with no expression
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ResourceLocatorWithBaseNoExpressionTest {
    private static final String ERROR_MSG = "Response contain wrong content";
    static Client client;

    @BeforeClass
    public static void setup() throws Exception {
        client = ClientBuilder.newClient();
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ResourceLocatorWithBaseNoExpressionTest.class.getSimpleName());
        war.addClasses(ResourceLocatorWithBaseNoExpressionSubresource.class,
                ResourceLocatorWithBaseNoExpressionSubresource2.class,
                ResourceLocatorWithBaseNoExpressionSubresource3.class,
                ResourceLocatorWithBaseNoExpressionSubresource3Interface.class);
        return TestUtil.finishContainerPrepare(war, null, ResourceLocatorWithBaseNoExpressionResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ResourceLocatorWithBaseNoExpressionTest.class.getSimpleName());
    }

    @AfterClass
    public static void close() throws Exception {
        client.close();
    }

    @AfterClass
    public static void after() throws Exception {

    }

    /**
     * @tpTestDetails Test for root resource and for subresource.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testSubresource() throws Exception {
        {
            Response response = client.target(generateURL("/a1/base/1/resources")).request().get();
            Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
            Assert.assertEquals(ERROR_MSG, ResourceLocatorWithBaseNoExpressionSubresource.class.getName(),
                    response.readEntity(String.class));
            response.close();
        }
        {
            Response response = client.target(generateURL("/a1/base/1/resources/subresource2/stuff/2/bar")).request().get();
            Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
            Assert.assertEquals(ERROR_MSG, ResourceLocatorWithBaseNoExpressionSubresource2.class.getName() + "-2",
                    response.readEntity(String.class));
            response.close();
        }
    }

}
