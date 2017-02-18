package org.jboss.resteasy.test.resteasy1569;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.resteasy1569.*;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
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
import java.net.URL;

/**
 * @Path annotation paths can consist of Regex Non-Capturing groups  used with
 * Resource Locator scenarios.
 *
 * User: rsearls
 * Date: 2/18/17
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ResourceLocatorRegexNonCapturingGroupTest {
    private static final String ERROR_MSG = "Response contain wrong content";
    static Client client;

    @ArquillianResource
    private URL baseURL;

    @BeforeClass
    public static void setup() throws Exception {
        client = ClientBuilder.newClient();
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, ResourceLocatorRegexNonCapturingGroupTest.class.getSimpleName() + ".war");
        war.addClass(TestApplication.class);
        war.addClasses(ResourceLocatorRegexCapturingGroupSubResourceNoPath.class,
                ResourceLocatorRegexCapturingGroupSubResourceWithPath.class,
                ResourceLocatorRegexCapturingGroup.class,
                ResourceLocatorRegexNonCapturingGroup.class);
        war.addAsWebInfResource(ResourceLocatorRegexNonCapturingGroupTest.class.getPackage(), "web.xml", "web.xml");

        return war;
    }

    private String generateURL(String path) {
        String testName = ResourceLocatorRegexNonCapturingGroupTest.class.getSimpleName();
        return String.format("http://%s:%d/%s%s", baseURL.getHost(), baseURL.getPort(), testName, path);
    }

    @AfterClass
    public static void close() throws Exception {
        client.close();
    }

    @AfterClass
    public static void after() throws Exception {

    }

    @Test
    public void testBird() throws Exception {
        {
            Response response = client.target(generateURL("/noCapture/nobird")).request().get();
            Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
            Assert.assertEquals(ERROR_MSG, "nobird success", response.readEntity(String.class));
            response.close();
        }

        {
            Response response = client.target(generateURL("/noCapture/BIRD/test")).request().get();
            Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
            Assert.assertEquals(ERROR_MSG, "BIRD test", response.readEntity(String.class));
            response.close();
        }
    }

    @Test
    public void testFly() throws Exception {
        {
            Response response = client.target(generateURL("/noCapture/a/nofly/b")).request().get();
            Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
            Assert.assertEquals(ERROR_MSG, "a/nofly/b success", response.readEntity(String.class));
            response.close();
        }

        {
            Response response = client.target(generateURL("/noCapture/a/FLY/b/test")).request().get();
            Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
            Assert.assertEquals(ERROR_MSG, "a/FLY/b test", response.readEntity(String.class));
            response.close();
        }
    }
}