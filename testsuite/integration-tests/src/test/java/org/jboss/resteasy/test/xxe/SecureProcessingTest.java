package org.jboss.resteasy.test.xxe;

import static org.jboss.resteasy.utils.PortProviderUtil.generateURL;

import java.io.File;
import java.util.List;

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
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.xxe.resource.ObjectFactory;
import org.jboss.resteasy.test.xxe.resource.SecureProcessingBar;
import org.jboss.resteasy.test.xxe.resource.SecureProcessingFavoriteMovie;
import org.jboss.resteasy.test.xxe.resource.SecureProcessingFavoriteMovieXmlRootElement;
import org.jboss.resteasy.test.xxe.resource.SecureProcessingFavoriteMovieXmlType;
import org.jboss.resteasy.test.xxe.resource.SecureProcessingResource;
import org.jboss.resteasy.utils.AssumeUtils;
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
 * @tpTestCaseDetails Regression test for RESTEASY-1103
 *                    RestEasy is vulnerable to XML Entity Denial of Service XXE is disabled.
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
@ServerSetup(DisableDefaultExceptionMapperSetupTask.class)
@DisabledForJreRange(min = JRE.JAVA_24)
public class SecureProcessingTest {

    protected final Logger logger = Logger.getLogger(SecureProcessingTest.class.getName());
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

    String bigElementDoctype = "<!DOCTYPE foodocument [" +
            "<!ENTITY foo 'foo'>" +
            "<!ENTITY foo1 '&foo;&foo;&foo;&foo;&foo;&foo;&foo;&foo;&foo;&foo;'>" +
            "<!ENTITY foo2 '&foo1;&foo1;&foo1;&foo1;&foo1;&foo1;&foo1;&foo1;&foo1;&foo1;'>" +
            "<!ENTITY foo3 '&foo2;&foo2;&foo2;&foo2;&foo2;&foo2;&foo2;&foo2;&foo2;&foo2;'>" +
            "<!ENTITY foo4 '&foo3;&foo3;&foo3;&foo3;&foo3;&foo3;&foo3;&foo3;&foo3;&foo3;'>" +
            "<!ENTITY foo5 '&foo4;&foo4;&foo4;&foo4;&foo4;&foo4;&foo4;&foo4;&foo4;&foo4;'>" +
            "<!ENTITY foo6 '&foo5;&foo5;&foo5;&foo5;&foo5;&foo5;&foo5;&foo5;&foo5;&foo5;'>" +
            "]>";

    String bigXmlRootElement = bigElementDoctype
            + "<secureProcessingFavoriteMovieXmlRootElement><title>&foo6;</title></secureProcessingFavoriteMovieXmlRootElement>";
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

    File file = new File("src/test/resources/org/jboss/resteasy/test/xxe/SecureProcessing_external.dtd");
    String secureProcessing_externalDtd = file.getAbsolutePath();
    String bar = "<!DOCTYPE secureProcessingBar SYSTEM \"" + secureProcessing_externalDtd
            + "\"><secureProcessingBar><s>junk</s></secureProcessingBar>";
    File file2 = new File("src/test/resources/org/jboss/resteasy/test/xxe/SecureProcessiongTestpasswd");
    String filename = file2.getAbsolutePath();
    String externalXmlRootElement = "<?xml version=\"1.0\"?>\r" +
            "<!DOCTYPE foo\r" +
            "[<!ENTITY xxe SYSTEM \"" + filename + "\">\r" +
            "]>\r" +
            "<secureProcessingFavoriteMovieXmlRootElement><title>&xxe;</title></secureProcessingFavoriteMovieXmlRootElement>";

    String externalXmlType = "<?xml version=\"1.0\"?>\r" +
            "<!DOCTYPE foo\r" +
            "[<!ENTITY xxe SYSTEM \"" + filename + "\">\r" +
            "]>\r" +
            "<favoriteMovie><title>&xxe;</title></favoriteMovie>";

