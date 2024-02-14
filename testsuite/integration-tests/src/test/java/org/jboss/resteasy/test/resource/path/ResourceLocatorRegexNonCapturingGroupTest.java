package org.jboss.resteasy.test.resource.path;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.test.resource.path.resource.ResourceLocatorRegexCapturingGroupSubResourceNoPath;
import org.jboss.resteasy.test.resource.path.resource.ResourceLocatorRegexCapturingGroupSubResourceWithPath;
import org.jboss.resteasy.test.resource.path.resource.ResourceLocatorRegexNonCapturingGroup;
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
 * @Path annotation paths can consist of Regex Non-Capturing groups used with
 *       Resource Locator scenarios.
 *
 *       User: rsearls
 *       Date: 2/18/17
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ResourceLocatorRegexNonCapturingGroupTest {
    private static final String ERROR_MSG = "Response contain wrong content";
    static Client client;

    @BeforeAll
    public static void setup() throws Exception {
        client = ClientBuilder.newClient();
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ResourceLocatorRegexNonCapturingGroupTest.class.getSimpleName());
        war.addClasses(ResourceLocatorRegexCapturingGroupSubResourceNoPath.class,
                ResourceLocatorRegexCapturingGroupSubResourceWithPath.class);
        war.addAsWebInfResource(ResourceLocatorRegexNonCapturingGroupTest.class.getPackage(), "web.xml", "web.xml");
        return TestUtil.finishContainerPrepare(war, null, ResourceLocatorRegexNonCapturingGroup.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ResourceLocatorRegexNonCapturingGroupTest.class.getSimpleName());
    }

    @AfterAll
    public static void close() throws Exception {
        client.close();
    }

    @AfterAll
    public static void after() throws Exception {

    }

    @Test
    public void testBird() throws Exception {
        {
            Response response = client.target(generateURL("/noCapture/nobird")).request().get();
            Assertions.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
            Assertions.assertEquals("nobird success", response.readEntity(String.class),
                    ERROR_MSG);
            response.close();
        }

        {
            Response response = client.target(generateURL("/noCapture/BIRD/test")).request().get();
            Assertions.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
            Assertions.assertEquals("BIRD test", response.readEntity(String.class),
                    ERROR_MSG);
            response.close();
        }
    }

    @Test
    public void testFly() throws Exception {
        {
            Response response = client.target(generateURL("/noCapture/a/nofly/b")).request().get();
            Assertions.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
            Assertions.assertEquals("a/nofly/b success", response.readEntity(String.class),
                    ERROR_MSG);
            response.close();
        }

        {
            Response response = client.target(generateURL("/noCapture/a/FLY/b/test")).request().get();
            Assertions.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
            Assertions.assertEquals("a/FLY/b test", response.readEntity(String.class),
                    ERROR_MSG);
            response.close();
        }
    }
}
