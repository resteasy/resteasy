package org.jboss.resteasy.test.resource.basic;

import static org.hamcrest.CoreMatchers.is;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.hamcrest.MatcherAssert;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.resource.basic.resource.MatchedResource;
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
 * @tpSubChapter Resources
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression tests for RESTEASY-549 and RESTEASY-537
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class MatchedResourceTest {

    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() throws Exception {
        WebArchive war = TestUtil.prepareArchive(MatchedResourceTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, MatchedResource.class);
    }

    @BeforeAll
    public static void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterAll
    public static void close() {
        client.close();
    }

    private static String generateURL(String path) {
        return PortProviderUtil.generateURL(path, MatchedResourceTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Regression test for RESTEASY-549
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testEmpty() throws Exception {
        WebTarget base = client.target(generateURL("/start"));
        Response response = base.request().post(Entity.text(""));
        MatcherAssert.assertThat(response.getStatus(), is(HttpResponseCodes.SC_OK));
        String rtn = response.readEntity(String.class);
        Assertions.assertEquals("started", rtn);
        response.close();

        base = client.target(generateURL("/start"));
        response = base.request().post(Entity.entity("<xml/>", "application/xml"));
        MatcherAssert.assertThat(response.getStatus(), is(HttpResponseCodes.SC_OK));
        rtn = response.readEntity(String.class);
        Assertions.assertEquals(rtn, "<xml/>", "Wrong response content");
        response.close();
    }

    /**
     * @tpTestDetails Regression test for RESTEASY-537
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMatch() throws Exception {
        WebTarget base = client.target(generateURL("/match"));
        Response response = base.request().header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .get();
        Assertions.assertEquals("text/html;charset=UTF-8", response.getHeaders().getFirst("Content-Type"));
        String res = response.readEntity(String.class);
        Assertions.assertEquals("*/*", res, "Wrong response content");
        response.close();
    }

    public void generalPostTest(String uri, String value) {
        WebTarget base = client.target(uri);
        Response response = base.request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals(response.readEntity(String.class), value, "Wrong response content");
    }

    /**
     * @tpTestDetails Check post request on resource with @GET annotation
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testPost() {
        generalPostTest(generateURL("/test1/foo.xml.en"), "complex");
        generalPostTest(generateURL("/test2/foo.xml.en"), "complex2");
    }

}
