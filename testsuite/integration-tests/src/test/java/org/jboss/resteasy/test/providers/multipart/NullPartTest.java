package org.jboss.resteasy.test.providers.multipart;

import jakarta.ws.rs.client.ClientBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.test.providers.multipart.resource.MyServiceProxy;
import org.jboss.resteasy.test.providers.multipart.resource.NullPartBean;
import org.jboss.resteasy.test.providers.multipart.resource.NullPartService;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Multipart provider
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test proxy with null part of message with multipart provider
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class NullPartTest {
    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(NullPartTest.class.getSimpleName());
        war.addClasses(NullPartBean.class, MyServiceProxy.class);
        return TestUtil.finishContainerPrepare(war, null, NullPartService.class);
    }

    private static ResteasyClient client;

    private static String generateBaseUrl() {
        return PortProviderUtil.generateBaseUrl(NullPartTest.class.getSimpleName());
    }

    @BeforeClass
    public static void before() throws Exception {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterClass
    public static void after() throws Exception {
        client.close();
    }

    /**
     * @tpTestDetails Test new client
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testNewClient() throws Exception {
        MyServiceProxy proxy = client.target(generateBaseUrl()).proxy(MyServiceProxy.class);

        NullPartBean bean = proxy.createMyBean(); // should just be ok
        Assert.assertNotNull(bean);
        Assert.assertNull(bean.getSomeBinary());
    }

}
