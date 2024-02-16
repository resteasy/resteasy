package org.jboss.resteasy.test.core.smoke;

import jakarta.ws.rs.client.ClientBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.test.core.smoke.resource.ResourceWithInterfaceResourceWithInterface;
import org.jboss.resteasy.test.core.smoke.resource.ResourceWithInterfaceSimpleClient;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Smoke tests for jaxrs
 * @tpChapter Integration tests
 * @tpTestCaseDetails Smoke test for resource with interface.
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
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
        ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
        ResourceWithInterfaceSimpleClient proxy = client
                .target(PortProviderUtil.generateBaseUrl(ResourceWithInterfaceTest.class.getSimpleName()))
                .proxyBuilder(ResourceWithInterfaceSimpleClient.class).build();

        Assertions.assertEquals("basic", proxy.getBasic(), "Wrong client answer.");
        proxy.putBasic("hello world");
        Assertions.assertEquals("hello world", proxy.getQueryParam("hello world"),
                "Wrong client answer.");
        Assertions.assertEquals(1234, proxy.getUriParam(1234), "Wrong client answer.");

        client.close();
    }
}
