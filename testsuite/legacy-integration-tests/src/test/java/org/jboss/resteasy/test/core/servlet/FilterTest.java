package org.jboss.resteasy.test.core.servlet;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.core.servlet.resource.FilterForwardServlet;
import org.jboss.resteasy.test.core.servlet.resource.FilterResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

/**
 * @tpSubChapter Configuration
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-1049
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class FilterTest {
    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(FilterTest.class.getSimpleName())
                .addClasses(FilterForwardServlet.class)
                .addAsWebInfResource(ServletConfigTest.class.getPackage(), "FilterWeb.xml", "web.xml");
        return TestUtil.finishContainerPrepare(war, null, FilterResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, FilterTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Test for dynamic dispatching in servlet.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testDispatchDynamic() throws Exception {
        ResteasyClient client = new ResteasyClientBuilder().build();
        Invocation.Builder request = client.target(generateURL("/test/dispatch/dynamic")).request();
        Response response = request.get();
        assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        assertEquals("Wrong content of response", "forward", response.readEntity(String.class));
    }
}
