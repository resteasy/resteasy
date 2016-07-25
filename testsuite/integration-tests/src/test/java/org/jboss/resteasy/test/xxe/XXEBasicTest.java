package org.jboss.resteasy.test.xxe;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.test.xxe.resource.XXEBasicResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * @tpSubChapter XXE
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-637
 *                    Basic XXE test.
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
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
    protected final Logger logger = LogManager.getLogger(XXEBasicTest.class.getName());

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

    @Before
    public void init() {
        client = new ResteasyClientBuilder().build();
    }

    @After
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

        Response response = client.target(PortProviderUtil.generateURL("/", "false")).request().post(Entity.entity(request, "application/xml"));

        Assert.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
        String entity = response.readEntity(String.class);
        Assert.assertEquals(entity, null);
        response.close();
    }

    /**
     * @tpTestDetails "resteasy.document.secure.disableDTDs" is set to true
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testXXEWithExpansion() throws Exception {
        Response response = client.target(PortProviderUtil.generateURL("/", "true")).request().post(Entity.entity(request, "application/xml"));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String entity = response.readEntity(String.class);
        Assert.assertEquals("xx:xx:xx:xx:xx:xx:xx", entity);
        response.close();
    }
}
