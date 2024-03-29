package org.jboss.resteasy.test.client.proxy;

import java.io.InputStream;

import jakarta.ws.rs.client.ClientBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.test.client.proxy.resource.ProxyInputStreamProxy;
import org.jboss.resteasy.test.client.proxy.resource.ProxyInputStreamResource;
import org.jboss.resteasy.util.ReadFromStream;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-351
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
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

    @BeforeEach
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
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
        Assertions.assertEquals("hello world", str);
    }
}
