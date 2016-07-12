package org.jboss.resteasy.test.core.interceptors;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.plugins.interceptors.CorsFilter;
import org.jboss.resteasy.spi.CorsHeaders;
import org.jboss.resteasy.test.core.interceptors.resource.CorsFiltersResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestApplication;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;

/**
 * @tpSubChapter Interceptors
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test CorsFilter usage
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
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

    /**
     * @tpTestDetails Check different options of Cors headers.
     * CorsFilter is created as singleton in TestApplication instance.
     * In this test is CorsFilter get from static set from TestApplication class.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testPreflight() throws Exception {
        String testedURL = "http://" + PortProviderUtil.getHost();

        ResteasyClient client = new ResteasyClientBuilder().build();
        WebTarget target = client.target(generateURL("/test"));
        Response response = target.request().header(CorsHeaders.ORIGIN, testedURL)
                .options();
        Assert.assertEquals(HttpResponseCodes.SC_FORBIDDEN, response.getStatus());
        response.close();
        response = target.request().header(CorsHeaders.ORIGIN, testedURL)
                .get();
        Assert.assertEquals(HttpResponseCodes.SC_FORBIDDEN, response.getStatus());
        response.close();

        Assert.assertThat("Wrong count of singletons were created", TestApplication.singletons.size(), is(1));
        CorsFilter corsFilter = (CorsFilter) TestApplication.singletons.iterator().next();

        corsFilter.getAllowedOrigins().add(testedURL);
        response = target.request().header(CorsHeaders.ORIGIN, testedURL)
                .options();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        response.close();
        response = target.request().header(CorsHeaders.ORIGIN, testedURL)
                .get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals(response.getHeaderString(CorsHeaders.ACCESS_CONTROL_ALLOW_ORIGIN), testedURL);
        Assert.assertEquals("Wrong response", "hello", response.readEntity(String.class));
        response.close();

        client.close();
    }

}
