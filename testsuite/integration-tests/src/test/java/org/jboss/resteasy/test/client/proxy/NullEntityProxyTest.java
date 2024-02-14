package org.jboss.resteasy.test.client.proxy;

import jakarta.ws.rs.client.ClientBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.test.client.proxy.resource.NullEntityProxy;
import org.jboss.resteasy.test.client.proxy.resource.NullEntityProxyGreeter;
import org.jboss.resteasy.test.client.proxy.resource.NullEntityProxyGreeting;
import org.jboss.resteasy.test.client.proxy.resource.NullEntityProxyResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-1684
 * @tpSince RESTEasy 3.0.24
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class NullEntityProxyTest {

    private static ResteasyClient client;

    @BeforeAll
    public static void before() throws Exception {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterAll
    public static void after() throws Exception {
        client.close();
    }

    @Deployment
    public static Archive<?> deployUriInfoSimpleResource() {
        WebArchive war = TestUtil.prepareArchive(NullEntityProxyTest.class.getSimpleName());
        war.addClasses(NullEntityProxy.class, NullEntityProxyGreeting.class, NullEntityProxyGreeter.class);
        return TestUtil.finishContainerPrepare(war, null, NullEntityProxyResource.class);
    }

    private static String generateBaseUrl() {
        return PortProviderUtil.generateBaseUrl(NullEntityProxyTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Test to send null Entity with proxy
     * @tpSince RESTEasy 3.0.24
     */
    @Test
    public void testNullEntityWithProxy() {
        ResteasyWebTarget target = client.target(generateBaseUrl());
        NullEntityProxy proxy = target.proxy(NullEntityProxy.class);
        NullEntityProxyGreeting greeting = proxy.helloEntity(null);
        Assertions.assertEquals(null, greeting.getGreeter(), "Response has wrong content");
    }
}
