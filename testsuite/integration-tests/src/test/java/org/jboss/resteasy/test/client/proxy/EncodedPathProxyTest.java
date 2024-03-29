package org.jboss.resteasy.test.client.proxy;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.test.client.proxy.resource.EncodedPathProxyInterface;
import org.jboss.resteasy.test.client.proxy.resource.EncodedPathProxyResource;
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
 * @tpTestCaseDetails Regression test for RESTEASY-1475.
 * @tpSince RESTEasy 3.1.4
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class EncodedPathProxyTest {
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
        WebArchive war = TestUtil.prepareArchive(EncodedPathProxyTest.class.getSimpleName());
        war.addClasses(EncodedPathProxyInterface.class);
        return TestUtil.finishContainerPrepare(war, null, EncodedPathProxyResource.class);
    }

    private static String generateBaseUrl() {
        return PortProviderUtil.generateBaseUrl(EncodedPathProxyTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Verify "/" in "t;hawkular/f;jk-feed" is sent encoded
     * @tpSince RESTEasy 3.1.4
     */
    @Test
    public void testEncodeProxy() throws Exception {
        ResteasyWebTarget target = client.target(generateBaseUrl());
        EncodedPathProxyInterface proxy = target.proxy(EncodedPathProxyInterface.class);
        Response response = proxy.encode("t;hawkular/f;jk-feed", null);
        Assertions.assertEquals(200, response.getStatus());
        String uri = response.readEntity(String.class);
        Assertions.assertEquals(generateBaseUrl() + "/test/encode/t;hawkular%2Ff;jk-feed", uri);
    }

    /**
     * @tpTestDetails Verify "/" in "t;hawkular/f;jk-feed" is sent unencoded
     * @tpSince RESTEasy 3.1.4
     */
    @Test
    public void testNoencodeProxy() throws Exception {
        ResteasyWebTarget target = client.target(generateBaseUrl());
        EncodedPathProxyInterface proxy = target.proxy(EncodedPathProxyInterface.class);
        Response response = proxy.noencode("t;hawkular/f;jk-feed", null);
        Assertions.assertEquals(200, response.getStatus());
        String uri = response.readEntity(String.class);
        Assertions.assertEquals(generateBaseUrl() + "/test/noencode/t;hawkular/f;jk-feed", uri);
    }
}
