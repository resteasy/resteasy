package org.jboss.resteasy.test.xxe;

import java.util.HashMap;
import java.util.Map;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.setup.DisableDefaultExceptionMapperSetupTask;
import org.jboss.resteasy.test.xxe.resource.XxeSecureProcessingFavoriteMovieXmlRootElement;
import org.jboss.resteasy.test.xxe.resource.XxeSecureProcessingMovieResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledForJreRange;
import org.junit.jupiter.api.condition.JRE;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter XXE
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-869
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
@ServerSetup(DisableDefaultExceptionMapperSetupTask.class)
@DisabledForJreRange(min = JRE.JAVA_24)
public class XxeSecureProcessingTest {

    private ResteasyClient client;
    public final Logger logger = Logger.getLogger(XxeSecureProcessingTest.class.getName());

    String doctype = "<!DOCTYPE foodocument [" +
            "<!ENTITY foo 'foo'>" +
            "<!ENTITY foo1 '&foo;&foo;&foo;&foo;&foo;&foo;&foo;&foo;&foo;&foo;'>" +
            "<!ENTITY foo2 '&foo1;&foo1;&foo1;&foo1;&foo1;&foo1;&foo1;&foo1;&foo1;&foo1;'>" +
            "<!ENTITY foo3 '&foo2;&foo2;&foo2;&foo2;&foo2;&foo2;&foo2;&foo2;&foo2;&foo2;'>" +
            "<!ENTITY foo4 '&foo3;&foo3;&foo3;&foo3;&foo3;&foo3;&foo3;&foo3;&foo3;&foo3;'>" +
            "<!ENTITY foo5 '&foo4;&foo4;&foo4;&foo4;&foo4;&foo4;&foo4;&foo4;&foo4;&foo4;'>" +
            "<!ENTITY foo6 '&foo5;&foo5;&foo5;&foo5;&foo5;&foo5;&foo5;&foo5;&foo5;&foo5;'>" +
            "<!ENTITY foo7 '&foo6;&foo6;&foo6;&foo6;&foo6;&foo6;&foo6;&foo6;&foo6;&foo6;'>" +
            "<!ENTITY foo8 '&foo7;&foo7;&foo7;&foo7;&foo7;&foo7;&foo7;&foo7;&foo7;&foo7;'>" +
            "<!ENTITY foo9 '&foo8;&foo8;&foo8;&foo8;&foo8;&foo8;&foo8;&foo8;&foo8;&foo8;'>" +
            "]>";

    String small = doctype
            + "<xxeSecureProcessingFavoriteMovieXmlRootElement><title>&foo4;</title></xxeSecureProcessingFavoriteMovieXmlRootElement>";
    String big = doctype
            + "<xxeSecureProcessingFavoriteMovieXmlRootElement><title>&foo5;</title></xxeSecureProcessingFavoriteMovieXmlRootElement>";

    private static final String T_DEFAULT = "default";
    private static final String T_TRUE = "true";
    private static final String T_FALSE = "false";

    public static Archive<?> deploy(String expandEntityReferences) {
        WebArchive war = TestUtil.prepareArchive(expandEntityReferences);
        Map<String, String> contextParam = new HashMap<>();
        contextParam.put("resteasy.document.secure.disableDTDs", "false");
        if (expandEntityReferences != null) {
            contextParam.put("resteasy.document.expand.entity.references", expandEntityReferences);
        }
        war.addClass(XxeSecureProcessingFavoriteMovieXmlRootElement.class);
        return TestUtil.finishContainerPrepare(war, contextParam, XxeSecureProcessingMovieResource.class);
    }

