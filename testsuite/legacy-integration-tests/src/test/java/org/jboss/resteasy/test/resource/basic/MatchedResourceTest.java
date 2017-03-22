package org.jboss.resteasy.test.resource.basic;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.resource.basic.resource.MatchedResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static org.hamcrest.CoreMatchers.is;


/**
 * @tpSubChapter Resources
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression tests for RESTEASY-549 and RESTEASY-537
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class MatchedResourceTest {

    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() throws Exception {
        WebArchive war = TestUtil.prepareArchive(MatchedResourceTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, MatchedResource.class);
    }

    @BeforeClass
    public static void init() {
        client = new ResteasyClientBuilder().build();
    }

    @AfterClass
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
        Assert.assertThat(response.getStatus(), is(HttpResponseCodes.SC_OK));
        String rtn = response.readEntity(String.class);
        Assert.assertEquals("started", rtn);
        response.close();

        base = client.target(generateURL("/start"));
        response = base.request().post(Entity.entity("<xml/>", "application/xml"));
        Assert.assertThat(response.getStatus(), is(HttpResponseCodes.SC_OK));
        rtn = response.readEntity(String.class);
        Assert.assertEquals("Wrong response content", rtn, "<xml/>");
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
        Assert.assertEquals("text/html;charset=UTF-8", response.getHeaders().getFirst("Content-Type"));
        String res = response.readEntity(String.class);
        Assert.assertEquals("Wrong response content", "*/*", res);
        response.close();
    }

    public void generalPostTest(String uri, String value) {
        WebTarget base = client.target(uri);
        Response response = base.request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("Wrong response content", response.readEntity(String.class), value);
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