    String externalJAXBElement = "<?xml version=\"1.0\"?>\r" +
            "<!DOCTYPE foo\r" +
            "[<!ENTITY xxe SYSTEM \"" + filename + "\">\r" +
            "]>\r" +
            "<favoriteMovieXmlType><title>&xxe;</title></favoriteMovieXmlType>";

    String externalCollection = "<?xml version=\"1.0\"?>\r" +
            "<!DOCTYPE foo\r" +
            "[<!ENTITY xxe SYSTEM \"" + filename + "\">\r" +
            "]>\r" +
            "<collection>" +
            "  <secureProcessingFavoriteMovieXmlRootElement><title>&xxe;</title></secureProcessingFavoriteMovieXmlRootElement>"
            +
            "  <secureProcessingFavoriteMovieXmlRootElement><title>&xxe;</title></secureProcessingFavoriteMovieXmlRootElement>"
            +
            "</collection>";

    String externalMap = "<?xml version=\"1.0\"?>\r" +
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

    @Deployment(name = "fff", order = 14)
    public static Archive<?> createTestArchive_fff() {
        return createTestArchive("fff", "false_false_false");
    }

    @Deployment(name = "fft", order = 15)
    public static Archive<?> createTestArchive_fft() {
        return createTestArchive("fft", "false_false_true");
    }

    @Deployment(name = "ftd", order = 16)
    public static Archive<?> createTestArchive_ftd() {
        return createTestArchive("ftd", "false_true_default");
    }

    @Deployment(name = "ftf", order = 17)
    public static Archive<?> createTestArchive_ftf() {
        return createTestArchive("ftf", "false_true_false");
    }

    @Deployment(name = "ftt", order = 18)
    public static Archive<?> createTestArchive_ftt() {
        return createTestArchive("ftt", "false_true_true");
    }

    @Deployment(name = "tdd", order = 19)
    public static Archive<?> createTestArchive_tdd() {
        return createTestArchive("tdd", "true_default_default");
    }

    @Deployment(name = "tdf", order = 20)
    public static Archive<?> createTestArchive_tdf() {
        return createTestArchive("tdf", "true_default_false");
    }

    @Deployment(name = "tdt", order = 21)
    public static Archive<?> createTestArchive_tdt() {
        return createTestArchive("tdt", "true_default_true");
    }

    @Deployment(name = "tfd", order = 22)
    public static Archive<?> createTestArchive_tfd() {
        return createTestArchive("tfd", "true_false_default");
    }

    @Deployment(name = "tff", order = 23)
    public static Archive<?> createTestArchive_tff() {
        return createTestArchive("tff", "true_false_false");
    }

    @Deployment(name = "tft", order = 24)
    public static Archive<?> createTestArchive_tft() {
        return createTestArchive("tft", "true_false_true");
    }

    @Deployment(name = "ttd", order = 25)
    public static Archive<?> createTestArchive_ttd() {
        return createTestArchive("ttd", "true_true_default");
    }

    @Deployment(name = "ttf", order = 26)
    public static Archive<?> createTestArchive_ttf() {
        return createTestArchive("ttf", "true_true_false");
    }

    @Deployment(name = "ttt", order = 27)
    public static Archive<?> createTestArchive_ttt() {
        return createTestArchive("ttt", "true_true_true");
    }

    static Archive<?> createTestArchive(String warExt, String webXmlExt) {
        WebArchive war = TestUtil.prepareArchive(URL_PREFIX + warExt);
        war.addClasses(SecureProcessingBar.class, SecureProcessingFavoriteMovie.class,
                SecureProcessingFavoriteMovieXmlRootElement.class);
        war.addClasses(SecureProcessingFavoriteMovieXmlType.class, ObjectFactory.class);
        war.addAsWebInfResource(SecureProcessingTest.class.getPackage(), "SecureProcessing_external.dtd", "external.dtd");
        war.addAsWebInfResource(SecureProcessingTest.class.getPackage(), "SecureProcessing_web_" + webXmlExt + ".xml",
                "web.xml");
        return TestUtil.finishContainerPrepare(war, null, SecureProcessingResource.class);
    }

