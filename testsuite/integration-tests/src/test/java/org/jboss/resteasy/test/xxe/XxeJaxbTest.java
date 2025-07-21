package org.jboss.resteasy.test.xxe;

import java.io.File;
import java.util.Hashtable;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.xxe.resource.xxeJaxb.ObjectFactory;
import org.jboss.resteasy.test.xxe.resource.xxeJaxb.XxeJaxbFavoriteMovie;
import org.jboss.resteasy.test.xxe.resource.xxeJaxb.XxeJaxbFavoriteMovieXmlRootElement;
import org.jboss.resteasy.test.xxe.resource.xxeJaxb.XxeJaxbFavoriteMovieXmlType;
import org.jboss.resteasy.test.xxe.resource.xxeJaxb.XxeJaxbMovieResource;
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
 * @tpTestCaseDetails Regression test for RESTEASY-1103, RESTEASY-647.
 *                    RestEasy is vulnerable to XML Entity Denial of Service XXE is disabled.
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class XxeJaxbTest {

    static ResteasyClient client;
    private Logger logger = Logger.getLogger(XxeJaxbTest.class);
    private static final String URL_PREFIX = "RESTEASY-1103-";
    private String passwdFile = new File(TestUtil.getResourcePath(XxeJaxbTest.class, "XxeJaxbPasswd")).getAbsolutePath();

    String strMovie = "<?xml version=\"1.0\"?>\r" +
            "<!DOCTYPE foo\r" +
            "[<!ENTITY xxe SYSTEM \"" + passwdFile + "\">\r" +
            "]>\r" +
            "<xxeJaxbFavoriteMovie><title>&xxe;</title></xxeJaxbFavoriteMovie>";

    String strMovieRootElement = "<?xml version=\"1.0\"?>\r" +
            "<!DOCTYPE foo\r" +
            "[<!ENTITY xxe SYSTEM \"" + passwdFile + "\">\r" +
            "]>\r" +
            "<xxeJaxbFavoriteMovieXmlRootElement><title>&xxe;</title></xxeJaxbFavoriteMovieXmlRootElement>";

    String strMovieXmlType = "<?xml version=\"1.0\"?>\r" +
            "<!DOCTYPE foo\r" +
            "[<!ENTITY xxe SYSTEM \"" + passwdFile + "\">\r" +
            "]>\r" +
            "<xxeJaxbFavoriteMovieXmlType><title>&xxe;</title></xxeJaxbFavoriteMovieXmlType>";

    String strMovieRootElementCollection = "<?xml version=\"1.0\"?>\r" +
            "<!DOCTYPE foo\r" +
            "[<!ENTITY xxe SYSTEM \"" + passwdFile + "\">\r" +
            "]>\r" +
            "<collection>" +
            "<xxeJaxbFavoriteMovieXmlRootElement><title>&xxe;</title></xxeJaxbFavoriteMovieXmlRootElement>" +
            "<xxeJaxbFavoriteMovieXmlRootElement><title>Le Regle de Jeu</title></xxeJaxbFavoriteMovieXmlRootElement>" +
            "</collection>";

    String strMovieRootElementMap = "<?xml version=\"1.0\"?>\r" +
            "<!DOCTYPE foo\r" +
            "[<!ENTITY xxe SYSTEM \"" + passwdFile + "\">\r" +
            "]>\r" +
            "<map>" +
            "<entry key=\"american\">" +
            "<xxeJaxbFavoriteMovieXmlRootElement><title>&xxe;</title></xxeJaxbFavoriteMovieXmlRootElement>" +
            "</entry>" +
            "<entry key=\"french\">" +
            "<xxeJaxbFavoriteMovieXmlRootElement><title>La Regle de Jeu</title></xxeJaxbFavoriteMovieXmlRootElement>" +
            "</entry>" +
            "</map>";

    @BeforeEach
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
        client = null;
    }

    @Deployment(name = "f", order = 1)
    public static Archive<?> createTestArchive_f() {
        return createTestArchiveEnableSecurityFeature("f", "false");
    }

    @Deployment(name = "t", order = 2)
    public static Archive<?> createTestArchive_t() {
        return createTestArchiveEnableSecurityFeature("t", "true");
    }

    @Deployment(name = "ff", order = 3)
    public static Archive<?> createTestArchive_ff() {
        return createTestArchiveExpandEntityReferencesEnableSecurityFeature("ff", "false", "false");
    }

    @Deployment(name = "ft", order = 4)
    public static Archive<?> createTestArchive_ft() {
        return createTestArchiveExpandEntityReferencesEnableSecurityFeature("ft", "false", "true");
    }

    @Deployment(name = "tf", order = 5)
    public static Archive<?> createTestArchive_tf() {
        return createTestArchiveExpandEntityReferencesEnableSecurityFeature("tf", "true", "false");
    }

    @Deployment(name = "tt", order = 6)
    public static Archive<?> createTestArchive_tt() {
        return createTestArchiveExpandEntityReferencesEnableSecurityFeature("tt", "true", "true");
    }

    static Archive<?> createTestArchiveEnableSecurityFeature(String warExt, String enable) {
        WebArchive war = TestUtil.prepareArchive(URL_PREFIX + warExt);
        war.addClasses(XxeJaxbFavoriteMovie.class, XxeJaxbFavoriteMovieXmlRootElement.class, XxeJaxbFavoriteMovieXmlType.class,
                ObjectFactory.class);
        Hashtable<String, String> contextParams = new Hashtable<String, String>();
        contextParams.put("resteasy.document.secure.processing.feature", enable);
        contextParams.put("resteasy.document.secure.disableDTDs", "false");
        return TestUtil.finishContainerPrepare(war, contextParams, XxeJaxbMovieResource.class);
    }

    static Archive<?> createTestArchiveExpandEntityReferencesEnableSecurityFeature(String warExt, String expand,
            String enable) {
        WebArchive war = TestUtil.prepareArchive(URL_PREFIX + warExt);
        war.addClasses(XxeJaxbFavoriteMovie.class, XxeJaxbFavoriteMovieXmlRootElement.class, XxeJaxbFavoriteMovieXmlType.class,
                ObjectFactory.class);
        Hashtable<String, String> contextParams = new Hashtable<String, String>();
        contextParams.put("resteasy.document.secure.processing.feature", enable);
        contextParams.put("resteasy.document.secure.disableDTDs", "false");
        contextParams.put("resteasy.document.expand.entity.references", expand);
        return TestUtil.finishContainerPrepare(war, contextParams, XxeJaxbMovieResource.class);
    }

    /**
     * @tpTestDetails Test on jaxb object annotated with @XmlRootElement
     *                "resteasy.document.secure.processing.feature" is set to "false"
     * @tpPassCrit Passwd file should not be returned by the response.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @OperateOnDeployment("f")
    public void testXmlRootElementDefaultFalse() throws Exception {
        doTestXmlRootElementDefault("f");
    }

    /**
     * @tpTestDetails Test on jaxb object annotated with @XmlRootElement
     *                "resteasy.document.secure.processing.feature" is set to "true"
     * @tpPassCrit Passwd file should not be returned by the response.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @OperateOnDeployment("t")
    public void testXmlRootElementDefaultTrue() throws Exception {
        doTestXmlRootElementDefault("t");
    }

    /**
     * @tpTestDetails Test on jaxb object annotated with @XmlRootElement
     *                "resteasy.document.secure.processing.feature" is set to "false"
     *                "resteasy.document.expand.entity.references" is set to "false"
     * @tpPassCrit Passwd file should not be returned by the response.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @OperateOnDeployment("ff")
    public void testXmlRootElementWithoutExpansionFalse() throws Exception {
        doTestXmlRootElementWithoutExpansion("ff");
    }

    /**
     * @tpTestDetails Test on jaxb object annotated with @XmlRootElement
     *                "resteasy.document.secure.processing.feature" is set to "true"
     *                "resteasy.document.expand.entity.references" is set to "false"
     * @tpPassCrit Passwd file should not be returned by the response.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @OperateOnDeployment("ft")
    public void testXmlRootElementWithoutExpansionTrue() throws Exception {
        doTestXmlRootElementWithoutExpansion("ft");
    }

    /**
     * @tpTestDetails Test on jaxb object annotated with @XmlRootElement
     *                "resteasy.document.secure.processing.feature" is set to "false"
     *                "resteasy.document.expand.entity.references" is set to "true"
     * @tpPassCrit Passwd file should be returned by the response.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @OperateOnDeployment("tf")
    public void testXmlRootElementWithExpansionFalse() throws Exception {
        doTestXmlRootElementWithExpansion("tf");
    }

    /**
     * @tpTestDetails Test on jaxb object annotated with @XmlRootElement
     *                "resteasy.document.secure.processing.feature" is set to "true"
     *                "resteasy.document.expand.entity.references" is set to "true"
     * @tpPassCrit Passwd file should be returned by the response.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @OperateOnDeployment("tt")
    public void testXmlRootElementWithExpansionTrue() throws Exception {
        doTestXmlRootElementWithExpansion("tt");
    }

    /**
     * @tpTestDetails Test on jaxb object annotated with @XmlType
     *                "resteasy.document.secure.processing.feature" is set to "false"
     * @tpPassCrit Passwd file should not be returned by the response.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @OperateOnDeployment("f")
    public void testXmlTypeDefaultFalse() throws Exception {
        doTestXmlTypeDefault("f");
    }

    /**
     * @tpTestDetails Test on jaxb object annotated with @XmlType
     *                "resteasy.document.secure.processing.feature" is set to "true"
     * @tpPassCrit Passwd file should not be returned by the response.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @OperateOnDeployment("t")
    public void testXmlTypeDefaultTrue() throws Exception {
        doTestXmlTypeDefault("t");
    }

    /**
     * @tpTestDetails Test on jaxb object annotated with @XmlType
     *                "resteasy.document.secure.processing.feature" is set to "false"
     *                "resteasy.document.expand.entity.references" is set to "false"
     * @tpPassCrit Passwd file should not be returned by the response.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @OperateOnDeployment("ff")
    public void testXmlTypeWithoutExpansionFalse() throws Exception {
        doTestXmlTypeWithoutExpansion("ff");
    }

    /**
     * @tpTestDetails Test on jaxb object annotated with @XmlType
     *                "resteasy.document.secure.processing.feature" is set to "true"
     *                "resteasy.document.expand.entity.references" is set to "false"
     * @tpPassCrit Passwd file should not be returned by the response.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @OperateOnDeployment("ft")
    public void testXmlTypeWithoutExpansionTrue() throws Exception {
        doTestXmlTypeWithoutExpansion("ft");
    }

    /**
     * @tpTestDetails Test on jaxb object annotated with @XmlType
     *                "resteasy.document.secure.processing.feature" is set to "false"
     *                "resteasy.document.expand.entity.references" is set to "true"
     * @tpPassCrit Passwd file should be returned by the response.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @OperateOnDeployment("tf")
    public void testXmlTypeWithExpansionFalse() throws Exception {
        doTestXmlTypeWithExpansion("tf");
    }

    /**
     * @tpTestDetails Test on jaxb object annotated with @XmlType
     *                "resteasy.document.secure.processing.feature" is set to "true"
     *                "resteasy.document.expand.entity.references" is set to "true"
     * @tpPassCrit Passwd file should be returned by the response.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @OperateOnDeployment("tt")
    public void testXmlTypeWithExpansionTrue() throws Exception {
        doTestXmlTypeWithExpansion("tt");
    }

    /**
     * @tpTestDetails Test on jaxb object annotated with @XmlType and resource binding xml element to JaxbElement
     *                "resteasy.document.secure.processing.feature" is set to "false"
     * @tpPassCrit Passwd file should not be returned by the response.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @OperateOnDeployment("f")
    public void testJAXBElementDefaultFalse() throws Exception {
        doTestJAXBElementDefault("f");
    }

    /**
     * @tpTestDetails Test on jaxb object annotated with @XmlType and resource binding xml element to JaxbElement
     *                "resteasy.document.secure.processing.feature" is set to "true"
     * @tpPassCrit Passwd file should not be returned by the response.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @OperateOnDeployment("t")
    public void testJAXBElementDefaultTrue() throws Exception {
        doTestJAXBElementDefault("t");
    }

    /**
     * @tpTestDetails Test on jaxb object annotated with @XmlType and resource binding xml element to JaxbElement
     *                "resteasy.document.secure.processing.feature" is set to "false"
     *                "resteasy.document.expand.entity.references" is set to "false"
     * @tpPassCrit Passwd file should not be returned by the response.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @OperateOnDeployment("ff")
    public void testJAXBElementWithoutExpansionFalse() throws Exception {
        doTestJAXBElementWithoutExpansion("ff");
    }

    /**
     * @tpTestDetails Test on jaxb object annotated with @XmlType and resource binding xml element to JaxbElement
     *                "resteasy.document.secure.processing.feature" is set to "true"
     *                "resteasy.document.expand.entity.references" is set to "false"
     * @tpPassCrit Passwd file should not be returned by the response.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @OperateOnDeployment("ft")
    public void testJAXBElementWithoutExpansionTrue() throws Exception {
        doTestJAXBElementWithoutExpansion("ft");
    }

    /**
     * @tpTestDetails Test on jaxb object annotated with @XmlType and resource binding xml element to JaxbElement
     *                "resteasy.document.secure.processing.feature" is set to "false"
     *                "resteasy.document.expand.entity.references" is set to "true"
     * @tpPassCrit Passwd file should be returned by the response.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @OperateOnDeployment("tf")
    public void testJAXBElementWithExpansionFalse() throws Exception {
        doTestJAXBElementWithExpansion("tf");
    }

    /**
     * @tpTestDetails Test on jaxb object annotated with @XmlType and resource binding xml element to JaxbElement
     *                "resteasy.document.secure.processing.feature" is set to "true"
     *                "resteasy.document.expand.entity.references" is set to "true"
     * @tpPassCrit Passwd file should be returned by the response.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @OperateOnDeployment("tt")
    public void testJAXBElementWithExpansionTrue() throws Exception {
        doTestJAXBElementWithExpansion("tt");
    }

    /**
     * @tpTestDetails Test on jaxb object annotated with @XmlRootElement which placed in xml string within
     *                <collection></collection>
     *                tags. Resource binds such xml correctly into a list object
     *                "resteasy.document.secure.processing.feature" is set to "false"
     * @tpPassCrit Passwd file should not be returned by the response.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @OperateOnDeployment("f")
    public void testListDefaultFalse() throws Exception {
        doCollectionTestWithoutExpansion("f", "list");
    }

    /**
     * @tpTestDetails Test on jaxb object annotated with @XmlRootElement which placed in xml string within
     *                <collection></collection>
     *                tags. Resource binds such xml correctly into a list object
     *                "resteasy.document.secure.processing.feature" is set to "true"
     * @tpPassCrit Passwd file should not be returned by the response.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @OperateOnDeployment("t")
    public void testListDefaultTrue() throws Exception {
        doCollectionTestWithoutExpansion("t", "list");
    }

    /**
     * @tpTestDetails Test on jaxb object annotated with @XmlRootElement which placed in xml string within
     *                <collection></collection>
     *                tags. Resource binds such xml correctly into a list object
     *                "resteasy.document.secure.processing.feature" is set to "false"
     *                "resteasy.document.expand.entity.references" is set to "false"
     * @tpPassCrit Passwd file should not be returned by the response.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @OperateOnDeployment("ff")
    public void testListWithoutExpansionFalse() throws Exception {
        doCollectionTestWithoutExpansion("ff", "list");
    }

    /**
     * @tpTestDetails Test on jaxb object annotated with @XmlRootElement which placed in xml string within
     *                <collection></collection>
     *                tags. Resource binds such xml correctly into a list object
     *                "resteasy.document.secure.processing.feature" is set to "true"
     *                "resteasy.document.expand.entity.references" is set to "false"
     * @tpPassCrit Passwd file should not be returned by the response.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @OperateOnDeployment("ft")
    public void testListWithoutExpansionTrue() throws Exception {
        doCollectionTestWithoutExpansion("ft", "list");
    }

    /**
     * @tpTestDetails Test on jaxb object annotated with @XmlRootElement which placed in xml string within
     *                <collection></collection>
     *                tags. Resource binds such xml correctly into a list object
     *                "resteasy.document.secure.processing.feature" is set to "false"
     *                "resteasy.document.expand.entity.references" is set to "true"
     * @tpPassCrit Passwd file should be returned by the response.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @OperateOnDeployment("tf")
    public void testListWithExpansionFalse() throws Exception {
        doCollectionTestWithExpansion("tf", "list");
    }

    /**
     * @tpTestDetails Test on jaxb object annotated with @XmlRootElement which placed in xml string within
     *                <collection></collection>
     *                tags. Resource binds such xml correctly into a list object
     *                "resteasy.document.secure.processing.feature" is set to "true"
     *                "resteasy.document.expand.entity.references" is set to "true"
     * @tpPassCrit Passwd file should be returned by the response.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @OperateOnDeployment("tt")
    public void testListWithExpansionTrue() throws Exception {
        doCollectionTestWithExpansion("tt", "list");
    }

    /**
     * @tpTestDetails Test on jaxb object annotated with @XmlRootElement which placed in xml string within
     *                <collection></collection>
     *                tags. Resource binds such xml correctly into a Set object
     *                "resteasy.document.secure.processing.feature" is set to "false"
     * @tpPassCrit Passwd file should not be returned by the response.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @OperateOnDeployment("f")
    public void testSetDefaultFalse() throws Exception {
        doCollectionTestWithoutExpansion("f", "set");
    }

    /**
     * @tpTestDetails Test on jaxb object annotated with @XmlRootElement which placed in xml string within
     *                <collection></collection>
     *                tags. Resource binds such xml correctly into a Set object
     *                "resteasy.document.secure.processing.feature" is set to "true"
     * @tpPassCrit Passwd file should not be returned by the response.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @OperateOnDeployment("t")
    public void testSetDefaultTrue() throws Exception {
        doCollectionTestWithoutExpansion("t", "set");
    }

    /**
     * @tpTestDetails Test on jaxb object annotated with @XmlRootElement which placed in xml string within
     *                <collection></collection>
     *                tags. Resource binds such xml correctly into a Set object
     *                "resteasy.document.secure.processing.feature" is set to "false"
     *                "resteasy.document.expand.entity.references" is set to "false"
     * @tpPassCrit Passwd file should not be returned by the response.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @OperateOnDeployment("ff")
    public void testSetWithoutExpansionFalse() throws Exception {
        doCollectionTestWithoutExpansion("ff", "set");
    }

    /**
     * @tpTestDetails Test on jaxb object annotated with @XmlRootElement which placed in xml string within
     *                <collection></collection>
     *                tags. Resource binds such xml correctly into a Set object
     *                "resteasy.document.secure.processing.feature" is set to "true"
     *                "resteasy.document.expand.entity.references" is set to "false"
     * @tpPassCrit Passwd file should not be returned by the response.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @OperateOnDeployment("ft")
    public void testSetWithoutExpansionTrue() throws Exception {
        doCollectionTestWithoutExpansion("ft", "set");
    }

    /**
     * @tpTestDetails Test on jaxb object annotated with @XmlRootElement which placed in xml string within
     *                <collection></collection>
     *                tags. Resource binds such xml correctly into a Set object
     *                "resteasy.document.secure.processing.feature" is set to "false"
     *                "resteasy.document.expand.entity.references" is set to "true"
     * @tpPassCrit Passwd file should be returned by the response.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @OperateOnDeployment("tf")
    public void testSetWithExpansionFalse() throws Exception {
        doCollectionTestWithExpansion("tf", "set");
    }

    /**
     * @tpTestDetails Test on jaxb object annotated with @XmlRootElement which placed in xml string within
     *                <collection></collection>
     *                tags. Resource binds such xml correctly into a Set object
     *                "resteasy.document.secure.processing.feature" is set to "true"
     *                "resteasy.document.expand.entity.references" is set to "true"
     * @tpPassCrit Passwd file should be returned by the response.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @OperateOnDeployment("tt")
    public void testSetWithExpansionTrue() throws Exception {
        doCollectionTestWithExpansion("tt", "set");
    }

    /**
     * @tpTestDetails Test on jaxb object annotated with @XmlRootElement which placed in xml string within
     *                <collection></collection>
     *                tags. Resource binds such xml correctly into an array
     *                "resteasy.document.secure.processing.feature" is set to "false"
     * @tpPassCrit Passwd file should not be returned by the response.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @OperateOnDeployment("f")
    public void testArrayDefaultFalse() throws Exception {
        doCollectionTestWithoutExpansion("f", "array");
    }

    /**
     * @tpTestDetails Test on jaxb object annotated with @XmlRootElement which placed in xml string within
     *                <collection></collection>
     *                tags. Resource binds such xml correctly into an array
     *                "resteasy.document.secure.processing.feature" is set to "true"
     * @tpPassCrit Passwd file should not be returned by the response.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @OperateOnDeployment("t")
    public void testArrayDefaultTrue() throws Exception {
        doCollectionTestWithoutExpansion("t", "array");
    }

    /**
     * @tpTestDetails Test on jaxb object annotated with @XmlRootElement which placed in xml string within
     *                <collection></collection>
     *                tags. Resource binds such xml correctly into an array
     *                "resteasy.document.secure.processing.feature" is set to "false"
     *                "resteasy.document.expand.entity.references" is set to "false"
     * @tpPassCrit Passwd file should not be returned by the response.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @OperateOnDeployment("ff")
    public void testArrayWithoutExpansionFalse() throws Exception {
        doCollectionTestWithoutExpansion("ff", "array");
    }

    /**
     * @tpTestDetails Test on jaxb object annotated with @XmlRootElement which placed in xml string within
     *                <collection></collection>
     *                tags. Resource binds such xml correctly into an array
     *                "resteasy.document.secure.processing.feature" is set to "true"
     *                "resteasy.document.expand.entity.references" is set to "false"
     * @tpPassCrit Passwd file should not be returned by the response.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @OperateOnDeployment("ft")
    public void testArrayWithoutExpansionTrue() throws Exception {
        doCollectionTestWithoutExpansion("ft", "array");
    }

    /**
     * @tpTestDetails Test on jaxb object annotated with @XmlRootElement which placed in xml string within
     *                <collection></collection>
     *                tags. Resource binds such xml correctly into an array
     *                "resteasy.document.secure.processing.feature" is set to "false"
     *                "resteasy.document.expand.entity.references" is set to "true"
     * @tpPassCrit Passwd file should be returned by the response.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @OperateOnDeployment("tf")
    public void testArrayWithExpansionFalse() throws Exception {
        doCollectionTestWithExpansion("tf", "array");
    }

    /**
     * @tpTestDetails Test on jaxb object annotated with @XmlRootElement which placed in xml string within
     *                <collection></collection>
     *                tags. Resource binds such xml correctly into an array
     *                "resteasy.document.secure.processing.feature" is set to "true"
     *                "resteasy.document.expand.entity.references" is set to "true"
     * @tpPassCrit Passwd file should be returned by the response.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @OperateOnDeployment("tt")
    public void testArrayWithExpansionTrue() throws Exception {
        doCollectionTestWithExpansion("tt", "array");
    }

    /**
     * @tpTestDetails Test on jaxb object annotated with @XmlRootElement which placed in xml string within <map></map>
     *                tags. Resource binds such xml correctly into a map
     *                "resteasy.document.secure.processing.feature" is set to "false"
     * @tpPassCrit Passwd file should not be returned by the response.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @OperateOnDeployment("f")
    public void testMapDefaultFalse() throws Exception {
        doMapTestWithoutExpansion("f");
    }

    /**
     * @tpTestDetails Test on jaxb object annotated with @XmlRootElement which placed in xml string within <map></map>
     *                tags. Resource binds such xml correctly into a map
     *                "resteasy.document.secure.processing.feature" is set to "true"
     * @tpPassCrit Passwd file should not be returned by the response.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @OperateOnDeployment("t")
    public void testMapDefaultTrue() throws Exception {
        doMapTestWithoutExpansion("t");
    }

    /**
     * @tpTestDetails Test on jaxb object annotated with @XmlRootElement which placed in xml string within <map></map>
     *                tags. Resource binds such xml correctly into a map
     *                "resteasy.document.secure.processing.feature" is set to "false"
     *                "resteasy.document.expand.entity.references" is set to "false"
     * @tpPassCrit Passwd file should not be returned by the response.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @OperateOnDeployment("ff")
    public void testMapWithoutExpansionFalse() throws Exception {
        doMapTestWithoutExpansion("ff");
    }

    /**
     * @tpTestDetails Test on jaxb object annotated with @XmlRootElement which placed in xml string within <map></map>
     *                tags. Resource binds such xml correctly into a map
     *                "resteasy.document.secure.processing.feature" is set to "true"
     *                "resteasy.document.expand.entity.references" is set to "false"
     * @tpPassCrit Passwd file should not be returned by the response.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @OperateOnDeployment("ft")
    public void testMapWithoutExpansionTrue() throws Exception {
        doMapTestWithoutExpansion("ft");
    }

    /**
     * @tpTestDetails Test on jaxb object annotated with @XmlRootElement which placed in xml string within <map></map>
     *                tags. Resource binds such xml correctly into a map
     *                "resteasy.document.secure.processing.feature" is set to "false"
     *                "resteasy.document.expand.entity.references" is set to "true"
     * @tpPassCrit Passwd file should be returned by the response.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @OperateOnDeployment("tf")
    public void testMapWithExpansionFalse() throws Exception {
        doMapTestWithExpansion("tf");
    }

    /**
     * @tpTestDetails Test on jaxb object annotated with @XmlRootElement which placed in xml string within <map></map>
     *                tags. Resource binds such xml correctly into a map
     *                "resteasy.document.secure.processing.feature" is set to "true"
     *                "resteasy.document.expand.entity.references" is set to "true"
     * @tpPassCrit Passwd file should be returned by the response.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @OperateOnDeployment("tt")
    public void testMapWithExpansionTrue() throws Exception {
        doMapTestWithExpansion("tt");
    }

    void doTestXmlRootElementDefault(String ext) throws Exception {
        WebTarget target = client.target(PortProviderUtil.generateURL("/xmlRootElement", URL_PREFIX + ext));
        logger.info(strMovieRootElement);
        Response response = target.request().post(Entity.entity(strMovieRootElement, "application/xml"));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String entity = response.readEntity(String.class);
        logger.info("Result: " + entity);
        Assertions.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") < 0,
                "The entity was expanded and it shouldn't be");
    }

    void doTestXmlRootElementWithoutExpansion(String ext) throws Exception {
        WebTarget target = client.target(PortProviderUtil.generateURL("/xmlRootElement", URL_PREFIX + ext));
        logger.info(strMovieRootElement);
        Response response = target.request().post(Entity.entity(strMovieRootElement, "application/xml"));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String entity = response.readEntity(String.class);
        logger.info("Result: " + entity);
        Assertions.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") < 0,
                "The entity was expanded and it shouldn't be");
    }

    void doTestXmlRootElementWithExpansion(String ext) throws Exception {
        WebTarget target = client.target(PortProviderUtil.generateURL("/xmlRootElement", URL_PREFIX + ext));
        logger.info(strMovieRootElement);
        Response response = target.request().post(Entity.entity(strMovieRootElement, "application/xml"));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String entity = response.readEntity(String.class);
        logger.info("result: " + entity);
        Assertions.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") >= 0,
                "The entity wasn't expanded and it should be");
    }

    void doTestXmlTypeDefault(String ext) throws Exception {
        WebTarget target = client.target(PortProviderUtil.generateURL("/xmlType", URL_PREFIX + ext));
        logger.info(strMovie);
        Response response = target.request().post(Entity.entity(strMovie, "application/xml"));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String entity = response.readEntity(String.class);
        logger.info("Result: " + entity);
        Assertions.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") < 0,
                "The entity was expanded and it shouldn't be");
    }

    void doTestXmlTypeWithoutExpansion(String ext) throws Exception {
        WebTarget target = client.target(PortProviderUtil.generateURL("/xmlType", URL_PREFIX + ext));
        logger.info(strMovie);
        Response response = target.request().post(Entity.entity(strMovie, "application/xml"));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String entity = response.readEntity(String.class);
        logger.info("Result: " + entity);
        Assertions.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") < 0,
                "The entity was expanded and it shouldn't be");
    }

    void doTestXmlTypeWithExpansion(String ext) throws Exception {
        WebTarget target = client.target(PortProviderUtil.generateURL("/xmlType", URL_PREFIX + ext));
        logger.info(strMovie);
        Response response = target.request().post(Entity.entity(strMovie, "application/xml"));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String entity = response.readEntity(String.class);
        logger.info("result: " + entity);
        Assertions.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") >= 0,
                "The entity wasn't expanded and it should be");
    }

    void doTestJAXBElementDefault(String ext) throws Exception {
        WebTarget target = client.target(PortProviderUtil.generateURL("/JAXBElement", URL_PREFIX + ext));
        logger.info(strMovieXmlType);
        Response response = target.request().post(Entity.entity(strMovieXmlType, "application/xml"));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String entity = response.readEntity(String.class);
        logger.info("Result: " + entity);
        Assertions.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") < 0,
                "The entity was expanded and it shouldn't be");
    }

    void doTestJAXBElementWithoutExpansion(String ext) throws Exception {
        WebTarget target = client.target(PortProviderUtil.generateURL("/JAXBElement", URL_PREFIX + ext));
        logger.info(strMovieXmlType);
        Response response = target.request().post(Entity.entity(strMovieXmlType, "application/xml"));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String entity = response.readEntity(String.class);
        logger.info("Result: " + entity);
        Assertions.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") < 0,
                "The entity was expanded and it shouldn't be");
    }

    void doTestJAXBElementWithExpansion(String ext) throws Exception {
        WebTarget target = client.target(PortProviderUtil.generateURL("/JAXBElement", URL_PREFIX + ext));
        logger.info(strMovieXmlType);
        Response response = target.request().post(Entity.entity(strMovieXmlType, "application/xml"));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String entity = response.readEntity(String.class);
        logger.info("Result: " + entity);
        Assertions.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") >= 0,
                "The entity wasn't expanded and it should be");
    }

    void doCollectionTestWithoutExpansion(String ext, String path) throws Exception {
        WebTarget target = client.target(PortProviderUtil.generateURL("/" + path, URL_PREFIX + ext));
        logger.info(strMovieRootElementCollection);
        Response response = target.request().post(Entity.entity(strMovieRootElementCollection, "application/xml"));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String entity = response.readEntity(String.class);
        logger.info("Result: " + entity);
        Assertions.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") < 0,
                "The entity was expanded and it shouldn't be");
    }

    void doCollectionTestWithExpansion(String ext, String path) throws Exception {
        WebTarget target = client.target(PortProviderUtil.generateURL("/" + path, URL_PREFIX + ext));
        logger.info(strMovieRootElementCollection);
        Response response = target.request().post(Entity.entity(strMovieRootElementCollection, "application/xml"));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String entity = response.readEntity(String.class);
        logger.info("Result: " + entity);
        Assertions.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") >= 0,
                "The entity wasn't expanded and it should be");
    }

    void doMapTestWithoutExpansion(String ext) throws Exception {
        WebTarget target = client.target(PortProviderUtil.generateURL("/map", URL_PREFIX + ext));
        logger.info(strMovieRootElementMap);
        Response response = target.request().post(Entity.entity(strMovieRootElementMap, "application/xml"));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String entity = response.readEntity(String.class);
        logger.info("Result: " + entity);
        Assertions.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") < 0,
                "The entity was expanded and it shouldn't be");
    }

    void doMapTestWithExpansion(String ext) throws Exception {
        WebTarget target = client.target(PortProviderUtil.generateURL("/map", URL_PREFIX + ext));
        logger.info(strMovieRootElementMap);
        Response response = target.request().post(Entity.entity(strMovieRootElementMap, "application/xml"));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String entity = response.readEntity(String.class);
        logger.info("Result: " + entity);
        Assertions.assertTrue(entity.indexOf("xx:xx:xx:xx:xx:xx:xx") >= 0,
                "The entity wasn't expanded and it should be");
    }
}
