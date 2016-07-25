package org.jboss.resteasy.test.core.smoke;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.core.smoke.resource.ResourceWithMultipleInterfacesEmpty;
import org.jboss.resteasy.test.core.smoke.resource.ResourceWithMultipleInterfacesIntA;
import org.jboss.resteasy.test.core.smoke.resource.ResourceWithMultipleInterfacesRootResource;
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
 * @tpSubChapter Smoke tests for jaxrs
 * @tpChapter Integration tests
 * @tpTestCaseDetails Smoke test for resource with multiple interfaces.
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ResourceWithMultipleInterfacesTest {

    static ResteasyClient client;

    @Deployment(name = "LocatingResource")
    public static Archive<?> deployLocatingResource() {
        WebArchive war = TestUtil.prepareArchive(ResourceWithMultipleInterfacesTest.class.getSimpleName());
        war.addClass(ResourceWithMultipleInterfacesIntA.class);
        war.addClass(ResourceWithMultipleInterfacesEmpty.class);
        return TestUtil.finishContainerPrepare(war, null, ResourceWithMultipleInterfacesRootResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ResourceWithMultipleInterfacesTest.class.getSimpleName());
    }

    @Before
    public void init() {
        client = new ResteasyClientBuilder().build();
    }

    @After
    public void after() throws Exception {
        client.close();
    }

    /**
     * @tpTestDetails Check result from resource with multiple interfaces.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testNoDefaultsResource() throws Exception {
        ResourceWithMultipleInterfacesIntA proxy = client.target(generateURL("/")).proxyBuilder(ResourceWithMultipleInterfacesIntA.class).build();
        Assert.assertEquals("Wrong client answer.", "FOO", proxy.getFoo());
    }
}
