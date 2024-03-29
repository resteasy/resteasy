package org.jboss.resteasy.test.resource.basic;

import java.util.HashMap;
import java.util.Map;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.resource.basic.resource.ScanProxy;
import org.jboss.resteasy.test.resource.basic.resource.ScanResource;
import org.jboss.resteasy.test.resource.basic.resource.ScanSubresource;
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
 * @tpSubChapter Resource
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression tests for RESTEASY-263 and RESTEASY-274
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ScanTest {
    private static Client client;

    @BeforeAll
    public static void before() throws Exception {
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void after() throws Exception {
        client.close();
    }

    @Deployment
    public static Archive<?> deployUriInfoSimpleResource() {
        WebArchive war = TestUtil.prepareArchive(ScanTest.class.getSimpleName());
        war.addClass(ScanProxy.class);
        Map<String, String> contextParams = new HashMap<>();
        contextParams.put("resteasy.scan", "true");
        return TestUtil.finishContainerPrepare(war, contextParams, ScanResource.class, ScanSubresource.class);
    }

    /**
     * @tpTestDetails Test with new client
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testNewClient() throws Exception {
        Response response = client.target(PortProviderUtil.generateURL("/test/doit", ScanTest.class.getSimpleName())).request()
                .get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("hello world", response.readEntity(String.class), "Wrong content of response");
    }
}
