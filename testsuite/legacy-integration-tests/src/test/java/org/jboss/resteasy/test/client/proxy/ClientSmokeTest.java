package org.jboss.resteasy.test.client.proxy;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.client.proxy.resource.ClientSmokeResource;
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
 * @tpTestCaseDetails Smoke test for client ProxyFactory.
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ClientSmokeTest {

    @Deployment
    public static Archive<?> deploySimpleResource() {
        WebArchive war = TestUtil.prepareArchive(ClientSmokeTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, ClientSmokeResource.class);
    }

    /**
     * @tpTestDetails Check results from ResourceWithInterfaceSimpleClient.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testNoDefaultsResource() throws Exception {
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResourceWithInterfaceSimpleClient proxy = client.target(
                PortProviderUtil.generateBaseUrl(ClientSmokeTest.class.getSimpleName()))
                .proxyBuilder(ResourceWithInterfaceSimpleClient.class).build();

        Assert.assertEquals("Wrong client answer.", "basic", proxy.getBasic());
        proxy.putBasic("hello world");
        Assert.assertEquals("Wrong client answer.", "hello world", proxy.getQueryParam("hello world"));
        Assert.assertEquals("Wrong client answer.", 1234, proxy.getUriParam(1234));

        client.close();
    }

}