    @BeforeEach
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
    }

    @Deployment(name = T_DEFAULT)
    public static Archive<?> deployDefault() {
        return deploy(null);
    }

    @Deployment(name = T_TRUE)
    public static Archive<?> deployTrue() {
        return deploy("true");
    }

    @Deployment(name = T_FALSE)
    public static Archive<?> deployFalse() {
        return deploy("false");
    }

    /**
     * @tpTestDetails Small request in XML root element. "resteasy.document.expand.entity.references" property is not set.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testXmlRootElementDefaultSmall() throws Exception {
        Response response = client.target(PortProviderUtil.generateURL("/xmlRootElement", null)).request()
                .post(Entity.entity(small, "application/xml"));
        Assertions.assertEquals(200, response.getStatus());
        String entity = response.readEntity(String.class);
        logger.debug("Result: " + entity.substring(0, 30));
        Assertions.assertEquals(10000, countFoos(entity));
        response.close();
    }

    /**
     * @tpTestDetails Big request in XML root element. "resteasy.document.expand.entity.references" property is not set.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testXmlRootElementDefaultBig() throws Exception {
        Response response = client.target(PortProviderUtil.generateURL("/xmlRootElement", null)).request()
                .post(Entity.entity(big, "application/xml"));
        Assertions.assertEquals(400, response.getStatus());
        String entity = response.readEntity(String.class);
        logger.debug("Result: " + entity);
        Assertions.assertTrue(entity.contains("jakarta.xml.bind.UnmarshalException"));
        response.close();
    }

    /**
     * @tpTestDetails Small request in XML root element. "resteasy.document.expand.entity.references" property is set to false.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testXmlRootElementWithoutExternalExpansionSmall() throws Exception {
        Response response = client.target(PortProviderUtil.generateURL("/xmlRootElement", T_FALSE)).request()
                .post(Entity.entity(small, "application/xml"));
        Assertions.assertEquals(200, response.getStatus());
        String entity = response.readEntity(String.class);
        logger.debug("Result: " + entity.substring(0, 30));
        Assertions.assertEquals(10000, countFoos(entity));
        response.close();
    }

    /**
     * @tpTestDetails Big request in XML root element. "resteasy.document.expand.entity.references" property is set to false.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testXmlRootElementWithoutExternalExpansionBig() throws Exception {
        Response response = client.target(PortProviderUtil.generateURL("/xmlRootElement", T_FALSE)).request()
                .post(Entity.entity(big, "application/xml"));
        Assertions.assertEquals(400, response.getStatus());
        String entity = response.readEntity(String.class);
        logger.debug("Result: " + entity);
        Assertions.assertTrue(entity.contains("jakarta.xml.bind.UnmarshalException"));
        response.close();
    }

    /**
     * @tpTestDetails Small request in XML root element. "resteasy.document.expand.entity.references" property is set to true.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testXmlRootElementWithExternalExpansionSmall() throws Exception {
        Response response = client.target(PortProviderUtil.generateURL("/xmlRootElement", T_TRUE)).request()
                .post(Entity.entity(small, "application/xml"));
        Assertions.assertEquals(200, response.getStatus());
        String entity = response.readEntity(String.class);
        logger.debug("Result: " + entity.substring(0, 30));
        Assertions.assertEquals(10000, countFoos(entity));
        response.close();
    }

    /**
     * @tpTestDetails Big request in XML root element. "resteasy.document.expand.entity.references" property is set to true.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testXmlRootElementWithExternalExpansionBig() throws Exception {
        Response response = client.target(PortProviderUtil.generateURL("/xmlRootElement", T_TRUE)).request()
                .post(Entity.entity(big, "application/xml"));
        Assertions.assertEquals(400, response.getStatus());
        String entity = response.readEntity(String.class);
        logger.debug("Result: " + entity);
        Assertions.assertTrue(entity.contains("jakarta.xml.bind.UnmarshalException"));
        response.close();
    }

    private int countFoos(String s) {
        int count = 0;
        int pos = 0;

        while (pos >= 0) {
            pos = s.indexOf("foo", pos);
            if (pos >= 0) {
                count++;
                pos += 3;
            }
        }
        return count;
    }
}
