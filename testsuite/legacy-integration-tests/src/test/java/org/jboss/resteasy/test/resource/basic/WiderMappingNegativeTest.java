package org.jboss.resteasy.test.resource.basic;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.resource.basic.resource.WiderMappingDefaultOptions;
import org.jboss.resteasy.test.resource.basic.resource.WiderMappingResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

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
