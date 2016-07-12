package org.jboss.resteasy.test.client.proxy;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.core.marshallers.ResteasyClientProxy;
import org.jboss.resteasy.client.jaxrs.ProxyBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.test.client.proxy.resource.ProxyCastingResource;
import org.jboss.resteasy.test.client.proxy.resource.ProxyCastingInterfaceA;
import org.jboss.resteasy.test.client.proxy.resource.ProxyCastingInterfaceB;
import org.jboss.resteasy.test.client.proxy.resource.ProxyCastingNothing;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import static org.junit.Assert.assertEquals;

/**
 * @tpSubChapter Configuration
 * @tpChapter Integration tests
 * @tpTestCaseDetails Any interface could be cast to ResteasyClientProxy.
 *                JBEAP-3197, JBEAP-4700
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
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

    @BeforeClass
    public static void before() throws Exception {
        client = ClientBuilder.newClient();
        target = (ResteasyWebTarget) client.target(generateURL("/foobar"));
    }

    @AfterClass
    public static void after() throws Exception {
        client.close();
    }

    /**
     * @tpTestDetails Cast one proxy to other proxy. Old client.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testOldClient() throws Exception {
        ProxyCastingInterfaceA a = ProxyFactory.create(ProxyCastingInterfaceA.class, generateURL("/foobar"));
        assertEquals("FOO", a.getFoo());
        ProxyCastingInterfaceB b = ((ResteasyClientProxy) a).as(ProxyCastingInterfaceB.class);
        assertEquals("BAR", b.getBar());
    }

    /**
     * @tpTestDetails Cast one proxy to other proxy. New client.
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    public void testNewClient() throws Exception {
        ProxyCastingInterfaceA a = ProxyBuilder.builder(ProxyCastingInterfaceA.class, target).build();
        assertEquals("FOO", a.getFoo());
        ProxyCastingInterfaceB b = ((org.jboss.resteasy.client.jaxrs.internal.proxy.ResteasyClientProxy) a).as(ProxyCastingInterfaceB.class);
        assertEquals("BAR", b.getBar());
    }
}
