package org.jboss.resteasy.test.core.interceptors;

import java.util.ArrayList;
import java.util.List;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.plugins.interceptors.CorsFilter;
import org.jboss.resteasy.spi.CorsHeaders;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.core.interceptors.resource.CorsFiltersResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestApplication;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Interceptors
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test CorsFilter usage
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
public class CorsFiltersTest {

    @Deployment
    public static Archive<?> deploySimpleResource() {
        WebArchive war = TestUtil.prepareArchive(CorsFiltersTest.class.getSimpleName());
        war.addClass(PortProviderUtil.class);
        List<Class<?>> singletons = new ArrayList<>();
        singletons.add(CorsFilter.class);
        return TestUtil.finishContainerPrepare(war, null, singletons, CorsFiltersResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, CorsFiltersTest.class.getSimpleName());
    }

    @AfterEach
    public void resetFilter() {
        CorsFilter corsFilter = (CorsFilter) TestApplication.singletons.iterator().next();
        corsFilter.getAllowedOrigins().remove("http://" + PortProviderUtil.getHost());
    }

    /**
     * @tpTestDetails Check different options of Cors headers.
     *                CorsFilter is created as singleton in TestApplication instance.
     *                In this test is CorsFilter get from static set from TestApplication class.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testPreflight() throws Exception {
        String testedURL = "http://" + PortProviderUtil.getHost();

        ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
        WebTarget target = client.target(generateURL("/test"));
        Response response = target.request().header(CorsHeaders.ORIGIN, testedURL)
                .options();
        Assertions.assertEquals(HttpResponseCodes.SC_FORBIDDEN, response.getStatus());
        response.close();
        response = target.request().header(CorsHeaders.ORIGIN, testedURL)
                .get();
        Assertions.assertEquals(HttpResponseCodes.SC_FORBIDDEN, response.getStatus());
        response.close();

        Assertions.assertEquals(1, TestApplication.singletons.size(), "Wrong count of singletons were created");
        CorsFilter corsFilter = (CorsFilter) TestApplication.singletons.iterator().next();

        corsFilter.getAllowedOrigins().add(testedURL);
        response = target.request().header(CorsHeaders.ORIGIN, testedURL)
                .options();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        response.close();
        response = target.request().header(CorsHeaders.ORIGIN, testedURL)
                .get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals(response.getHeaderString(CorsHeaders.ACCESS_CONTROL_ALLOW_ORIGIN), testedURL);
        Assertions.assertEquals("hello", response.readEntity(String.class), "Wrong response");
        response.close();

        client.close();
    }

    /**
     * @tpTestDetails Test that the response contains the Vary: Origin header
     * @tpInfo RESTEASY-1704
     * @tpSince RESTEasy 3.0.25
     */
    @Test
    public void testVaryOriginHeader() {
        String testedURL = "http://" + PortProviderUtil.getHost();
        ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
        WebTarget target = client.target(generateURL("/test"));

        Assertions.assertEquals(1, TestApplication.singletons.size(), "Wrong count of singletons were created");
        CorsFilter corsFilter = (CorsFilter) TestApplication.singletons.iterator().next();
        corsFilter.getAllowedOrigins().add(testedURL);

        Response response = target.request().header(CorsHeaders.ORIGIN, testedURL).get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals(CorsHeaders.ORIGIN, response.getHeaderString(CorsHeaders.VARY),
                "Response doesn't contain the Vary: Origin header");
        response.close();

        response = target.request().header(CorsHeaders.ORIGIN, testedURL).options();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals(CorsHeaders.ORIGIN, response.getHeaderString(CorsHeaders.VARY),
                "Response doesn't contain the Vary: Origin header");
        response.close();

        client.close();
    }

}