    @BeforeEach
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
    }

    /**
     * @tpTestDetails "resteasy.document.secure.processing.feature" is set to false
     *                "resteasy.document.secure.disableDTDs" is set to false
     *                "resteasy.document.expand.entity.references" is set to false
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testSecurityFalseDTDsFalseExpansionFalse() throws Exception {
        AssumeUtils.canDisableFeatureSecureProcessing();
        doTestPassesPassesPassesFails("fff");
    }

    /**
     * @tpTestDetails "resteasy.document.secure.processing.feature" is set to false
     *                "resteasy.document.secure.disableDTDs" is set to false
     *                "resteasy.document.expand.entity.references" is set to true
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testSecurityFalseDTDsFalseExpansionTrue() throws Exception {
        AssumeUtils.canDisableFeatureSecureProcessing();
        doTestPassesPassesPassesPasses("fft");
    }

    /**
     * @tpTestDetails "resteasy.document.secure.processing.feature" is set to false
     *                "resteasy.document.secure.disableDTDs" is set to true
     *                "resteasy.document.expand.entity.references" is set to default value
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testSecurityFalseDTDsTrueExpansionDefault() throws Exception {
        AssumeUtils.canDisableFeatureSecureProcessing();
        doTestSkipPassesFailsSkip("ftd");
    }

    /**
     * @tpTestDetails "resteasy.document.secure.processing.feature" is set to false
     *                "resteasy.document.secure.disableDTDs" is set to true
     *                "resteasy.document.expand.entity.references" is set to false
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testSecurityFalseDTDsTrueExpansionFalse() throws Exception {
        AssumeUtils.canDisableFeatureSecureProcessing();
        doTestSkipPassesFailsSkip("ftf");
    }

    /**
     * @tpTestDetails "resteasy.document.secure.processing.feature" is set to false
     *                "resteasy.document.secure.disableDTDs" is set to true
     *                "resteasy.document.expand.entity.references" is set to true
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testSecurityFalseDTDsTrueExpansionTrue() throws Exception {
        AssumeUtils.canDisableFeatureSecureProcessing();
        doTestSkipPassesFailsSkip("ftt");
    }

    /**
     * @tpTestDetails "resteasy.document.secure.processing.feature" is set to true
     *                "resteasy.document.secure.disableDTDs" is set to default value
     *                "resteasy.document.expand.entity.references" is set to default value
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testSecurityTrueDTDsDefaultExpansionDefault() throws Exception {
        doTestSkipFailsFailsSkip("tdd");
    }

    /**
     * @tpTestDetails "resteasy.document.secure.processing.feature" is set to true
     *                "resteasy.document.secure.disableDTDs" is set to default value
     *                "resteasy.document.expand.entity.references" is set to false
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testSecurityTrueDTDsDefaultExpansionFalse() throws Exception {
        doTestSkipFailsFailsSkip("tdf");
    }

    /**
     * @tpTestDetails "resteasy.document.secure.processing.feature" is set to true
     *                "resteasy.document.secure.disableDTDs" is set to default value
     *                "resteasy.document.expand.entity.references" is set to true
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testSecurityTrueDTDsDefaultExpansionTrue() throws Exception {
        doTestSkipFailsFailsSkip("tdt");
    }

    /**
     * @tpTestDetails "resteasy.document.secure.processing.feature" is set to true
     *                "resteasy.document.secure.disableDTDs" is set to false
     *                "resteasy.document.expand.entity.references" is set to default value
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testSecurityTrueDTDsFalseExpansionDefault() throws Exception {
        // Leaving this heere because Xerces allows this, however new versions of JAXP do not and should not.
        //doTestFailsFailsPassesFails("tfd");
        final String ext = "tfd";
        doEntityExpansionFails(ext);
        doMaxAttributesFails(ext);
        // Expect a different failure as resteasy.document.secure.processing.feature is set to true. This setting
        // enables XMLConstants.FEATURE_SECURE_PROCESSING which does not allow external DTD's. With the JDK's
        // implementation, you cannot set this to true and expect external expansion to work.
        doDTDFails(ext, List.of("External DTD", "accessExternalDTD"));
        doExternalEntityExpansionFails(ext);
    }

    /**
     * @tpTestDetails "resteasy.document.secure.processing.feature" is set to true
     *                "resteasy.document.secure.disableDTDs" is set to false
     *                "resteasy.document.expand.entity.references" is set to false
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testSecurityTrueDTDsFalseExpansionFalse() throws Exception {
        // Leaving this heere because Xerces allows this, however new versions of JAXP do not and should not.
        //doTestFailsFailsPassesFails("tff");
        final String ext = "tff";
        doEntityExpansionFails(ext);
        doMaxAttributesFails(ext);
        // Expect a different failure as resteasy.document.secure.processing.feature is set to true. This setting
        // enables XMLConstants.FEATURE_SECURE_PROCESSING which does not allow external DTD's. With the JDK's
        // implementation, you cannot set this to true and expect external expansion to work.
        doDTDFails(ext, List.of("External DTD", "accessExternalDTD"));
        doExternalEntityExpansionFails(ext);
    }

    /**
     * @tpTestDetails "resteasy.document.secure.processing.feature" is set to true
     *                "resteasy.document.secure.disableDTDs" is set to false
     *                "resteasy.document.expand.entity.references" is set to true
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testSecurityTrueDTDsFalseExpansionTrue() throws Exception {
        doTestFailsFailsPassesPasses("tft");
    }

    /**
     * @tpTestDetails "resteasy.document.secure.processing.feature" is set to true
     *                "resteasy.document.secure.disableDTDs" is set to true
     *                "resteasy.document.expand.entity.references" is set to default value
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testSecurityTrueDTDsTrueExpansionDefault() throws Exception {
        doTestSkipFailsFailsSkip("ttd");
    }

    /**
     * @tpTestDetails "resteasy.document.secure.processing.feature" is set to true
     *                "resteasy.document.secure.disableDTDs" is set to true
     *                "resteasy.document.expand.entity.references" is set to false
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testSecurityTrueDTDsTrueExpansionFalse() throws Exception {
        doTestSkipFailsFailsSkip("ttf");
    }

    /**
     * @tpTestDetails "resteasy.document.secure.processing.feature" is set to true
     *                "resteasy.document.secure.disableDTDs" is set to true
     *                "resteasy.document.expand.entity.references" is set to true
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testSecurityTrueDTDsTrueExpansionTrue() throws Exception {
        doTestSkipFailsFailsSkip("ttt");
    }

    /**
     * @tpTestDetails "resteasy.document.secure.processing.feature" is set to true
     *                "resteasy.document.secure.disableDTDs" is set to true
     *                "resteasy.document.expand.entity.references" is set to true
     * @tpSince RESTEasy 3.1.0
     */
    @Test
    public void testSecurityTrueDTDsTrueExpansionTrueWithApacheLinkMessage() throws Exception {
        doTestSkipFailsFailsSkipWithApacheLinkMessage("ttt");
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
            Response response = client.target(generateURL("/entityExpansion/xmlRootElement/", URL_PREFIX + ext))
                    .request()
                    .post(Entity.entity(bigXmlRootElement, "application/xml"));
            Assertions.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
            String entity = response.readEntity(String.class);
            logger.info("doEntityExpansionFails() result: " + entity);
            Assertions.assertTrue(entity.contains("jakarta.xml.bind.UnmarshalException"),
                    "Wrong type of exception");
        }
        {
            Response response = client.target(generateURL("/entityExpansion/xmlType/", URL_PREFIX + ext)).request()
                    .post(Entity.entity(bigXmlType, "application/xml"));
            Assertions.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
            String entity = response.readEntity(String.class);
            logger.info("doEntityExpansionFails() result: " + entity);
            Assertions.assertTrue(entity.contains("jakarta.xml.bind.UnmarshalException"),
                    "Wrong type of exception");
        }
        {
            Response response = client.target(generateURL("/entityExpansion/JAXBElement/", URL_PREFIX + ext)).request()
                    .post(Entity.entity(bigJAXBElement, "application/xml"));
            Assertions.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
            String entity = response.readEntity(String.class);
            logger.info("doEntityExpansionFails() result: " + entity);
            Assertions.assertTrue(entity.contains("jakarta.xml.bind.UnmarshalException"),
                    "Wrong type of exception");
        }
        {
            Response response = client.target(generateURL("/entityExpansion/collection/", URL_PREFIX + ext)).request()
                    .post(Entity.entity(bigCollection, "application/xml"));
            Assertions.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
            String entity = response.readEntity(String.class);
            logger.info("doEntityExpansionFails() result: " + entity);
            Assertions.assertTrue(entity.contains("jakarta.xml.bind.UnmarshalException"),
                    "Wrong type of exception");
        }
        {
            Response response = client.target(generateURL("/entityExpansion/map/", URL_PREFIX + ext)).request()
                    .post(Entity.entity(bigMap, "application/xml"));
            Assertions.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
            String entity = response.readEntity(String.class);
            logger.info("doEntityExpansionFails() result: " + entity);
            Assertions.assertTrue(entity.contains("jakarta.xml.bind.UnmarshalException"),
                    "Wrong type of exception");
        }
    }

    void doEntityExpansionPasses(String ext) throws Exception {
        logger.info("entering doEntityExpansionFails(" + ext + ")");
        {
            logger.info("Request body: " + bigXmlRootElement);
            Response response = client.target(generateURL("/entityExpansion/xmlRootElement/", URL_PREFIX + ext))
                    .request()
                    .post(Entity.entity(bigXmlRootElement, "application/xml"));
            Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            String entity = response.readEntity(String.class);
            int len = Math.min(entity.length(), 30);
            logger.info("doEntityExpansionPasses() result: " + entity.substring(0, len) + "...");
            Assertions.assertEquals(1000000, countFoos(entity), "Wrong number of received \"foo\" in text");
        }
        {
            Response response = client.target(generateURL("/entityExpansion/xmlType/", URL_PREFIX + ext)).request()
                    .post(Entity.entity(bigXmlType, "application/xml"));
            Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            String entity = response.readEntity(String.class);
            int len = Math.min(entity.length(), 30);
            logger.info("doEntityExpansionPasses() result: " + entity.substring(0, len) + "...");
            Assertions.assertEquals(1000000, countFoos(entity), "Wrong number of received \"foo\" in text");
        }
        {
            Response response = client.target(generateURL("/entityExpansion/JAXBElement/", URL_PREFIX + ext)).request()
                    .post(Entity.entity(bigJAXBElement, "application/xml"));
            Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            String entity = response.readEntity(String.class);
            int len = Math.min(entity.length(), 30);
            logger.info("doEntityExpansionPasses() result: " + entity.substring(0, len) + "...");
            Assertions.assertEquals(1000000, countFoos(entity), "Wrong number of received \"foo\" in text");
        }
        {
            Response response = client.target(generateURL("/entityExpansion/collection/", URL_PREFIX + ext)).request()
                    .post(Entity.entity(bigCollection, "application/xml"));
            Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            String entity = response.readEntity(String.class);
            int len = Math.min(entity.length(), 30);
            logger.info("doEntityExpansionPasses() result: " + entity.substring(0, len) + "...");
            Assertions.assertEquals(2000000, countFoos(entity), "Wrong number of received \"foo\" in text");
        }
        {
            Response response = client.target(generateURL("/entityExpansion/map/", URL_PREFIX + ext)).request()
                    .post(Entity.entity(bigMap, "application/xml"));
            Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            String entity = response.readEntity(String.class);
            int len = Math.min(entity.length(), 30);
            logger.info("doEntityExpansionPasses() result: " + entity.substring(0, len) + "...");
            Assertions.assertEquals(2000000, countFoos(entity), "Wrong number of received \"foo\" in text");
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
        Assertions.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
    }

    void doDTDFails(String ext) throws Exception {
        doDTDFails(ext, List.of("DOCTYPE", "true"));
    }

    void doDTDFails(final String ext, final List<String> expected) {
        logger.info("entering doDTDFails(" + ext + ")");
        Response response = client.target(generateURL("/DTD/", URL_PREFIX + ext)).request()
                .post(Entity.entity(bar, "application/xml"));
        logger.info("status: " + response.getStatus());
        String entity = response.readEntity(String.class);
        logger.info("doDTDFails(): result: " + entity);
        Assertions.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
        Assertions.assertTrue(entity.contains("jakarta.xml.bind.UnmarshalException"),
                "Wrong exception in response");
        for (String s : expected) {
            Assertions.assertTrue(entity.contains(s), "Wrong content of response");
        }
    }

    void doDTDFailsWithApacheLinkMessage(String ext) throws Exception {
        logger.info("entering doDTDFails(" + ext + ")");
        Response response = client.target(generateURL("/DTD/", URL_PREFIX + ext)).request()
                .post(Entity.entity(bar, "application/xml"));
        logger.info("status: " + response.getStatus());
        String entity = response.readEntity(String.class);
        logger.info("doDTDFails(): result: " + entity);
        Assertions.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
        Assertions.assertTrue(entity.contains("jakarta.xml.bind.UnmarshalException"),
                "Wrong exception in response");
        Assertions.assertTrue(entity.contains("DOCTYPE"), "Wrong content of response");
        Assertions.assertTrue(entity.contains("http:&#x2F;&#x2F;apache.org&#x2F;xml&#x2F;features&#x2F;disallow-doctype-decl"),
                "Wrong content of response");
        Assertions.assertTrue(entity.contains("true"), "Wrong content of response");
    }

    void doDTDPasses(String ext) throws Exception {
        logger.info("entering doDTDPasses(" + ext + ")");
        Response response = client.target(generateURL("/DTD/", URL_PREFIX + ext)).request()
                .post(Entity.entity(bar, "application/xml"));
        logger.info("status: " + response.getStatus());
        String entity = response.readEntity(String.class);
        logger.info("doDTDPasses() result: " + entity);
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertTrue(entity.contains("junk"), "Wrong content of response");
    }

    void doExternalEntityExpansionFails(String ext) throws Exception {
        logger.info("entering doExternalEntityExpansionFails(" + ext + ")");
        {
            Response response = client.target(generateURL("/entityExpansion/xmlRootElement/", URL_PREFIX + ext))
                    .request()
                    .post(Entity.entity(externalXmlRootElement, "application/xml"));
            String entity = response.readEntity(String.class);
            logger.info("doExternalEntityExpansionFails() result: " + entity);
            Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            Assertions.assertTrue(entity.isEmpty(), "Wrong content of response");
        }
        {
            Response response = client.target(generateURL("/entityExpansion/xmlType/", URL_PREFIX + ext)).request()
                    .post(Entity.entity(externalXmlType, "application/xml"));
            String entity = response.readEntity(String.class);
            logger.info("doExternalEntityExpansionFails() result: " + entity);
            Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            Assertions.assertTrue(entity.isEmpty(), "Wrong content of response");
        }
        {
            Response response = client.target(generateURL("/entityExpansion/JAXBElement/", URL_PREFIX + ext)).request()
                    .post(Entity.entity(externalJAXBElement, "application/xml"));
            String entity = response.readEntity(String.class);
            logger.info("doExternalEntityExpansionFails() result: " + entity);
            Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            Assertions.assertTrue(entity.isEmpty(), "Wrong content of response");
        }
        {
            Response response = client.target(generateURL("/entityExpansion/collection/", URL_PREFIX + ext)).request()
                    .post(Entity.entity(externalCollection, "application/xml"));
            String entity = response.readEntity(String.class);
            logger.info("doExternalEntityExpansionFails() result: " + entity);
            Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            Assertions.assertTrue(entity.isEmpty(), "Wrong content of response");
        }
        {
            Response response = client.target(generateURL("/entityExpansion/map/", URL_PREFIX + ext)).request()
                    .post(Entity.entity(externalMap, "application/xml"));
            String entity = response.readEntity(String.class);
            logger.info("doExternalEntityExpansionFails() result: " + entity);
            Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            Assertions.assertTrue(entity.isEmpty(), "Wrong content of response");
        }
    }

    void doExternalEntityExpansionPasses(String ext) throws Exception {
        logger.info("entering doExternalEntityExpansionPasses(" + ext + ")");
        {
            logger.info("externalXmlRootElement: " + externalXmlRootElement);
            Response response = client.target(generateURL("/entityExpansion/xmlRootElement/", URL_PREFIX + ext))
                    .request()
                    .post(Entity.entity(externalXmlRootElement, "application/xml"));
            String entity = response.readEntity(String.class);
            int len = Math.min(entity.length(), 30);
            logger.info("doExternalEntityExpansionPasses() result: " + entity.substring(0, len) + "...");
            Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            Assertions.assertTrue(entity.equals("xx:xx:xx:xx:xx:xx:xx"), "Content of response should contain password");
        }
        {
            Response response = client.target(generateURL("/entityExpansion/xmlType/", URL_PREFIX + ext)).request()
                    .post(Entity.entity(externalXmlType, "application/xml"));
            String entity = response.readEntity(String.class);
            int len = Math.min(entity.length(), 30);
            logger.info("doExternalEntityExpansionPasses() result: " + entity.substring(0, len) + "...");
            Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            Assertions.assertTrue(entity.equals("xx:xx:xx:xx:xx:xx:xx"), "Content of response should contain password");
        }
        {
            Response response = client.target(generateURL("/entityExpansion/JAXBElement/", URL_PREFIX + ext)).request()
                    .post(Entity.entity(externalJAXBElement, "application/xml"));
            String entity = response.readEntity(String.class);
            int len = Math.min(entity.length(), 30);
            logger.info("doExternalEntityExpansionPasses() result: " + entity.substring(0, len) + "...");
            Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            Assertions.assertTrue(entity.equals("xx:xx:xx:xx:xx:xx:xx"), "Content of response should contain password");
        }
        {
            Response response = client.target(generateURL("/entityExpansion/collection/", URL_PREFIX + ext)).request()
                    .post(Entity.entity(externalCollection, "application/xml"));
            String entity = response.readEntity(String.class);
            int len = Math.min(entity.length(), 30);
            logger.info("doExternalEntityExpansionPasses() result: " + entity.substring(0, len) + "...");
            Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            Assertions.assertTrue(entity.equals("xx:xx:xx:xx:xx:xx:xxxx:xx:xx:xx:xx:xx:xx"),
                    "Content of response should contain password twice");
        }
        {
            Response response = client.target(generateURL("/entityExpansion/map/", URL_PREFIX + ext)).request()
                    .post(Entity.entity(externalMap, "application/xml"));
            String entity = response.readEntity(String.class);
            int len = Math.min(entity.length(), 30);
            logger.info("doExternalEntityExpansionPasses() result: " + entity.substring(0, len) + "...");
            Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            Assertions.assertTrue(entity.equals("xx:xx:xx:xx:xx:xx:xxxx:xx:xx:xx:xx:xx:xx"),
                    "Content of response should contain password twice");
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
