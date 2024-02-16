package org.jboss.resteasy.test.resource.path;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.test.resource.path.resource.ResourceLocatorWithBaseNoExpressionResource;
import org.jboss.resteasy.test.resource.path.resource.ResourceLocatorWithBaseNoExpressionSubresource;
import org.jboss.resteasy.test.resource.path.resource.ResourceLocatorWithBaseNoExpressionSubresource2;
import org.jboss.resteasy.test.resource.path.resource.ResourceLocatorWithBaseNoExpressionSubresource3;
import org.jboss.resteasy.test.resource.path.resource.ResourceLocatorWithBaseNoExpressionSubresource3Interface;
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
 * @tpTestCaseDetails Check resources with locator with no expression
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ResourceLocatorWithBaseNoExpressionTest {
    private static final String ERROR_MSG = "Response contain wrong content";
    static Client client;

    @BeforeAll
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

    @AfterAll
    public static void close() throws Exception {
        client.close();
    }

    @AfterAll
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
            Assertions.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
            Assertions.assertEquals(ResourceLocatorWithBaseNoExpressionSubresource.class.getName(),
                    response.readEntity(String.class), ERROR_MSG);
            response.close();
        }
        {
            Response response = client.target(generateURL("/a1/base/1/resources/subresource2/stuff/2/bar")).request().get();
            Assertions.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
            Assertions.assertEquals(ResourceLocatorWithBaseNoExpressionSubresource2.class.getName() + "-2",
                    response.readEntity(String.class), ERROR_MSG);
            response.close();
        }
    }

}
