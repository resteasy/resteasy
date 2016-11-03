package org.jboss.resteasy.test.resource.param;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.resource.param.resource.SpecialCharsInUrlResource;
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
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;


/**
 * @tpSubChapter Parameters
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for special characters in url
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class SpecialCharsInUrlTest {

    static Client client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(SpecialCharsInUrlTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, SpecialCharsInUrlResource.class);
    }

    @BeforeClass
    public static void setup() {
        client = ClientBuilder.newClient();
    }

    @AfterClass
    public static void cleanup() {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, SpecialCharsInUrlTest.class.getSimpleName());
    }

    private static final String encodedPart = "foo+bar%20gee@foo.com";
    private static final String decodedPart = "foo+bar gee@foo.com";

    /**
     * @tpTestDetails Test for '+' and '@' characters in url, RESTEASY-137
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGet() throws Exception {
        WebTarget target = client.target(String.format("%s%s?foo=%s", generateURL("/simple/"), encodedPart, encodedPart));
        Response response = target.request().get();
        Assert.assertEquals("The result is not correctly decoded", HttpResponseCodes.SC_OK, response.getStatus());
        String result = response.readEntity(String.class);
        Assert.assertEquals("The result is not correctly decoded", decodedPart, result);
    }

}
