package org.jboss.resteasy.test.resource.basic;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.resource.basic.resource.ExtensionResource;
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
 * @tpSubChapter Resource
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for resteasy.media.type.mappings and resteasy.language.mappings parameters
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
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

    @BeforeClass
    public static void init() {
        client = ClientBuilder.newClient();
    }

    @AfterClass
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
        Response response = client.target(PortProviderUtil.generateURL("/extension.junk", ExtensionTest.class.getSimpleName())).request().get();
        Assert.assertEquals(HttpResponseCodes.SC_NOT_FOUND, response.getStatus());
        response.close();
    }

    private void basicTest(String path, String body) {
        Response response = client.target(PortProviderUtil.generateURL(path, ExtensionTest.class.getSimpleName())).request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("Wrong content of response", body, response.readEntity(String.class));
        response.close();
    }
}
