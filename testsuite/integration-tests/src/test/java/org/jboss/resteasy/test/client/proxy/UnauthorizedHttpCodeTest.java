package org.jboss.resteasy.test.client.proxy;

import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.client.ClientBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.client.proxy.resource.UnauthorizedHttpCodeProxy;
import org.jboss.resteasy.test.client.proxy.resource.UnauthorizedHttpCodeResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Client tests
 * @tpSince RESTEasy 3.0.16
 * @tpTestCaseDetails Regression test for RESTEASY-575
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class UnauthorizedHttpCodeTest {

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(UnauthorizedHttpCodeTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, UnauthorizedHttpCodeResource.class);
    }

    /**
     * @tpTestDetails Get 401 http code via proxy
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testProxy() throws Exception {
        ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
        UnauthorizedHttpCodeProxy proxy = client
                .target(PortProviderUtil.generateURL("/", UnauthorizedHttpCodeTest.class.getSimpleName()))
                .proxy(UnauthorizedHttpCodeProxy.class);
        try {
            proxy.getFoo();
        } catch (NotAuthorizedException e) {
            Assertions.assertEquals(e.getResponse().getStatus(), HttpResponseCodes.SC_UNAUTHORIZED);
            String val = e.getResponse().readEntity(String.class);
            Assertions.assertEquals("hello", val, "Wrong content of response");
        }
        client.close();
    }

}
