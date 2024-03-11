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
import org.jboss.resteasy.test.resource.basic.resource.ExtensionResource;
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
 * @tpTestCaseDetails Test for resteasy.media.type.mappings and resteasy.language.mappings parameters
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ExtensionTest {

    static Client client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ExtensionTest.class.getSimpleName());

        Map<String, String> params = new HashMap<>();
        params.put("resteasy.media.type.mappings", "xml : application/xml, html : text/html, txt : text/plain");
        params.put("resteasy.language.mappings", "en : en-US");
        return TestUtil.finishContainerPrepare(war, params, ExtensionResource.class);
    }

    @BeforeAll
    public static void init() {
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void after() throws Exception {
        client.close();
    }

    /**
     * @tpTestDetails Check correct values
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testIt() {
        basicTest("/extension.xml", "xml");
        basicTest("/extension.html.en", "html");
        basicTest("/extension.en.html", "html");
        basicTest("/extension/stuff.old.en.txt", "plain");
        basicTest("/extension/stuff.en.old.txt", "plain");
        basicTest("/extension/stuff.en.txt.old", "plain");
    }

    /**
     * @tpTestDetails Check wrong value
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testError() {
        Response response = client.target(PortProviderUtil.generateURL("/extension.junk", ExtensionTest.class.getSimpleName()))
                .request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_NOT_FOUND, response.getStatus());
        response.close();
    }

    private void basicTest(String path, String body) {
        Response response = client.target(PortProviderUtil.generateURL(path, ExtensionTest.class.getSimpleName())).request()
                .get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals(body, response.readEntity(String.class),
                "Wrong content of response");
        response.close();
    }
}
