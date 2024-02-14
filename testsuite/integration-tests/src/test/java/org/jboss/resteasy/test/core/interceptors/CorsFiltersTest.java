package org.jboss.resteasy.test.core.interceptors;

import static org.hamcrest.core.Is.is;

import java.lang.reflect.ReflectPermission;
import java.net.SocketPermission;
import java.util.ArrayList;
import java.util.List;
import java.util.PropertyPermission;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.hamcrest.MatcherAssert;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.plugins.interceptors.CorsFilter;
import org.jboss.resteasy.spi.CorsHeaders;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.core.interceptors.resource.CorsFiltersResource;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestApplication;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Interceptors
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test CorsFilter usage
 * @tpSince RESTEasy 3.0.16
 */
@Disabled("RESTEASY-3450")
@ExtendWith(ArquillianExtension.class)
public class CorsFiltersTest {

    @Deployment
    public static Archive<?> deploySimpleResource() {
        WebArchive war = TestUtil.prepareArchive(CorsFiltersTest.class.getSimpleName());
        war.addClass(PortProviderUtil.class);
        war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(new ReflectPermission("suppressAccessChecks"),
                new PropertyPermission("arquillian.*", "read"),
                new PropertyPermission("node", "read"),
                new PropertyPermission("ipv6", "read"),
                new PropertyPermission("org.jboss.resteasy.port", "read"),
                new PropertyPermission("quarkus.tester", "read"),
                new RuntimePermission("accessDeclaredMembers"),
                new RuntimePermission("getenv.RESTEASY_PORT"),
                new SocketPermission(PortProviderUtil.getHost(), "connect,resolve")), "permissions.xml");
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

        MatcherAssert.assertThat("Wrong count of singletons were created", TestApplication.singletons.size(), is(1));
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
        Assertions.assertEquals("Wrong response", "hello", response.readEntity(String.class));
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

        MatcherAssert.assertThat("Wrong count of singletons were created", TestApplication.singletons.size(), is(1));
        CorsFilter corsFilter = (CorsFilter) TestApplication.singletons.iterator().next();
        corsFilter.getAllowedOrigins().add(testedURL);

        Response response = target.request().header(CorsHeaders.ORIGIN, testedURL).get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("Response doesn't contain the Vary: Origin header", CorsHeaders.ORIGIN,
                response.getHeaderString(CorsHeaders.VARY));
        response.close();

        response = target.request().header(CorsHeaders.ORIGIN, testedURL).options();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("Response doesn't contain the Vary: Origin header", CorsHeaders.ORIGIN,
                response.getHeaderString(CorsHeaders.VARY));
        response.close();

        client.close();
    }

}
