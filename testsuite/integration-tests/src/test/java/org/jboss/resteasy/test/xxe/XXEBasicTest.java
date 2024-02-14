package org.jboss.resteasy.test.xxe;

import java.util.HashMap;
import java.util.Map;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.xxe.resource.XXEBasicResource;
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
 * @tpSubChapter XXE
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-637
 *                    Basic XXE test.
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class XXEBasicTest {

    static String request;
    static {
        String filename = TestUtil.getResourcePath(XXEBasicTest.class, "testpasswd.txt");
        request = new StringBuilder()
                .append("<?xml version=\"1.0\"?>\r")
                .append("<!DOCTYPE foo\r")
                .append("[<!ENTITY xxe SYSTEM \"").append(filename).append("\">\r")
                .append("]>\r")
                .append("<search><user>&xxe;</user></search>").toString();
    }

    static ResteasyClient client;
    protected final Logger logger = Logger.getLogger(XXEBasicTest.class.getName());

    public static Archive<?> deploy(String expandEntityReferences) {
        WebArchive war = TestUtil.prepareArchive(expandEntityReferences);
        Map<String, String> contextParam = new HashMap<>();
        contextParam.put(ResteasyContextParameters.RESTEASY_EXPAND_ENTITY_REFERENCES, expandEntityReferences);
        contextParam.put(ResteasyContextParameters.RESTEASY_DISABLE_DTDS, "false");
        return TestUtil.finishContainerPrepare(war, contextParam, XXEBasicResource.class);
    }

    @Deployment(name = "true")
    public static Archive<?> deployDefault() {
        return deploy("true");
    }

    @Deployment(name = "false")
    public static Archive<?> deployOne() {
        return deploy("false");
    }

    @BeforeEach
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
        client = null;
    }

    /**
     * @tpTestDetails "resteasy.document.secure.disableDTDs" is set to false
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testXXEWithoutExpansion() throws Exception {
        logger.info(String.format("Request body: %s", request));

        Response response = client.target(PortProviderUtil.generateURL("/", "false")).request()
                .post(Entity.entity(request, "application/xml"));

        Assertions.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
        String entity = response.readEntity(String.class);
        Assertions.assertEquals(entity, null);
        response.close();
    }

    /**
     * @tpTestDetails "resteasy.document.secure.disableDTDs" is set to true
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testXXEWithExpansion() throws Exception {
        Response response = client.target(PortProviderUtil.generateURL("/", "true")).request()
                .post(Entity.entity(request, "application/xml"));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String entity = response.readEntity(String.class);
        Assertions.assertEquals("xx:xx:xx:xx:xx:xx:xx", entity);
        response.close();
    }
}
