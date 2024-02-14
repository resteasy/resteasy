package org.jboss.resteasy.test.core.servlet;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.core.servlet.resource.FilterForwardServlet;
import org.jboss.resteasy.test.core.servlet.resource.FilterResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Configuration
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-1049
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
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
        ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
        Invocation.Builder request = client.target(generateURL("/test/dispatch/dynamic")).request();
        Response response = request.get();
        assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        assertEquals("forward", response.readEntity(String.class),
                "Wrong content of response");
        client.close();
    }
}
