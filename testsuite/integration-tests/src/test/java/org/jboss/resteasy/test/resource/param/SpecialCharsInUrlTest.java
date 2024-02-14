package org.jboss.resteasy.test.resource.param;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.resource.param.resource.SpecialCharsInUrlResource;
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
 * @tpSubChapter Parameters
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for special characters in url
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class SpecialCharsInUrlTest {

    static Client client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(SpecialCharsInUrlTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, SpecialCharsInUrlResource.class);
    }

    @BeforeAll
    public static void setup() {
        client = ClientBuilder.newClient();
    }

    @AfterAll
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
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus(), "The result is not correctly decoded");
        String result = response.readEntity(String.class);
        Assertions.assertEquals(decodedPart, result, "The result is not correctly decoded");
    }

}
