package org.jboss.resteasy.test.validation;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;
import org.jboss.resteasy.api.validation.ViolationReport;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.validation.resource.ValidationXMLClassConstraint;
import org.jboss.resteasy.test.validation.resource.ValidationXMLClassValidator;
import org.jboss.resteasy.test.validation.resource.ValidationXMLFoo;
import org.jboss.resteasy.test.validation.resource.ValidationXMLFooConstraint;
import org.jboss.resteasy.test.validation.resource.ValidationXMLFooReaderWriter;
import org.jboss.resteasy.test.validation.resource.ValidationXMLFooValidator;
import org.jboss.resteasy.test.validation.resource.ValidationXMLResourceWithAllFivePotentialViolations;
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
 * @tpSubChapter Validation
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test of xml validation
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ValidationXMLTest {
    static final String WRONG_ERROR_MSG = "Expected validation error is not in response";
    protected static final Logger logger = Logger.getLogger(ValidationXMLTest.class.getName());
    ResteasyClient client;

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(ValidationXMLTest.class.getSimpleName())
                .addClasses(ValidationXMLFoo.class, ValidationXMLFooValidator.class, ValidationXMLFooConstraint.class,
                        ValidationXMLClassValidator.class, ValidationXMLClassConstraint.class);
        Map<String, String> contextParams = new HashMap<>();
        contextParams.put(ResteasyContextParameters.RESTEASY_PREFER_JACKSON_OVER_JSONB, "true");
        return TestUtil.finishContainerPrepare(war, contextParams, ValidationXMLFooReaderWriter.class,
                ValidationXMLResourceWithAllFivePotentialViolations.class);
    }

    @BeforeEach
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient().register(ValidationXMLFooReaderWriter.class);
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
    }

    private static String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ValidationXMLTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Test standard XML
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testStandardXML() throws Exception {
        doTestXML(MediaType.APPLICATION_XML);
    }

    /**
     * @tpTestDetails Test standard JSON
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testStandardJSON() throws Exception {
        doTestJSON(MediaType.APPLICATION_JSON);
    }

    /**
     * @tpTestDetails Test standard plain text
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testText() throws Exception {
        doTestText(MediaType.TEXT_PLAIN);
    }

    /**
     * @tpTestDetails Check wildcard
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testWildcard() throws Exception {
        doTestText(MediaType.WILDCARD);
    }

    /**
     * @tpTestDetails Check XML and JSON. JSON use q=.5
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void tesXML_NoQ_JSON() throws Exception {
        doTestXML(MediaType.APPLICATION_XML + "," + MediaType.APPLICATION_JSON + ";q=.5");
    }

    /**
     * @tpTestDetails Check XML and JSON. JSON use q=.5
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void tesXML_JSON_NoQ() throws Exception {
        doTestJSON(MediaType.APPLICATION_XML + ";q=.5" + "," + MediaType.APPLICATION_JSON);
    }

    /**
     * @tpTestDetails Check XML and JSON. JSON use q=.5. XML use q=1
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testXML_GT_JSON() throws Exception {
        doTestXML(MediaType.APPLICATION_XML + ";q=1," + MediaType.APPLICATION_JSON + ";q=.5");
    }

    /**
     * @tpTestDetails Check XML and JSON. JSON use q=1. XML use q=.5
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testXML_LT_JSON() throws Exception {
        doTestJSON(MediaType.APPLICATION_XML + ";q=.5," + MediaType.APPLICATION_JSON + ";q=1");
    }

    protected void doTestXML(String mediaType) throws Exception {
        doTestXML_pre(mediaType);
        doTestXML_post(mediaType);
    }

    protected void doTestJSON(String mediaType) throws Exception {
        doTestJSON_pre(mediaType);
        doTestJSON_post(mediaType);
    }

    protected void doTestText(String mediaType) throws Exception {
        doTestText_pre(mediaType);
        doTestText_post(mediaType);
    }

    protected void doTestXML_pre(String mediaType) throws Exception {
        {
            // Text form
            ValidationXMLFoo foo = new ValidationXMLFoo("p");
            Response response = client.target(generateURL("/a/b/c")).request().accept(mediaType)
                    .post(Entity.entity(foo, "application/foo"));
            Assertions.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
            String entity = response.readEntity(String.class);
            logger.info("report: " + entity);
            String start = "<violationReport>";
            String propertyViolation1 = "<propertyViolations><constraintType>PROPERTY</constraintType><path>s</path><message>size must be between 2 and 4</message><value>a</value></propertyViolations>";
            String propertyViolation2 = "<propertyViolations><constraintType>PROPERTY</constraintType><path>t</path><message>size must be between 2 and 4</message><value>b</value></propertyViolations>";
            String propertyViolation3 = "<propertyViolations><constraintType>PROPERTY</constraintType><path>u</path><message>size must be between 3 and 5</message><value>c</value></propertyViolations>";
            String classViolationStart = "<classViolations><constraintType>CLASS</constraintType><path></path><message>Concatenation of s and u must have length &gt; 5</message><value>org.jboss.resteasy.test.validation.resource.ValidationXMLResourceWithAllFivePotentialViolations";
            String classViolationEnd = "</value></classViolations>";
            String parameterViolationP1 = "<parameterViolations><constraintType>PARAMETER</constraintType><path>post.";
            String parameterViolationP2 = "</path><message>s must have length: 3 &lt;= length &lt;= 5</message><value>ValidationXMLFoo[p]</value></parameterViolations>";
            String end = "</violationReport>";
            Assertions.assertTrue(entity.contains(start), WRONG_ERROR_MSG);
            Assertions.assertTrue(entity.contains(propertyViolation1), WRONG_ERROR_MSG);
            Assertions.assertTrue(entity.contains(propertyViolation2), WRONG_ERROR_MSG);
            Assertions.assertTrue(entity.contains(propertyViolation3), WRONG_ERROR_MSG);
            Assertions.assertTrue(entity.contains(classViolationStart), WRONG_ERROR_MSG);
            Assertions.assertTrue(entity.contains(classViolationEnd), WRONG_ERROR_MSG);
            Assertions.assertTrue(entity.contains(parameterViolationP1), WRONG_ERROR_MSG);
            Assertions.assertTrue(entity.contains(parameterViolationP2), WRONG_ERROR_MSG);
            Assertions.assertTrue(entity.contains(end), WRONG_ERROR_MSG);
            response.close();
        }

        {
            // Unmarshal report,
            ValidationXMLFoo foo = new ValidationXMLFoo("p");
            Response response = client.target(generateURL("/a/b/c")).request().accept(MediaType.APPLICATION_XML)
                    .post(Entity.entity(foo, "application/foo"));
            Assertions.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
            ViolationReport report = response.readEntity(ViolationReport.class);
            logger.info("report: " + report);
            TestUtil.countViolations(report, 3, 1, 1, 0);
            ResteasyConstraintViolation cv = TestUtil.getViolationByMessageAndValue(report.getPropertyViolations(),
                    "size must be between 2 and 4", "a");
            Assertions.assertNotNull(cv, WRONG_ERROR_MSG);
            cv = TestUtil.getViolationByMessageAndValue(report.getPropertyViolations(), "size must be between 2 and 4", "b");
            Assertions.assertNotNull(cv, WRONG_ERROR_MSG);
            cv = TestUtil.getViolationByMessage(report.getPropertyViolations(), "size must be between 3 and 5");
            Assertions.assertNotNull(cv, WRONG_ERROR_MSG);
            Assertions.assertEquals("c", cv.getValue(), WRONG_ERROR_MSG);
            cv = report.getClassViolations().iterator().next();
            Assertions.assertEquals("Concatenation of s and u must have length > 5", cv.getMessage(),
                    WRONG_ERROR_MSG);
            logger.info("value: " + cv.getValue());
            Assertions.assertTrue(cv.getValue().startsWith(
                    "org.jboss.resteasy.test.validation.resource.ValidationXMLResourceWithAllFivePotentialViolations@"));
            cv = report.getParameterViolations().iterator().next();
            Assertions.assertEquals("s must have length: 3 <= length <= 5", cv.getMessage(),
                    WRONG_ERROR_MSG);
            Assertions.assertEquals("ValidationXMLFoo[p]", cv.getValue(), WRONG_ERROR_MSG);
            response.close();
        }
    }

    protected void doTestXML_post(String mediaType) throws Exception {
        {
            // Text form
            ValidationXMLFoo foo = new ValidationXMLFoo("123");
            Response response = client.target(generateURL("/abc/pqr/xyz")).request().accept(mediaType)
                    .post(Entity.entity(foo, "application/foo"));
            Assertions.assertEquals(HttpResponseCodes.SC_INTERNAL_SERVER_ERROR, response.getStatus());
            String entity = response.readEntity(String.class);
            logger.info("report: " + entity);
            String expected = "<violationReport><returnValueViolations><constraintType>RETURN_VALUE</constraintType><path>post.&lt;return value&gt;</path><message>s must have length: 4 &lt;= length &lt;= 5</message><value>ValidationXMLFoo[123]</value></returnValueViolations></violationReport>";
            Assertions.assertTrue(entity.contains(expected), WRONG_ERROR_MSG);
            response.close();
        }

        {
            // Unmarshal report,
            ValidationXMLFoo foo = new ValidationXMLFoo("123");
            Response response = client.target(generateURL("/abc/pqr/xyz")).request().accept(MediaType.APPLICATION_XML)
                    .post(Entity.entity(foo, "application/foo"));
            Assertions.assertEquals(HttpResponseCodes.SC_INTERNAL_SERVER_ERROR, response.getStatus());
            ViolationReport report = response.readEntity(ViolationReport.class);
            logger.info("report: " + report);
            TestUtil.countViolations(report, 0, 0, 0, 1);
            ResteasyConstraintViolation cv = report.getReturnValueViolations().iterator().next();
            Assertions.assertEquals("s must have length: 4 <= length <= 5", cv.getMessage(),
                    WRONG_ERROR_MSG);
            Assertions.assertEquals(foo.toString(), cv.getValue(), WRONG_ERROR_MSG);
            response.close();
        }
    }

    protected void doTestJSON_pre(String mediaType) throws Exception {
        {
            // Text form
            ValidationXMLFoo foo = new ValidationXMLFoo("p");
            Response response = client.target(generateURL("/a/b/c")).request().accept(mediaType)
                    .post(Entity.entity(foo, "application/foo"));
            Assertions.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
            ViolationReport entity = response.readEntity(ViolationReport.class);
            logger.info("report: " + entity);
            final List<ResteasyConstraintViolation> propertyViolations = entity.getPropertyViolations();
            Assertions.assertTrue(hasItems(resolveValues(propertyViolations, ResteasyConstraintViolation::getPath),
                    "s", "t", "u"), WRONG_ERROR_MSG);
            Assertions.assertTrue(hasItems(resolveValues(propertyViolations, ResteasyConstraintViolation::getValue),
                    "a", "b", "c"), WRONG_ERROR_MSG);
            Assertions.assertTrue(hasItems(resolveValues(propertyViolations, ResteasyConstraintViolation::getMessage),
                    "size must be between 2 and 4", "size must be between 2 and 4",
                    "size must be between 3 and 5"), WRONG_ERROR_MSG);

            final List<ResteasyConstraintViolation> classViolations = entity.getClassViolations();
            Assertions.assertTrue(hasItems(resolveValues(classViolations, ResteasyConstraintViolation::getPath), ""),
                    WRONG_ERROR_MSG);
            Assertions.assertTrue(hasItems(resolveValues(classViolations, ResteasyConstraintViolation::getMessage),
                    "Concatenation of s and u must have length > 5"),
                    WRONG_ERROR_MSG);

            final List<ResteasyConstraintViolation> parameterViolations = entity.getParameterViolations();
            Assertions.assertEquals(parameterViolations.size(), 1, WRONG_ERROR_MSG);
            Assertions.assertTrue(parameterViolations.get(0).getPath().startsWith("post."),
                    WRONG_ERROR_MSG);
            Assertions.assertTrue(hasItems(resolveValues(parameterViolations, ResteasyConstraintViolation::getMessage),
                    "s must have length: 3 <= length <= 5"), WRONG_ERROR_MSG);
            Assertions.assertTrue(hasItems(resolveValues(parameterViolations, ResteasyConstraintViolation::getValue),
                    "ValidationXMLFoo[p]"), WRONG_ERROR_MSG);
            Assertions.assertEquals(entity.getReturnValueViolations().size(), 0,
                    WRONG_ERROR_MSG);
            response.close();
        }

        {
            // Unmarshal report,
            ValidationXMLFoo foo = new ValidationXMLFoo("p");
            Response response = client.target(generateURL("/a/b/c")).request().accept(MediaType.APPLICATION_XML)
                    .post(Entity.entity(foo, "application/foo"));
            Assertions.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
            ViolationReport report = response.readEntity(ViolationReport.class);
            logger.info("report: " + report);
            TestUtil.countViolations(report, 3, 1, 1, 0);
            ResteasyConstraintViolation cv = TestUtil.getViolationByMessageAndValue(report.getPropertyViolations(),
                    "size must be between 2 and 4", "a");
            Assertions.assertNotNull(cv, WRONG_ERROR_MSG);
            cv = TestUtil.getViolationByMessageAndValue(report.getPropertyViolations(), "size must be between 2 and 4", "b");
            Assertions.assertNotNull(cv, WRONG_ERROR_MSG);
            cv = TestUtil.getViolationByMessage(report.getPropertyViolations(), "size must be between 3 and 5");
            Assertions.assertNotNull(cv, WRONG_ERROR_MSG);
            Assertions.assertEquals("c", cv.getValue(), WRONG_ERROR_MSG);
            cv = report.getClassViolations().iterator().next();
            Assertions.assertEquals("Concatenation of s and u must have length > 5", cv.getMessage(),
                    WRONG_ERROR_MSG);
            logger.info("value: " + cv.getValue());
            Assertions.assertTrue(cv.getValue().startsWith(
                    "org.jboss.resteasy.test.validation.resource.ValidationXMLResourceWithAllFivePotentialViolations@"),
                    WRONG_ERROR_MSG);
            cv = report.getParameterViolations().iterator().next();
            Assertions.assertEquals("s must have length: 3 <= length <= 5", cv.getMessage(), WRONG_ERROR_MSG);
            Assertions.assertEquals("ValidationXMLFoo[p]", cv.getValue(), WRONG_ERROR_MSG);
            response.close();
        }
    }

    protected void doTestJSON_post(String mediaType) throws Exception {
        {
            // Text form
            ValidationXMLFoo foo = new ValidationXMLFoo("123");
            Response response = client.target(generateURL("/abc/pqr/xyz")).request().accept(mediaType)
                    .post(Entity.entity(foo, "application/foo"));
            Assertions.assertEquals(HttpResponseCodes.SC_INTERNAL_SERVER_ERROR, response.getStatus());
            JsonObject entity = response.readEntity(JsonObject.class);
            logger.info("report: " + entity);
            Assertions.assertTrue(entity.getJsonArray("propertyViolations").isEmpty(),
                    WRONG_ERROR_MSG);
            Assertions.assertTrue(entity.getJsonArray("classViolations").isEmpty(),
                    WRONG_ERROR_MSG);
            Assertions.assertTrue(entity.getJsonArray("parameterViolations").isEmpty(),
                    WRONG_ERROR_MSG);

            final JsonArray returnValueViolations = entity.getJsonArray("returnValueViolations");
            Assertions.assertNotNull(returnValueViolations, "Did not find returnValueViolations entry");
            Assertions.assertFalse(returnValueViolations.isEmpty(), "Did not find returnValueViolations entry");
            final JsonObject returnValueViolation = returnValueViolations.getJsonObject(0);
            Assertions.assertEquals("RETURN_VALUE", returnValueViolation.getString("constraintType"),
                    WRONG_ERROR_MSG);
            Assertions.assertEquals("post.<return value>", returnValueViolation.getString("path"),
                    WRONG_ERROR_MSG);
            Assertions.assertEquals("s must have length: 4 <= length <= 5",
                    returnValueViolation.getString("message"), WRONG_ERROR_MSG);
            Assertions.assertEquals("ValidationXMLFoo[123]", returnValueViolation.getString("value"),
                    WRONG_ERROR_MSG);
            response.close();
        }

        {
            // Unmarshal report,
            ValidationXMLFoo foo = new ValidationXMLFoo("123");
            Response response = client.target(generateURL("/abc/pqr/xyz")).request().accept(MediaType.APPLICATION_XML)
                    .post(Entity.entity(foo, "application/foo"));
            Assertions.assertEquals(HttpResponseCodes.SC_INTERNAL_SERVER_ERROR, response.getStatus());
            ViolationReport report = response.readEntity(ViolationReport.class);
            logger.info("report: " + report);
            TestUtil.countViolations(report, 0, 0, 0, 1);
            ResteasyConstraintViolation cv = report.getReturnValueViolations().iterator().next();
            Assertions.assertEquals("s must have length: 4 <= length <= 5", cv.getMessage(),
                    WRONG_ERROR_MSG);
            Assertions.assertEquals(foo.toString(), cv.getValue(), WRONG_ERROR_MSG);
            response.close();
        }
    }

    protected void doTestText_pre(String mediaType) throws Exception {
        ValidationXMLFoo foo = new ValidationXMLFoo("p");
        Response response = client.target(generateURL("/a/b/c")).request().accept(mediaType)
                .post(Entity.entity(foo, "application/foo"));
        Assertions.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
        String entity = response.readEntity(String.class);
        logger.info("report:");
        logger.info(entity);
        String propertyViolation1 = "[PROPERTY]\r" +
                "[s]\r" +
                "[size must be between 2 and 4]\r" +
                "[a]\r";
        String propertyViolation2 = "[PROPERTY]\r" +
                "[t]\r" +
                "[size must be between 2 and 4]\r" +
                "[b]\r";
        String propertyViolation3 = "[PROPERTY]\r" +
                "[u]\r" +
                "[size must be between 3 and 5]\r" +
                "[c]\r";
        String classViolation = "[CLASS]\r" +
                "[]\r" +
                "[Concatenation of s and u must have length > 5]\r" +
                "[org.jboss.resteasy.test.validation.resource.ValidationXMLResourceWithAllFivePotentialViolations";
        String parameterViolationP1 = "[PARAMETER]\r" +
                "[post.";
        String parameterViolationP2 = "]\r" +
                "[s must have length: 3 <= length <= 5]\r" +
                "[ValidationXMLFoo[p]]";
        Assertions.assertTrue(entity.contains(propertyViolation1), WRONG_ERROR_MSG);
        Assertions.assertTrue(entity.contains(propertyViolation2), WRONG_ERROR_MSG);
        Assertions.assertTrue(entity.contains(propertyViolation3), WRONG_ERROR_MSG);
        Assertions.assertTrue(entity.contains(classViolation), WRONG_ERROR_MSG);
        Assertions.assertTrue(entity.contains(parameterViolationP1), WRONG_ERROR_MSG);
        Assertions.assertTrue(entity.contains(parameterViolationP2), WRONG_ERROR_MSG);
        response.close();
    }

    protected void doTestText_post(String mediaType) throws Exception {
        ValidationXMLFoo foo = new ValidationXMLFoo("123");
        Response response = client.target(generateURL("/abc/pqr/xyz")).request().accept(mediaType)
                .post(Entity.entity(foo, "application/foo"));
        Assertions.assertEquals(HttpResponseCodes.SC_INTERNAL_SERVER_ERROR, response.getStatus());
        String entity = response.readEntity(String.class);
        logger.info("report: " + entity);
        String returnValueViolation = "[RETURN_VALUE]\r" +
                "[post.<return value>]\r" +
                "[s must have length: 4 <= length <= 5]\r" +
                "[ValidationXMLFoo[123]]\r\r";
        Assertions.assertTrue(entity.equals(returnValueViolation), WRONG_ERROR_MSG);
        response.close();
    }

    private static <T> Collection<String> resolveValues(final Collection<T> c, final Function<T, String> mapper) {
        return c.stream()
                .map(mapper)
                .collect(Collectors.toList());
    }

    private static boolean hasItems(Collection<String> items, String... controlList) {
        return items.containsAll(List.of(controlList));
    }
}
