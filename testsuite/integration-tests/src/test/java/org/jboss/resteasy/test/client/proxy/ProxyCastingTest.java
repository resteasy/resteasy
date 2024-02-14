package org.jboss.resteasy.test.client.proxy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ProxyBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.test.client.proxy.resource.ProxyCastingInterfaceA;
import org.jboss.resteasy.test.client.proxy.resource.ProxyCastingInterfaceB;
import org.jboss.resteasy.test.client.proxy.resource.ProxyCastingNothing;
import org.jboss.resteasy.test.client.proxy.resource.ProxyCastingResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Configuration
 * @tpChapter Integration tests
 * @tpTestCaseDetails Any interface could be cast to ResteasyClientProxy.
 *                    JBEAP-3197, JBEAP-4700
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ProxyCastingTest {
    private static Client client;
    private static ResteasyWebTarget target;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ProxyCastingTest.class.getSimpleName());
        war.addClasses(ProxyCastingNothing.class,
                ProxyCastingInterfaceA.class, ProxyCastingInterfaceB.class);
        return TestUtil.finishContainerPrepare(war, null, ProxyCastingResource.class);
    }

    private static String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ProxyCastingTest.class.getSimpleName());
    }

    @BeforeAll
    public static void before() throws Exception {
        client = ClientBuilder.newClient();
        target = (ResteasyWebTarget) client.target(generateURL("/foobar"));
    }

    @AfterAll
    public static void after() throws Exception {
        client.close();
    }

    /**
     * @tpTestDetails Cast one proxy to other proxy. New client.
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    public void testNewClient() throws Exception {
        ProxyCastingInterfaceA a = ProxyBuilder.builder(ProxyCastingInterfaceA.class, target).build();
        assertEquals("FOO", a.getFoo());
        ProxyCastingInterfaceB b = ((org.jboss.resteasy.client.jaxrs.internal.proxy.ResteasyClientProxy) a)
                .as(ProxyCastingInterfaceB.class);
        assertEquals("BAR", b.getBar());
    }
}
