package org.jboss.resteasy.test.client.proxy;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ProxyBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.internal.proxy.ResteasyClientProxy;
import org.jboss.resteasy.test.client.proxy.resource.ProxyCastingInterfaceB;
import org.jboss.resteasy.test.client.proxy.resource.ProxyCastingSimpleFooBar;
import org.jboss.resteasy.test.client.proxy.resource.ProxyCastingSimpleFooBarImpl;
import org.jboss.resteasy.test.client.proxy.resource.ProxyCastingSimpleInterfaceA;
import org.jboss.resteasy.test.client.proxy.resource.ProxyCastingSimpleInterfaceAorB;
import org.jboss.resteasy.test.client.proxy.resource.ProxyCastingSimpleInterfaceB;
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
 * @tpSince RESTEasy 3.0.17
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ProxyCastingSimpleTest {
    private static Client client;
    private static ResteasyWebTarget target;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ProxyCastingSimpleTest.class.getSimpleName());
        war.addClasses(ProxyCastingSimpleFooBar.class,
                ProxyCastingSimpleFooBar.class, ProxyCastingInterfaceB.class,
                ProxyCastingSimpleInterfaceA.class, ProxyCastingSimpleInterfaceAorB.class,
                ProxyCastingSimpleInterfaceB.class);
        return TestUtil.finishContainerPrepare(war, null, ProxyCastingSimpleFooBarImpl.class);
    }

    private static String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ProxyCastingSimpleTest.class.getSimpleName());
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
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    public void testSubresourceProxy() throws Exception {
        ProxyCastingSimpleFooBar foobar = ProxyBuilder.builder(ProxyCastingSimpleFooBar.class, target).build();
        {
            ProxyCastingSimpleInterfaceA a = ((ResteasyClientProxy) foobar.getThing("a")).as(ProxyCastingSimpleInterfaceA.class);
            assertEquals("Wrong body of response", "FOO", a.getFoo());
            ProxyCastingSimpleInterfaceB b = ((ResteasyClientProxy) foobar.getThing("b")).as(ProxyCastingSimpleInterfaceB.class);
            assertEquals("Wrong body of response", "BAR", b.getBar());
        }
        {
            ProxyCastingSimpleInterfaceA a = foobar.getThing("a").as(ProxyCastingSimpleInterfaceA.class);
            assertEquals("Wrong body of response", "FOO", a.getFoo());
            ProxyCastingSimpleInterfaceB b = foobar.getThing("b").as(ProxyCastingSimpleInterfaceB.class);
            assertEquals("Wrong body of response", "BAR", b.getBar());
        }
    }
}
