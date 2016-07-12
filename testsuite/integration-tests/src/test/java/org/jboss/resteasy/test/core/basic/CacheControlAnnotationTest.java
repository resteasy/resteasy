package org.jboss.resteasy.test.core.basic;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.core.basic.resource.CacheControlAnnotationResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Configuration
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for org.jboss.resteasy.annotations.cache.Cache class
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class CacheControlAnnotationTest {

    private static ResteasyClient client;

    @Deployment
    public static Archive<?> deploySimpleResource() {
        WebArchive war = TestUtil.prepareArchive(CacheControlAnnotationTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, CacheControlAnnotationResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, CacheControlAnnotationTest.class.getSimpleName());
    }

    @Before
    public void setup() {
        client = new ResteasyClientBuilder().build();
    }

    @After
    public void after() throws Exception {
        client.close();
    }

    /**
     * @tpTestDetails Test for correct value of max-age of cache annotation
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testResourceValid() {
        WebTarget base = client.target(generateURL("/maxage"));
        Response response = base.request().get();

        Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        CacheControl cc = CacheControl.valueOf(response.getHeaderString("cache-control"));
        Assert.assertFalse("Cache should not be private", cc.isPrivate());
        Assert.assertEquals("Wrong age of cache", 3600, cc.getMaxAge());

        response.close();
    }

    /**
     * @tpTestDetails Test for no-cache settings
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testResourceNoCach() {
        WebTarget base = client.target(generateURL("/nocache"));
        Response response = base.request().get();

        Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        String value = response.getHeaderString("cache-control");
        Assert.assertEquals("Wrong value of cache header", "no-cache", value);
        CacheControl cc = CacheControl.valueOf(value);
        Assert.assertTrue("Wrong value of cache header", cc.isNoCache());

        response.close();
    }

}
