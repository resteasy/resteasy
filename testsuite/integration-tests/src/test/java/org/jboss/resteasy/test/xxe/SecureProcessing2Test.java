package org.jboss.resteasy.test.xxe;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.xxe.resource.SecureProcessingBar;
import org.jboss.resteasy.test.xxe.resource.SecureProcessingFavoriteMovie;
import org.jboss.resteasy.test.xxe.resource.SecureProcessingFavoriteMovieXmlRootElement;
import org.jboss.resteasy.test.xxe.resource.SecureProcessingFavoriteMovieXmlType;
import org.jboss.resteasy.test.xxe.resource.ObjectFactory;
import org.jboss.resteasy.test.xxe.resource.SecureProcessingResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.resteasy.category.NotForForwardCompatibility;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.core.StringContains.containsString;
import static org.jboss.resteasy.utils.PortProviderUtil.generateURL;

/**
 * @tpSubChapter XXE
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-1103
 *      RestEasy is vulnerable to XML Entity Denial of Service XXE is disabled.
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class SecureProcessing2Test {

    protected final Logger logger = LogManager.getLogger(SecureProcessing2Test.class.getName());
    static ResteasyClient client;

    private static final String URL_PREFIX = "RESTEASY-1103-";

    protected static String bigAttributeDoc;

    static {
        StringBuffer sb = new StringBuffer();
        sb.append("<secureProcessingBar ");
        for (int i = 0; i < 12000; i++) {
            sb.append("attr" + i + "=\"x\" ");
        }
        sb.append(">secureProcessingBar</secureProcessingBar>");
        bigAttributeDoc = sb.toString();
    }

    String bigElementDoctype =
            "<!DOCTYPE foodocument [" +
                    "<!ENTITY foo 'foo'>" +
                    "<!ENTITY foo1 '&foo;&foo;&foo;&foo;&foo;&foo;&foo;&foo;&foo;&foo;'>" +
                    "<!ENTITY foo2 '&foo1;&foo1;&foo1;&foo1;&foo1;&foo1;&foo1;&foo1;&foo1;&foo1;'>" +
                    "<!ENTITY foo3 '&foo2;&foo2;&foo2;&foo2;&foo2;&foo2;&foo2;&foo2;&foo2;&foo2;'>" +
                    "<!ENTITY foo4 '&foo3;&foo3;&foo3;&foo3;&foo3;&foo3;&foo3;&foo3;&foo3;&foo3;'>" +
                    "<!ENTITY foo5 '&foo4;&foo4;&foo4;&foo4;&foo4;&foo4;&foo4;&foo4;&foo4;&foo4;'>" +
                    "<!ENTITY foo6 '&foo5;&foo5;&foo5;&foo5;&foo5;&foo5;&foo5;&foo5;&foo5;&foo5;'>" +
                    "]>";

    String bigXmlRootElement = bigElementDoctype + "<secureProcessingFavoriteMovieXmlRootElement><title>&foo6;</title></secureProcessingFavoriteMovieXmlRootElement>";
    String bigXmlType = bigElementDoctype + "<favoriteMovie><title>&foo6;</title></favoriteMovie>";
    String bigJAXBElement = bigElementDoctype + "<favoriteMovieXmlType><title>&foo6;</title></favoriteMovieXmlType>";

    String bigCollection = bigElementDoctype +
            "<collection>" +
            "<secureProcessingFavoriteMovieXmlRootElement><title>&foo6;</title></secureProcessingFavoriteMovieXmlRootElement>" +
            "<secureProcessingFavoriteMovieXmlRootElement><title>&foo6;</title></secureProcessingFavoriteMovieXmlRootElement>" +
            "</collection>";

    String bigMap = bigElementDoctype +
            "<map>" +
            "<entry key=\"key1\">" +
            "<secureProcessingFavoriteMovieXmlRootElement><title>&foo6;</title></secureProcessingFavoriteMovieXmlRootElement>" +
            "</entry>" +
            "<entry key=\"key2\">" +
            "<secureProcessingFavoriteMovieXmlRootElement><title>&foo6;</title></secureProcessingFavoriteMovieXmlRootElement>" +
            "</entry>" +
            "</map>";

    String bar = "<!DOCTYPE secureProcessingBar SYSTEM \"src/test/resources/org/jboss/resteasy/test/xxe/SecureProcessing_external.dtd\"><secureProcessingBar><s>junk</s></secureProcessingBar>";
    String filename = TestUtil.getResourcePath(SecureProcessing2Test.class, "SecureProcessiongTestpasswd");
    String externalXmlRootElement =
            "<?xml version=\"1.0\"?>\r" +
                    "<!DOCTYPE foo\r" +
                    "[<!ENTITY xxe SYSTEM \"" + filename + "\">\r" +
                    "]>\r" +
                    "<secureProcessingFavoriteMovieXmlRootElement><title>&xxe;</title></secureProcessingFavoriteMovieXmlRootElement>";

    String externalXmlType =
            "<?xml version=\"1.0\"?>\r" +
                    "<!DOCTYPE foo\r" +
                    "[<!ENTITY xxe SYSTEM \"" + filename + "\">\r" +
                    "]>\r" +
                    "<favoriteMovie><title>&xxe;</title></favoriteMovie>";

    String externalJAXBElement =
            "<?xml version=\"1.0\"?>\r" +
                    "<!DOCTYPE foo\r" +
                    "[<!ENTITY xxe SYSTEM \"" + filename + "\">\r" +
                    "]>\r" +
                    "<favoriteMovieXmlType><title>&xxe;</title></favoriteMovieXmlType>";

    String externalCollection =
            "<?xml version=\"1.0\"?>\r" +
                    "<!DOCTYPE foo\r" +
                    "[<!ENTITY xxe SYSTEM \"" + filename + "\">\r" +
                    "]>\r" +
                    "<collection>" +
                    "  <secureProcessingFavoriteMovieXmlRootElement><title>&xxe;</title></secureProcessingFavoriteMovieXmlRootElement>" +
                    "  <secureProcessingFavoriteMovieXmlRootElement><title>&xxe;</title></secureProcessingFavoriteMovieXmlRootElement>" +
                    "</collection>";

    String externalMap =
            "<?xml version=\"1.0\"?>\r" +
                    "<!DOCTYPE foo\r" +
                    "[<!ENTITY xxe SYSTEM \"" + filename + "\">\r" +
                    "]>\r" +
                    "<map>" +
                    "<entry key=\"american\">" +
                    "<secureProcessingFavoriteMovieXmlRootElement><title>&xxe;</title></secureProcessingFavoriteMovieXmlRootElement>" +
                    "</entry>" +
                    "<entry key=\"french\">" +
                    "<secureProcessingFavoriteMovieXmlRootElement><title>&xxe;</title></secureProcessingFavoriteMovieXmlRootElement>" +
                    "</entry>" +
                    "</map>";

    @Deployment(name = "ddd", order = 1)
    public static Archive<?> createTestArchive_ddd() {
        return createTestArchive("ddd", "default_default_default");
    }

    @Deployment(name = "ddf", order = 2)
    public static Archive<?> createTestArchive_ddf() {
        return createTestArchive("ddf", "default_default_false");
    }

    @Deployment(name = "ddt", order = 3)
    public static Archive<?> createTestArchive_ddt() {
        return createTestArchive("ddt", "default_default_true");
    }

    @Deployment(name = "dfd", order = 4)
    public static Archive<?> createTestArchive_dfd() {
        return createTestArchive("dfd", "default_false_default");
    }

    @Deployment(name = "dff", order = 5)
    public static Archive<?> createTestArchive_dff() {
        return createTestArchive("dff", "default_false_false");
    }

    @Deployment(name = "dft", order = 6)
    public static Archive<?> createTestArchive_dft() {
        return createTestArchive("dft", "default_false_true");
    }

    @Deployment(name = "dtd", order = 7)
    public static Archive<?> createTestArchive_dtd() {
        return createTestArchive("dtd", "default_true_default");
    }

    @Deployment(name = "dtf", order = 8)
    public static Archive<?> createTestArchive_dtf() {
        return createTestArchive("dtf", "default_true_false");
    }

    @Deployment(name = "dtt", order = 9)
    public static Archive<?> createTestArchive_dtt() {
        return createTestArchive("dtt", "default_true_true");
    }

    @Deployment(name = "fdd", order = 10)
    public static Archive<?> createTestArchive_fdd() {
        return createTestArchive("fdd", "false_default_default");
    }

    @Deployment(name = "fdf", order = 11)
    public static Archive<?> createTestArchive_fdf() {
        return createTestArchive("fdf", "false_default_false");
    }

    @Deployment(name = "fdt", order = 12)
    public static Archive<?> createTestArchive_fdt() {
        return createTestArchive("fdt", "false_default_true");
    }

    @Deployment(name = "ffd", order = 13)
    public static Archive<?> createTestArchive_ffd() {
        return createTestArchive("ffd", "false_false_default");
    }

    static Archive<?> createTestArchive(String warExt, String webXmlExt) {
        WebArchive war = TestUtil.prepareArchive(URL_PREFIX + warExt);
        war.addClasses(SecureProcessingBar.class, SecureProcessingFavoriteMovie.class, SecureProcessingFavoriteMovieXmlRootElement.class);
        war.addClasses(SecureProcessingFavoriteMovieXmlType.class, ObjectFactory.class);
        war.addAsWebInfResource(SecureProcessing2Test.class.getPackage(), "SecureProcessing_external.dtd", "external.dtd");
        war.addAsWebInfResource(SecureProcessing2Test.class.getPackage(), "SecureProcessing_web_" + webXmlExt + ".xml", "web.xml");
        return TestUtil.finishContainerPrepare(war, null, SecureProcessingResource.class);
    }

    @Before
    public void init() {
        client = new ResteasyClientBuilder().build();
    }

    @After
    public void after() throws Exception {
        client.close();
    }

    /**
     * @tpTestDetails "resteasy.document.secure.processing.feature" is set to default value
     *                "resteasy.document.secure.disableDTDs" is set to default value
     *                "resteasy.document.expand.entity.references" is set to default value
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testSecurityDefaultDTDsDefaultExpansionDefault() throws Exception {
        doTestSkipFailsFailsSkip("ddd");
    }

    /**
     * @tpTestDetails "resteasy.document.secure.processing.feature" is set to default value
     *                "resteasy.document.secure.disableDTDs" is set to default value
     *                "resteasy.document.expand.entity.references" is set to false
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testSecurityDefaultDTDsDefaultExpansionFalse() throws Exception {
        doTestSkipFailsFailsSkip("ddf");
    }

    /**
     * @tpTestDetails "resteasy.document.secure.processing.feature" is set to default value
     *                "resteasy.document.secure.disableDTDs" is set to default value
     *                "resteasy.document.expand.entity.references" is set to true
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testSecurityDefaultDTDsDefaultExpansionTrue() throws Exception {
        doTestSkipFailsFailsSkip("ddt");
    }

    /**
     * @tpTestDetails "resteasy.document.secure.processing.feature" is set to default value
     *                "resteasy.document.secure.disableDTDs" is set to false
     *                "resteasy.document.expand.entity.references" is set to default value
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testSecurityDefaultDTDsFalseExpansionDefault() throws Exception {
        doTestFailsFailsPassesFails("dfd");
    }

    /**
     * @tpTestDetails "resteasy.document.secure.processing.feature" is set to default value
     *                "resteasy.document.secure.disableDTDs" is set to false
     *                "resteasy.document.expand.entity.references" is set to false
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testSecurityDefaultDTDsFalseExpansionFalse() throws Exception {
        doTestFailsFailsPassesFails("dff");
    }

    /**
     * @tpTestDetails "resteasy.document.secure.processing.feature" is set to default value
     *                "resteasy.document.secure.disableDTDs" is set to false
     *                "resteasy.document.expand.entity.references" is set to true
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testSecurityDefaultDTDsFalseExpansionTrue() throws Exception {
        doTestFailsFailsPassesPasses("dft");
    }

    /**
     * @tpTestDetails "resteasy.document.secure.processing.feature" is set to default value
     *                "resteasy.document.secure.disableDTDs" is set to true
     *                "resteasy.document.expand.entity.references" is set to default value
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testSecurityDefaultDTDsTrueExpansionDefault() throws Exception {
        doTestSkipFailsFailsSkip("dtd");
    }

    /**
     * @tpTestDetails "resteasy.document.secure.processing.feature" is set to default value
     *                "resteasy.document.secure.disableDTDs" is set to true
     *                "resteasy.document.expand.entity.references" is set to false
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testSecurityDefaultDTDsTrueExpansionFalse() throws Exception {
        doTestSkipFailsFailsSkip("dtf");
    }

    /**
     * @tpTestDetails "resteasy.document.secure.processing.feature" is set to default value
     *                "resteasy.document.secure.disableDTDs" is set to true
     *                "resteasy.document.expand.entity.references" is set to true
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testSecurityDefaultDTDsTrueExpansionTrue() throws Exception {
        doTestSkipFailsFailsSkip("dtt");
    }

    /**
     * @tpTestDetails "resteasy.document.secure.processing.feature" is set to false
     *                "resteasy.document.secure.disableDTDs" is set to default value
     *                "resteasy.document.expand.entity.references" is set to default value
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testSecurityFalseDTDsDefaultExpansionDefault() throws Exception {
        doTestSkipPassesFailsSkip("fdd");
    }

    /**
     * @tpTestDetails "resteasy.document.secure.processing.feature" is set to false
     *                "resteasy.document.secure.disableDTDs" is set to default value
     *                "resteasy.document.expand.entity.references" is set to false
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testSecurityFalseDTDsDefaultExpansionFalse() throws Exception {
        doTestSkipPassesFailsSkip("fdf");
    }

    /**
     * @tpTestDetails "resteasy.document.secure.processing.feature" is set to false
     *                "resteasy.document.secure.disableDTDs" is set to default value
     *                "resteasy.document.expand.entity.references" is set to true
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testSecurityFalseDTDsDefaultExpansionTrue() throws Exception {
        doTestSkipPassesFailsSkip("fdt");
    }

    /**
     * @tpTestDetails "resteasy.document.secure.processing.feature" is set to false
     *                "resteasy.document.secure.disableDTDs" is set to false
     *                "resteasy.document.expand.entity.references" is set to default value
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testSecurityFalseDTDsFalseExpansionDefault() throws Exception {
        doTestPassesPassesPassesFails("ffd");
    }

    /**
     * @tpTestDetails "resteasy.document.secure.processing.feature" is set to default value
     *                "resteasy.document.secure.disableDTDs" is set to true
     *                "resteasy.document.expand.entity.references" is set to true
     * @tpSince RESTEasy 3.1.0
     */
    @Test
    @Category({NotForForwardCompatibility.class})
    public void testSecurityDefaultDTDsTrueExpansionTrueWithApacheLinkMessage() throws Exception {
        doTestSkipFailsFailsSkipWithApacheLinkMessage("dtt");
    }


    void doTestSkipFailsFailsSkipWithApacheLinkMessage(String ext) throws Exception {
        doMaxAttributesFails(ext);
        doDTDFailsWithApacheLinkMessage(ext);
    }

    void doTestSkipFailsFailsSkip(String ext) throws Exception {
        doMaxAttributesFails(ext);
        doDTDFails(ext);
    }

    void doTestSkipPassesFailsSkip(String ext) throws Exception {
        doMaxAttributesPasses(ext);
        doDTDFails(ext);
    }

    void doTestFailsFailsPassesFails(String ext) throws Exception {
        doEntityExpansionFails(ext);
        doMaxAttributesFails(ext);
        doDTDPasses(ext);
        doExternalEntityExpansionFails(ext);
    }

    void doTestFailsFailsPassesPasses(String ext) throws Exception {
        doEntityExpansionFails(ext);
        doMaxAttributesFails(ext);
        doDTDPasses(ext);
        doExternalEntityExpansionPasses(ext);
    }

    void doTestPassesPassesPassesFails(String ext) throws Exception {
        doEntityExpansionPasses(ext);
        doMaxAttributesPasses(ext);
        doDTDPasses(ext);
        doDTDPasses(ext);
        doExternalEntityExpansionFails(ext);
    }

    void doTestPassesPassesPassesPasses(String ext) throws Exception {
        doEntityExpansionPasses(ext);
        doMaxAttributesPasses(ext);
        doDTDPasses(ext);
        doDTDPasses(ext);
        doExternalEntityExpansionPasses(ext);
    }

    void doEntityExpansionFails(String ext) throws Exception {
        logger.info("entering doEntityExpansionFails(" + ext + ")");
        {
            logger.info("Request body: " + bigXmlRootElement);
            Response response = client.target(generateURL("/entityExpansion/xmlRootElement/", URL_PREFIX + ext)).request()
                    .post(Entity.entity(bigXmlRootElement, "application/xml"));
            Assert.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
            String entity = response.readEntity(String.class);
            logger.info("doEntityExpansionFails() result: " + entity);
            Assert.assertThat("Wrong type of exception", entity, containsString("javax.xml.bind.UnmarshalException"));
        }
        {
            Response response = client.target(generateURL("/entityExpansion/xmlType/", URL_PREFIX + ext)).request()
                    .post(Entity.entity(bigXmlType, "application/xml"));
            Assert.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
            String entity = response.readEntity(String.class);
            logger.info("doEntityExpansionFails() result: " + entity);
            Assert.assertThat("Wrong type of exception", entity, containsString("javax.xml.bind.UnmarshalException"));
        }
        {
            Response response = client.target(generateURL("/entityExpansion/JAXBElement/", URL_PREFIX + ext)).request()
                    .post(Entity.entity(bigJAXBElement, "application/xml"));
            Assert.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
            String entity = response.readEntity(String.class);
            logger.info("doEntityExpansionFails() result: " + entity);
            Assert.assertThat("Wrong type of exception", entity, containsString("javax.xml.bind.UnmarshalException"));
        }
        {
            Response response = client.target(generateURL("/entityExpansion/collection/", URL_PREFIX + ext)).request()
                    .post(Entity.entity(bigCollection, "application/xml"));
            Assert.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
            String entity = response.readEntity(String.class);
            logger.info("doEntityExpansionFails() result: " + entity);
            Assert.assertThat("Wrong type of exception", entity, containsString("javax.xml.bind.UnmarshalException"));
        }
        {
            Response response = client.target(generateURL("/entityExpansion/map/", URL_PREFIX + ext)).request()
                    .post(Entity.entity(bigMap, "application/xml"));
            Assert.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
            String entity = response.readEntity(String.class);
            logger.info("doEntityExpansionFails() result: " + entity);
            Assert.assertThat("Wrong type of exception", entity, containsString("javax.xml.bind.UnmarshalException"));
        }
    }

    void doEntityExpansionPasses(String ext) throws Exception {
        logger.info("entering doEntityExpansionFails(" + ext + ")");
        {
            logger.info("Request body: " + bigXmlRootElement);
            Response response = client.target(generateURL("/entityExpansion/xmlRootElement/", URL_PREFIX + ext)).request()
                    .post(Entity.entity(bigXmlRootElement, "application/xml"));
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            String entity = response.readEntity(String.class);
            int len = Math.min(entity.length(), 30);
            logger.info("doEntityExpansionPasses() result: " + entity.substring(0, len) + "...");
            Assert.assertEquals("Wrong number of received \"foo\" in text", 1000000, countFoos(entity));
        }
        {
            Response response = client.target(generateURL("/entityExpansion/xmlType/", URL_PREFIX + ext)).request()
                    .post(Entity.entity(bigXmlType, "application/xml"));
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            String entity = response.readEntity(String.class);
            int len = Math.min(entity.length(), 30);
            logger.info("doEntityExpansionPasses() result: " + entity.substring(0, len) + "...");
            Assert.assertEquals("Wrong number of received \"foo\" in text", 1000000, countFoos(entity));
        }
        {
            Response response = client.target(generateURL("/entityExpansion/JAXBElement/", URL_PREFIX + ext)).request()
                    .post(Entity.entity(bigJAXBElement, "application/xml"));
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            String entity = response.readEntity(String.class);
            int len = Math.min(entity.length(), 30);
            logger.info("doEntityExpansionPasses() result: " + entity.substring(0, len) + "...");
            Assert.assertEquals("Wrong number of received \"foo\" in text", 1000000, countFoos(entity));
        }
        {
            Response response = client.target(generateURL("/entityExpansion/collection/", URL_PREFIX + ext)).request()
                    .post(Entity.entity(bigCollection, "application/xml"));
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            String entity = response.readEntity(String.class);
            int len = Math.min(entity.length(), 30);
            logger.info("doEntityExpansionPasses() result: " + entity.substring(0, len) + "...");
            Assert.assertEquals("Wrong number of received \"foo\" in text", 2000000, countFoos(entity));
        }
        {
            Response response = client.target(generateURL("/entityExpansion/map/", URL_PREFIX + ext)).request()
                    .post(Entity.entity(bigMap, "application/xml"));
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            String entity = response.readEntity(String.class);
            int len = Math.min(entity.length(), 30);
            logger.info("doEntityExpansionPasses() result: " + entity.substring(0, len) + "...");
            Assert.assertEquals("Wrong number of received \"foo\" in text", 2000000, countFoos(entity));
        }
    }

    void doMaxAttributesFails(String ext) throws Exception {
        logger.info("entering doMaxAttributesFails(" + ext + ")");
        Response response = client.target(generateURL("/maxAttributes/", URL_PREFIX + ext)).request()
                .post(Entity.entity(bigAttributeDoc, "application/xml"));
        logger.info("doMaxAttributesFails() status: " + response.getStatus());
        String entity = response.readEntity(String.class);
        logger.info("doMaxAttributesFails() result: " + entity);
    }

    void doMaxAttributesPasses(String ext) throws Exception {
        logger.info("entering doMaxAttributesPasses(" + ext + ")");
        Response response = client.target(generateURL("/maxAttributes/", URL_PREFIX + ext)).request()
                .post(Entity.entity(bigAttributeDoc, "application/xml"));
        logger.info("doMaxAttributesPasses() status: " + response.getStatus());
        String entity = response.readEntity(String.class);
        logger.info("doMaxAttributesPasses() result: " + entity);
        Assert.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
    }

    void doDTDFails(String ext) throws Exception {
        logger.info("entering doDTDFails(" + ext + ")");
        Response response = client.target(generateURL("/DTD/", URL_PREFIX + ext)).request()
                .post(Entity.entity(bar, "application/xml"));
        logger.info("status: " + response.getStatus());
        String entity = response.readEntity(String.class);
        logger.info("doDTDFails(): result: " + entity);
        Assert.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
        Assert.assertThat("Wrong exception in response", entity, startsWith("javax.xml.bind.UnmarshalException"));
        Assert.assertThat("Wrong content of response", entity, containsString("DOCTYPE"));
        Assert.assertThat("Wrong content of response", entity, containsString("true"));
    }

    void doDTDFailsWithApacheLinkMessage(String ext) throws Exception {
        logger.info("entering doDTDFails(" + ext + ")");
        Response response = client.target(generateURL("/DTD/", URL_PREFIX + ext)).request()
                .post(Entity.entity(bar, "application/xml"));
        logger.info("status: " + response.getStatus());
        String entity = response.readEntity(String.class);
        logger.info("doDTDFails(): result: " + entity);
        Assert.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
        Assert.assertThat("Wrong exception in response", entity, startsWith("javax.xml.bind.UnmarshalException"));
        Assert.assertThat("Wrong content of response", entity, containsString("DOCTYPE"));
        Assert.assertThat("Wrong content of response", entity, containsString("http:&#x2F;&#x2F;apache.org&#x2F;xml&#x2F;features&#x2F;disallow-doctype-decl"));
        Assert.assertThat("Wrong content of response", entity, containsString("true"));
    }

    void doDTDPasses(String ext) throws Exception {
        logger.info("entering doDTDPasses(" + ext + ")");
        Response response = client.target(generateURL("/DTD/", URL_PREFIX + ext)).request()
                .post(Entity.entity(bar, "application/xml"));
        String entity = response.readEntity(String.class);
        logger.info("doDTDPasses() result: " + entity);
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertThat("Wrong content of response", entity, containsString("junk"));
    }

    void doExternalEntityExpansionFails(String ext) throws Exception {
        logger.info("entering doExternalEntityExpansionFails(" + ext + ")");
        {
            Response response = client.target(generateURL("/entityExpansion/xmlRootElement/", URL_PREFIX + ext)).request()
                    .post(Entity.entity(externalXmlRootElement, "application/xml"));
            String entity = response.readEntity(String.class);
            logger.info("doExternalEntityExpansionFails() result: " + entity);
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            Assert.assertThat("Wrong content of response", entity, isEmptyString());
        }
        {
            Response response = client.target(generateURL("/entityExpansion/xmlType/", URL_PREFIX + ext)).request()
                    .post(Entity.entity(externalXmlType, "application/xml"));
            logger.info("status: " + response.getStatus());
            String entity = response.readEntity(String.class);
            logger.info("doExternalEntityExpansionFails() result: " + entity);
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            Assert.assertThat("Wrong content of response", entity, isEmptyString());
        }
        {
            Response response = client.target(generateURL("/entityExpansion/JAXBElement/", URL_PREFIX + ext)).request()
                    .post(Entity.entity(externalJAXBElement, "application/xml"));
            String entity = response.readEntity(String.class);
            logger.info("doExternalEntityExpansionFails() result: " + entity);
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            Assert.assertThat("Wrong content of response", entity, isEmptyString());
        }
        {
            Response response = client.target(generateURL("/entityExpansion/collection/", URL_PREFIX + ext)).request()
                    .post(Entity.entity(externalCollection, "application/xml"));
            String entity = response.readEntity(String.class);
            logger.info("doExternalEntityExpansionFails() result: " + entity);
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            Assert.assertThat("Wrong content of response", entity, isEmptyString());
        }
        {
            Response response = client.target(generateURL("/entityExpansion/map/", URL_PREFIX + ext)).request()
                    .post(Entity.entity(externalMap, "application/xml"));
            String entity = response.readEntity(String.class);
            logger.info("doExternalEntityExpansionFails() result: " + entity);
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            Assert.assertThat("Wrong content of response", entity, isEmptyString());
        }
    }

    void doExternalEntityExpansionPasses(String ext) throws Exception {
        logger.info("entering doExternalEntityExpansionPasses(" + ext + ")");
        {
            logger.info("externalXmlRootElement: " + externalXmlRootElement);
            Response response = client.target(generateURL("/entityExpansion/xmlRootElement/", URL_PREFIX + ext)).request()
                    .post(Entity.entity(externalXmlRootElement, "application/xml"));
            String entity = response.readEntity(String.class);
            int len = Math.min(entity.length(), 30);
            logger.info("doExternalEntityExpansionPasses() result: " + entity.substring(0, len) + "...");
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            Assert.assertThat("Content of response should contain password", entity, is("xx:xx:xx:xx:xx:xx:xx"));
        }
        {
            Response response = client.target(generateURL("/entityExpansion/xmlType/", URL_PREFIX + ext)).request()
                    .post(Entity.entity(externalXmlType, "application/xml"));
            String entity = response.readEntity(String.class);
            int len = Math.min(entity.length(), 30);
            logger.info("doExternalEntityExpansionPasses() result: " + entity.substring(0, len) + "...");
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            Assert.assertThat("Content of response should contain password", entity, is("xx:xx:xx:xx:xx:xx:xx"));
        }
        {
            Response response = client.target(generateURL("/entityExpansion/JAXBElement/", URL_PREFIX + ext)).request()
                    .post(Entity.entity(externalJAXBElement, "application/xml"));
            String entity = response.readEntity(String.class);
            int len = Math.min(entity.length(), 30);
            logger.info("doExternalEntityExpansionPasses() result: " + entity.substring(0, len) + "...");
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            Assert.assertThat("Content of response should contain password", entity, is("xx:xx:xx:xx:xx:xx:xx"));
        }
        {
            Response response = client.target(generateURL("/entityExpansion/collection/", URL_PREFIX + ext)).request()
                    .post(Entity.entity(externalCollection, "application/xml"));
            String entity = response.readEntity(String.class);
            int len = Math.min(entity.length(), 30);
            logger.info("doExternalEntityExpansionPasses() result: " + entity.substring(0, len) + "...");
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            Assert.assertThat("Content of response should contain password twice", entity, is("xx:xx:xx:xx:xx:xx:xxxx:xx:xx:xx:xx:xx:xx"));
        }
        {
            Response response = client.target(generateURL("/entityExpansion/map/", URL_PREFIX + ext)).request()
                    .post(Entity.entity(externalMap, "application/xml"));
            String entity = response.readEntity(String.class);
            int len = Math.min(entity.length(), 30);
            logger.info("doExternalEntityExpansionPasses() result: " + entity.substring(0, len) + "...");
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            Assert.assertThat("Content of response should contain password twice", entity, is("xx:xx:xx:xx:xx:xx:xxxx:xx:xx:xx:xx:xx:xx"));
        }
    }

    /**
     * Get count of "foo" substring in input string
     */
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
