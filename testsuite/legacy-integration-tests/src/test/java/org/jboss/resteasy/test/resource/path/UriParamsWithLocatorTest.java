package org.jboss.resteasy.test.resource.path;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.resource.path.resource.UriParamsWithLocatorLocator;
import org.jboss.resteasy.test.resource.path.resource.UriParamsWithLocatorLocator2;
import org.jboss.resteasy.test.resource.path.resource.UriParamsWithLocatorResource;
import org.jboss.resteasy.test.resource.path.resource.UriParamsWithLocatorResource2;
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
 * @tpSubChapter UriParamsWithLocatorResource
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 * @tpTestCaseDetails Test that a locator and resource with same path params work
 */
@RunWith(Arquillian.class)
@RunAsClient
public class UriParamsWithLocatorTest {
    static Client client;

    @BeforeClass
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

    @AfterClass
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
        Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
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
        Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        response.close();
    }
}