package org.jboss.resteasy.test.core.basic;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.CacheControl;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.RuntimeDelegate;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.test.core.basic.resource.CacheAnnotationInheritance;
import org.jboss.resteasy.test.core.basic.resource.CacheControlAnnotationResource;
import org.jboss.resteasy.test.core.basic.resource.CacheControlAnnotationResourceInheritance;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

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
        war.addClasses(CacheControlAnnotationResourceInheritance.class);

        return TestUtil.finishContainerPrepare(war, null, CacheControlAnnotationResource.class,
                CacheAnnotationInheritance.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, CacheControlAnnotationTest.class.getSimpleName());
    }

    @Before
    public void setup() {
        client = (ResteasyClient) ClientBuilder.newClient();
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

        try (Response response = base.request().get()) {
            Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
            CacheControl cc = RuntimeDelegate.getInstance().createHeaderDelegate(CacheControl.class)
                    .fromString(response.getHeaderString("cache-control"));
            Assert.assertFalse("Cache should not be private", cc.isPrivate());
            Assert.assertEquals("Wrong age of cache", 3600, cc.getMaxAge());
        }
    }

    /**
     * @tpTestDetails Test for no-cache settings
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testResourceNoCach() {
        WebTarget base = client.target(generateURL("/nocache"));

        try (Response response = base.request().get()) {
            Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
            String value = response.getHeaderString("cache-control");
            Assert.assertEquals("Wrong value of cache header", "no-cache", value);
            CacheControl cc = RuntimeDelegate.getInstance().createHeaderDelegate(CacheControl.class).fromString(value);
            Assert.assertTrue("Wrong value of cache header", cc.isNoCache());
        }
    }

    /**
     * @tpTestDetails Test for no-cache settings mixed with other directives
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testResourceCompositeNoCache() {
        WebTarget base = client.target(generateURL("/composite"));

        try (Response response = base.request().get()) {
            Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
            CacheControl cc = RuntimeDelegate.getInstance().createHeaderDelegate(CacheControl.class)
                    .fromString(response.getHeaderString("cache-control"));
            Assert.assertTrue("There must be no-store", cc.isNoStore());
            Assert.assertTrue("There must be must-revalidate", cc.isMustRevalidate());
            Assert.assertTrue("Cache must be private", cc.isPrivate());
            Assert.assertEquals("Wrong age of cache", 0, cc.getMaxAge());
            Assert.assertEquals("Wrong age of shared cache", 0, cc.getSMaxAge());
            Assert.assertTrue("There must be no-cache", cc.isNoCache());
        }
    }

    /**
     * @tpTestDetails Test for correct value of max-age of inherited cache annotation
     * @tpSince RESTEasy 7.0.0
     */
    @Test
    public void testInheritedResourceValid() {
        WebTarget base = client.target(generateURL("/inheritance"));

        try (Response response = base.request().get()) {
            Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
            CacheControl cc = RuntimeDelegate.getInstance().createHeaderDelegate(CacheControl.class)
                    .fromString(response.getHeaderString("cache-control"));
            Assert.assertFalse("Cache should not be private", cc.isPrivate());
            Assert.assertEquals("Wrong age of cache", 3600, cc.getMaxAge());
        }
    }
}
