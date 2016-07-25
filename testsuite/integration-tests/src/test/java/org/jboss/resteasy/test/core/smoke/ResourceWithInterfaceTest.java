package org.jboss.resteasy.test.core.smoke;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.core.smoke.resource.ResourceWithInterfaceResourceWithInterface;
import org.jboss.resteasy.test.core.smoke.resource.ResourceWithInterfaceSimpleClient;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Smoke tests for jaxrs
 * @tpChapter Integration tests
 * @tpTestCaseDetails Smoke test for resource with interface.
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ResourceWithInterfaceTest {
    @Deployment(name = "LocatingResource")
    public static Archive<?> deployLocatingResource() {
        WebArchive war = TestUtil.prepareArchive(ResourceWithInterfaceTest.class.getSimpleName());
        war.addClass(ResourceWithInterfaceSimpleClient.class);
        return TestUtil.finishContainerPrepare(war, null, ResourceWithInterfaceResourceWithInterface.class);
    }

    /**
     * @tpTestDetails Check result from resource with interface.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testNoDefaultsResource() throws Exception {
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResourceWithInterfaceSimpleClient proxy = client.target(PortProviderUtil.generateBaseUrl(ResourceWithInterfaceTest.class.getSimpleName())).proxyBuilder(ResourceWithInterfaceSimpleClient.class).build();

        Assert.assertEquals("Wrong client answer.", "basic", proxy.getBasic());
        proxy.putBasic("hello world");
        Assert.assertEquals("Wrong client answer.", "hello world", proxy.getQueryParam("hello world"));
        Assert.assertEquals("Wrong client answer.", 1234, proxy.getUriParam(1234));

        client.close();
    }
}
