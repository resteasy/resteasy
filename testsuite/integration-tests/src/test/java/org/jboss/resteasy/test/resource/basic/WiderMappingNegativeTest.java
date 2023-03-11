package org.jboss.resteasy.test.resource.basic;

import java.util.HashMap;
import java.util.Map;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.resource.basic.resource.WiderMappingDefaultOptions;
import org.jboss.resteasy.test.resource.basic.resource.WiderMappingResource;
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
 * @tpSubChapter Resources
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test negative scenario for "resteasy.wider.request.matching" property
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class WiderMappingNegativeTest {

    static Client client;

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, WiderMappingNegativeTest.class.getSimpleName());
    }

    @Deployment
    public static Archive<?> deployUriInfoSimpleResource() {
        WebArchive war = TestUtil.prepareArchive(WiderMappingNegativeTest.class.getSimpleName());
        war.addClass(PortProviderUtil.class);

        Map<String, String> contextParam = new HashMap<>();
        contextParam.put("resteasy.wider.request.matching", "false");
        return TestUtil.finishContainerPrepare(war, contextParam, WiderMappingResource.class, WiderMappingDefaultOptions.class);
    }

    @BeforeClass
    public static void setup() {
        client = ClientBuilder.newClient();
    }

    @AfterClass
    public static void cleanup() {
        client.close();
    }

    /**
     * @tpTestDetails Two resources used, more general resource should not be used
     *                Test confirms that the "HTTP OPTIONS requests" declared in WiderMappingDefaultOptions
     *                is not called because config switch resteasy.wider.request.matching is set to "false".
     *                (see Jakarta RESTful Web Services 3.1 specification. Section 3.3.5, HEAD and OPTIONS)
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testOptions() {
        Response response = client.target(generateURL("/hello/int")).request().options();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertNotEquals(response.readEntity(String.class), "hello");
        response.close();
    }

}
