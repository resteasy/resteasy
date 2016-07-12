package org.jboss.resteasy.test.client.proxy;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.client.proxy.resource.ProxyInputStreamProxy;
import org.jboss.resteasy.test.client.proxy.resource.ProxyInputStreamResource;
import org.jboss.resteasy.util.ReadFromStream;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.InputStream;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-351
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ProxyInputStreamTest {

    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ProxyInputStreamTest.class.getSimpleName());
        war.addClass(ProxyInputStreamProxy.class);
        return TestUtil.finishContainerPrepare(war, null, ProxyInputStreamResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ProxyInputStreamTest.class.getSimpleName());
    }

    @Before
    public void init() {
        client = new ResteasyClientBuilder().build();
    }

    @After
    public void after() throws Exception {
        client.close();
    }

    /**
     * @tpTestDetails Old client version
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testInputStreamOldClient() throws Exception {
        ProxyInputStreamProxy proxy = ProxyFactory.create(ProxyInputStreamProxy.class, generateURL("/"));
        InputStream is = proxy.get();
        byte[] bytes = ReadFromStream.readFromStream(100, is);
        is.close();
        String str = new String(bytes);
        Assert.assertEquals("hello world", str);
    }

    /**
     * @tpTestDetails New client version
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testInputStreamNewClient() throws Exception {
        ProxyInputStreamProxy proxy = client.target(generateURL("/")).proxy(ProxyInputStreamProxy.class);
        InputStream is = proxy.get();
        byte[] bytes = ReadFromStream.readFromStream(100, is);
        is.close();
        String str = new String(bytes);
        Assert.assertEquals("hello world", str);
    }
}
