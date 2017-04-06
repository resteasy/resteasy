package org.jboss.resteasy.test.resource.path;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.resource.path.resource.ResourceLocatorRegexCapturingGroup;
import org.jboss.resteasy.test.resource.path.resource.ResourceLocatorRegexCapturingGroupSubResourceNoPath;
import org.jboss.resteasy.test.resource.path.resource.ResourceLocatorRegexCapturingGroupSubResourceWithPath;
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
 * @tpSubChapter Resources
 * @tpChapter Integration tests
 * @tpTestCaseDetails @Path annotation paths can consist of Regex Capturing groups used with
 * Resource Locator scenarios.
 * @tpSince RESTEasy 3.0.22
 *
 * User: rsearls
 * Date: 2/17/17
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ResourceLocatorRegexCapturingGroupTest {
    private static final String ERROR_MSG = "Response contain wrong content";
    static Client client;

    @BeforeClass
    public static void setup() throws Exception {
        client = ClientBuilder.newClient();
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ResourceLocatorRegexCapturingGroupTest.class.getSimpleName());
        war.addClasses(ResourceLocatorRegexCapturingGroupSubResourceNoPath.class,
            ResourceLocatorRegexCapturingGroupSubResourceWithPath.class);
        war.addAsWebInfResource(ResourceLocatorRegexCapturingGroupTest.class.getPackage(), "web.xml", "web.xml");
        return TestUtil.finishContainerPrepare(war, null, ResourceLocatorRegexCapturingGroup.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ResourceLocatorRegexCapturingGroupTest.class.getSimpleName());
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
    public void testBasic() throws Exception {
        {
            Response response = client.target(generateURL("/capture/basic")).request().get();
            Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
            Assert.assertEquals(ERROR_MSG, "basic success", response.readEntity(String.class));
            response.close();
        }
        {
            Response response = client.target(generateURL("/capture/BASIC/test")).request().get();
            Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
            Assert.assertEquals(ERROR_MSG, "BASIC test", response.readEntity(String.class));
            response.close();
        }
    }

    @Test
    public void testBird() throws Exception {
        {
            Response response = client.target(generateURL("/capture/nobird")).request().get();
            Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
            Assert.assertEquals(ERROR_MSG, "nobird success", response.readEntity(String.class));
            response.close();
        }

        {
            Response response = client.target(generateURL("/capture/BIRD/test")).request().get();
            Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
            Assert.assertEquals(ERROR_MSG, "BIRD test", response.readEntity(String.class));
            response.close();
        }
    }

    @Test
    public void testFly() throws Exception {
        {
            Response response = client.target(generateURL("/capture/a/nofly/b")).request().get();
            Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
            Assert.assertEquals(ERROR_MSG, "a/nofly/b success", response.readEntity(String.class));
            response.close();
        }

        {
            Response response = client.target(generateURL("/capture/a/FLY/b/test")).request().get();
            Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
            Assert.assertEquals(ERROR_MSG, "a/FLY/b test", response.readEntity(String.class));
            response.close();
        }
    }
}
