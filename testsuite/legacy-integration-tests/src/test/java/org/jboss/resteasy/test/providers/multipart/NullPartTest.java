package org.jboss.resteasy.test.providers.multipart;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.providers.multipart.resource.MyServiceProxy;
import org.jboss.resteasy.test.providers.multipart.resource.NullPartBean;
import org.jboss.resteasy.test.providers.multipart.resource.NullPartService;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
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

    private static String generateBaseUrl() {
        return PortProviderUtil.generateBaseUrl(NullPartTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Test new client
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testNewClient() throws Exception {
        MyServiceProxy proxy = new ResteasyClientBuilder().build().target(generateBaseUrl()).proxy(MyServiceProxy.class);

        NullPartBean bean = proxy.createMyBean(); // should just be ok
        Assert.assertNotNull(bean);
        Assert.assertNull(bean.getSomeBinary());
    }

    /**
     * @tpTestDetails Test old client
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testOldClient() throws Exception {
        MyServiceProxy proxy = ProxyFactory.create(MyServiceProxy.class, generateBaseUrl());

        NullPartBean bean = proxy.createMyBean(); // should just be ok
        Assert.assertNotNull(bean);
        Assert.assertNull(bean.getSomeBinary());
    }


}
