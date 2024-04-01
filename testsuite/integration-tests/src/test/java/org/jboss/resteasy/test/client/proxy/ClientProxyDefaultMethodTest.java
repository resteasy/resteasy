package org.jboss.resteasy.test.client.proxy;

import jakarta.ws.rs.client.ClientBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.test.client.proxy.resource.ClientSmokeResource;
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
 * @tpTestCaseDetails Client proxy supports interface default methods
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ClientProxyDefaultMethodTest {

    @Deployment
    public static Archive<?> deploySimpleResource() {
        WebArchive war = TestUtil.prepareArchive(ClientProxyDefaultMethodTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, ClientSmokeResource.class);
    }

    /**
     * @tpTestDetails Check proxy supports interface default method
     */
    @Test
    public void testNoDefaultsResource() throws Exception {
        ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
        ResourceWithInterfaceSimpleClient proxy = client.target(
                PortProviderUtil.generateBaseUrl(ClientProxyDefaultMethodTest.class.getSimpleName()))
                .proxyBuilder(ResourceWithInterfaceSimpleClient.class).build();

        Assertions.assertEquals("basic", proxy.getBasicThroughDefaultMethod(), "Wrong client answer.");
        client.close();
    }

}
