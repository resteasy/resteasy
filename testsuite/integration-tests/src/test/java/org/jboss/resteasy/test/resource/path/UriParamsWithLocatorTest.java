package org.jboss.resteasy.test.resource.path;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.test.resource.path.resource.UriParamsWithLocatorLocator;
import org.jboss.resteasy.test.resource.path.resource.UriParamsWithLocatorLocator2;
import org.jboss.resteasy.test.resource.path.resource.UriParamsWithLocatorResource;
import org.jboss.resteasy.test.resource.path.resource.UriParamsWithLocatorResource2;
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
 * @tpSubChapter UriParamsWithLocatorResource
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 * @tpTestCaseDetails Test that a locator and resource with same path params work
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class UriParamsWithLocatorTest {
    static Client client;

    @BeforeAll
    public static void setup() throws Exception {
        client = ClientBuilder.newClient();
    }

    @Deployment(name = "one")
    public static Archive<?> deploy1() {
        WebArchive war = TestUtil.prepareArchive("one");
        war.addClass(UriParamsWithLocatorResource.class);
        return TestUtil.finishContainerPrepare(war, null, UriParamsWithLocatorLocator.class);
    }

    @Deployment(name = "two")
    public static Archive<?> deploy2() {
        WebArchive war = TestUtil.prepareArchive("two");
        war.addClass(UriParamsWithLocatorResource2.class);
        return TestUtil.finishContainerPrepare(war, null, UriParamsWithLocatorLocator2.class);
    }

    @AfterAll
    public static void close() throws Exception {
        client.close();
    }

    /**
     * @tpTestDetails CTest double ID as String in resource
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testDoubleId() throws Exception {
        Response response = client.target(PortProviderUtil.generateURL("/1/2", "one"))
                .request().get();
        Assertions.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        response.close();
    }

    /**
     * @tpTestDetails CTest double ID as PathSegment in resource
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testDoubleIdAsPathSegment() throws Exception {
        Response response = client.target(PortProviderUtil.generateURL("/1/2", "two"))
                .request().get();
        Assertions.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        response.close();
    }
}
