package org.jboss.resteasy.test.client.proxy;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.test.client.proxy.resource.ProxyNullInputStreamClientResponseFilter;
import org.jboss.resteasy.test.client.proxy.resource.ProxyNullInputStreamResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for RESTEASY-1671
 * @tpSince RESTEasy 4.0.0
 *
 *          Created by rsearls on 8/24/17.
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ProxyNullInputStreamTest {

    @Deployment
    public static Archive<?> deploySimpleResource() {
        WebArchive war = TestUtil.prepareArchive(ProxyNullInputStreamTest.class.getSimpleName());
        war.addClasses(ProxyNullInputStreamResource.class,
                ProxyNullInputStreamClientResponseFilter.class);
        return TestUtil.finishContainerPrepare(war, null, (Class<?>[]) null);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ProxyNullInputStreamTest.class.getSimpleName());
    }

    @Test
    public void testNullPointerEx() throws Exception {
        Client client = ClientBuilder.newBuilder().register(ProxyNullInputStreamClientResponseFilter.class).build();
        ProxyNullInputStreamResource pResource = ((ResteasyWebTarget) client.target(generateURL("/test/user/mydb")))
                .proxyBuilder(ProxyNullInputStreamResource.class)
                .build();
        try {
            pResource.getUserHead("myDb");
        } catch (Exception e) {
            Assertions.assertEquals("HTTP 404 Not Found", e.getMessage());
        } finally {
            client.close();
        }

    }
}
