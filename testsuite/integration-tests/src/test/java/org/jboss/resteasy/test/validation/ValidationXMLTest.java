package org.jboss.resteasy.test.validation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;
import org.jboss.resteasy.api.validation.ViolationReport;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.validation.resource.ValidationXMLClassConstraint;
import org.jboss.resteasy.test.validation.resource.ValidationXMLClassValidator;
import org.jboss.resteasy.test.validation.resource.ValidationXMLFoo;
import org.jboss.resteasy.test.validation.resource.ValidationXMLFooConstraint;
import org.jboss.resteasy.test.validation.resource.ValidationXMLFooReaderWriter;
import org.jboss.resteasy.test.validation.resource.ValidationXMLFooValidator;
import org.jboss.resteasy.test.validation.resource.ValidationXMLResourceWithAllFivePotentialViolations;
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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Iterator;


/**
 * @tpSubChapter Validation
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test of xml validation
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ValidationXMLTest {
    static final String WRONG_ERROR_MSG = "Expected validation error is not in response";
    protected static final Logger logger = LogManager.getLogger(ValidationXMLTest.class.getName());
    ResteasyClient client;

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(ValidationXMLTest.class.getSimpleName())
                .addClasses(ValidationXMLFoo.class, ValidationXMLFooValidator.class, ValidationXMLFooConstraint.class,
                        ValidationXMLClassValidator.class, ValidationXMLClassConstraint.class);
        return TestUtil.finishContainerPrepare(war, null, ValidationXMLFooReaderWriter.class, ValidationXMLResourceWithAllFivePotentialViolations.class);
    }

    @Before
    public void init() {
        client = new ResteasyClientBuilder().build().register(ValidationXMLFooReaderWriter.class);
    }

    @After
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
            Response response = client.target(generateURL("/a/b/c")).request().accept(mediaType).post(Entity.entity(foo, "application/foo"));
            Assert.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
            String entity = response.readEntity(String.class);
            logger.info("report: " + entity);
            String start = "<violationReport>";
            String fieldViolation1 = "<fieldViolations><constraintType>FIELD</constraintType><path>s</path><message>size must be between 2 and 4</message><value>a</value></fieldViolations>";
            String fieldViolation2 = "<fieldViolations><constraintType>FIELD</constraintType><path>t</path><message>size must be between 2 and 4</message><value>b</value></fieldViolations>";
            String propertyViolation = "<propertyViolations><constraintType>PROPERTY</constraintType><path>u</path><message>size must be between 3 and 5</message><value>c</value></propertyViolations>";
            String classViolationStart = "<classViolations><constraintType>CLASS</constraintType><path></path><message>Concatenation of s and u must have length &gt; 5</message><value>org.jboss.resteasy.test.validation.resource.ValidationXMLResourceWithAllFivePotentialViolations";
            String classViolationEnd = "</value></classViolations>";
            String parameterViolationP1 = "<parameterViolations><constraintType>PARAMETER</constraintType><path>post.";
            String parameterViolationP2 = "</path><message>s must have length: 3 &lt;= length &lt;= 5</message><value>ValidationXMLFoo[p]</value></parameterViolations>";
            String end = "</violationReport>";
            Assert.assertTrue(WRONG_ERROR_MSG, entity.contains(start));
            Assert.assertTrue(WRONG_ERROR_MSG, entity.contains(fieldViolation1));
            Assert.assertTrue(WRONG_ERROR_MSG, entity.contains(fieldViolation2));
            Assert.assertTrue(WRONG_ERROR_MSG, entity.contains(propertyViolation));
            Assert.assertTrue(WRONG_ERROR_MSG, entity.contains(classViolationStart));
            Assert.assertTrue(WRONG_ERROR_MSG, entity.contains(classViolationEnd));
            Assert.assertTrue(WRONG_ERROR_MSG, entity.contains(parameterViolationP1));
            Assert.assertTrue(WRONG_ERROR_MSG, entity.contains(parameterViolationP2));
            Assert.assertTrue(WRONG_ERROR_MSG, entity.contains(end));
            response.close();
        }

        {
            // Unmarshal report,
            ValidationXMLFoo foo = new ValidationXMLFoo("p");
            Response response = client.target(generateURL("/a/b/c")).request().accept(MediaType.APPLICATION_XML).post(Entity.entity(foo, "application/foo"));
            Assert.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
            ViolationReport report = response.readEntity(ViolationReport.class);
            logger.info("report: " + report);
            TestUtil.countViolations(report, 2, 1, 1, 1, 0);
            Iterator<ResteasyConstraintViolation> iterator = report.getFieldViolations().iterator();
            ResteasyConstraintViolation cv1 = iterator.next();
            ResteasyConstraintViolation cv2 = iterator.next();
            if (!("a").equals(cv1.getValue())) {
                ResteasyConstraintViolation tmp = cv1;
                cv1 = cv2;
                cv2 = tmp;
            }
            Assert.assertEquals(WRONG_ERROR_MSG, "size must be between 2 and 4", cv1.getMessage());
            Assert.assertEquals(WRONG_ERROR_MSG, "a", cv1.getValue());
            Assert.assertEquals(WRONG_ERROR_MSG, "size must be between 2 and 4", cv2.getMessage());
            Assert.assertEquals(WRONG_ERROR_MSG, "b", cv2.getValue());
            ResteasyConstraintViolation cv = report.getPropertyViolations().iterator().next();
            Assert.assertEquals(WRONG_ERROR_MSG, "size must be between 3 and 5", cv.getMessage());
            Assert.assertEquals(WRONG_ERROR_MSG, "c", cv.getValue());
            cv = report.getClassViolations().iterator().next();
            Assert.assertEquals(WRONG_ERROR_MSG, "Concatenation of s and u must have length > 5", cv.getMessage());
            logger.info("value: " + cv.getValue());
            Assert.assertTrue(cv.getValue().startsWith("org.jboss.resteasy.test.validation.resource.ValidationXMLResourceWithAllFivePotentialViolations@"));
            cv = report.getParameterViolations().iterator().next();
            Assert.assertEquals(WRONG_ERROR_MSG, "s must have length: 3 <= length <= 5", cv.getMessage());
            Assert.assertEquals(WRONG_ERROR_MSG, "ValidationXMLFoo[p]", cv.getValue());
            response.close();
        }
    }

    protected void doTestXML_post(String mediaType) throws Exception {
        {
            // Text form
            ValidationXMLFoo foo = new ValidationXMLFoo("123");
            Response response = client.target(generateURL("/abc/pqr/xyz")).request().accept(mediaType).post(Entity.entity(foo, "application/foo"));
            Assert.assertEquals(HttpResponseCodes.SC_INTERNAL_SERVER_ERROR, response.getStatus());
            String entity = response.readEntity(String.class);
            logger.info("report: " + entity);
            String expected = "<violationReport><returnValueViolations><constraintType>RETURN_VALUE</constraintType><path>post.&lt;return value&gt;</path><message>s must have length: 4 &lt;= length &lt;= 5</message><value>ValidationXMLFoo[123]</value></returnValueViolations></violationReport>";
            Assert.assertTrue(WRONG_ERROR_MSG, entity.contains(expected));
            response.close();
        }

        {
            // Unmarshal report,
            ValidationXMLFoo foo = new ValidationXMLFoo("123");
            Response response = client.target(generateURL("/abc/pqr/xyz")).request().accept(MediaType.APPLICATION_XML).post(Entity.entity(foo, "application/foo"));
            Assert.assertEquals(HttpResponseCodes.SC_INTERNAL_SERVER_ERROR, response.getStatus());
            ViolationReport report = response.readEntity(ViolationReport.class);
            logger.info("report: " + report);
            TestUtil.countViolations(report, 0, 0, 0, 0, 1);
            ResteasyConstraintViolation cv = report.getReturnValueViolations().iterator().next();
            Assert.assertEquals(WRONG_ERROR_MSG, "s must have length: 4 <= length <= 5", cv.getMessage());
            Assert.assertEquals(WRONG_ERROR_MSG, foo.toString(), cv.getValue());
            response.close();
        }
    }

    protected void doTestJSON_pre(String mediaType) throws Exception {
        {
            // Text form
            ValidationXMLFoo foo = new ValidationXMLFoo("p");
            Response response = client.target(generateURL("/a/b/c")).request().accept(mediaType).post(Entity.entity(foo, "application/foo"));
            Assert.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
            String entity = response.readEntity(String.class);
            logger.info("report: " + entity);
            String start = "{\"exception\":null,\"fieldViolations\":[";
            String fieldViolation1 = "{\"constraintType\":\"FIELD\",\"path\":\"s\",\"message\":\"size must be between 2 and 4\",\"value\":\"a\"}";
            String fieldViolation2 = "{\"constraintType\":\"FIELD\",\"path\":\"t\",\"message\":\"size must be between 2 and 4\",\"value\":\"b\"}";
            String propertyViolation = "\"propertyViolations\":[{\"constraintType\":\"PROPERTY\",\"path\":\"u\",\"message\":\"size must be between 3 and 5\",\"value\":\"c\"}]";
            String classViolationStart = "\"classViolations\":[{\"constraintType\":\"CLASS\",\"path\":\"\",\"message\":\"Concatenation of s and u must have length > 5\",\"value\":\"org.jboss.resteasy.test.validation.resource.ValidationXMLResourceWithAllFivePotentialViolations@";
            String classViolationEnd = "}]";
            String parameterViolationP1 = "\"parameterViolations\":[{\"constraintType\":\"PARAMETER\",\"path\":\"post.";
            String parameterViolationP2 = "\",\"message\":\"s must have length: 3 <= length <= 5\",\"value\":\"ValidationXMLFoo[p]\"}]";
            String returnValueViolation = "\"returnValueViolations\":[]";
            String end = "}";
            Assert.assertTrue(WRONG_ERROR_MSG, entity.contains(start));
            Assert.assertTrue(WRONG_ERROR_MSG, entity.contains(fieldViolation1));
            Assert.assertTrue(WRONG_ERROR_MSG, entity.contains(fieldViolation2));
            Assert.assertTrue(WRONG_ERROR_MSG, entity.contains(propertyViolation));
            Assert.assertTrue(WRONG_ERROR_MSG, entity.contains(classViolationStart));
            Assert.assertTrue(WRONG_ERROR_MSG, entity.contains(classViolationEnd));
            Assert.assertTrue(WRONG_ERROR_MSG, entity.contains(parameterViolationP1));
            Assert.assertTrue(WRONG_ERROR_MSG, entity.contains(parameterViolationP2));
            Assert.assertTrue(WRONG_ERROR_MSG, entity.contains(returnValueViolation));
            Assert.assertTrue(WRONG_ERROR_MSG, entity.contains(end));
            response.close();
        }

        {
            // Unmarshal report,
            ValidationXMLFoo foo = new ValidationXMLFoo("p");
            Response response = client.target(generateURL("/a/b/c")).request().accept(MediaType.APPLICATION_XML).post(Entity.entity(foo, "application/foo"));
            Assert.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
            ViolationReport report = response.readEntity(ViolationReport.class);
            logger.info("report: " + report);
            TestUtil.countViolations(report, 2, 1, 1, 1, 0);
            Iterator<ResteasyConstraintViolation> iterator = report.getFieldViolations().iterator();
            ResteasyConstraintViolation cv1 = iterator.next();
            ResteasyConstraintViolation cv2 = iterator.next();
            if (!("a").equals(cv1.getValue())) {
                ResteasyConstraintViolation tmp = cv1;
                cv1 = cv2;
                cv2 = tmp;
            }
            Assert.assertEquals(WRONG_ERROR_MSG, "size must be between 2 and 4", cv1.getMessage());
            Assert.assertEquals(WRONG_ERROR_MSG, "a", cv1.getValue());
            Assert.assertEquals(WRONG_ERROR_MSG, "size must be between 2 and 4", cv2.getMessage());
            Assert.assertEquals(WRONG_ERROR_MSG, "b", cv2.getValue());
            ResteasyConstraintViolation cv = report.getPropertyViolations().iterator().next();
            Assert.assertEquals(WRONG_ERROR_MSG, "size must be between 3 and 5", cv.getMessage());
            Assert.assertEquals(WRONG_ERROR_MSG, "c", cv.getValue());
            cv = report.getClassViolations().iterator().next();
            Assert.assertEquals(WRONG_ERROR_MSG, "Concatenation of s and u must have length > 5", cv.getMessage());
            logger.info("value: " + cv.getValue());
            Assert.assertTrue(WRONG_ERROR_MSG, cv.getValue().startsWith("org.jboss.resteasy.test.validation.resource.ValidationXMLResourceWithAllFivePotentialViolations@"));
            cv = report.getParameterViolations().iterator().next();
            Assert.assertEquals(WRONG_ERROR_MSG, "s must have length: 3 <= length <= 5", cv.getMessage());
            Assert.assertEquals(WRONG_ERROR_MSG, "ValidationXMLFoo[p]", cv.getValue());
            response.close();
        }
    }


    protected void doTestJSON_post(String mediaType) throws Exception {
        {
            // Text form
            ValidationXMLFoo foo = new ValidationXMLFoo("123");
            Response response = client.target(generateURL("/abc/pqr/xyz")).request().accept(mediaType).post(Entity.entity(foo, "application/foo"));
            Assert.assertEquals(HttpResponseCodes.SC_INTERNAL_SERVER_ERROR, response.getStatus());
            String entity = response.readEntity(String.class);
            logger.info("report: " + entity);
            String start = "\"exception\":null";
            String fieldViolation = "\"fieldViolations\":[]";
            String propertyViolation = "\"propertyViolations\":[]";
            String classViolation = "\"classViolations\":[]";
            String parameterViolation = "\"parameterViolations\":[],";
            String returnValueViolation = "\"returnValueViolations\":[{\"constraintType\":\"RETURN_VALUE\",\"path\":\"post.<return value>\",\"message\":\"s must have length: 4 <= length <= 5\",\"value\":\"ValidationXMLFoo[123]\"";
            Assert.assertTrue(WRONG_ERROR_MSG, entity.contains(start));
            Assert.assertTrue(WRONG_ERROR_MSG, entity.contains(fieldViolation));
            Assert.assertTrue(WRONG_ERROR_MSG, entity.contains(propertyViolation));
            Assert.assertTrue(WRONG_ERROR_MSG, entity.contains(classViolation));
            Assert.assertTrue(WRONG_ERROR_MSG, entity.contains(parameterViolation));
            Assert.assertTrue(WRONG_ERROR_MSG, entity.contains(returnValueViolation));
            response.close();
        }

        {
            // Unmarshal report,
            ValidationXMLFoo foo = new ValidationXMLFoo("123");
            Response response = client.target(generateURL("/abc/pqr/xyz")).request().accept(MediaType.APPLICATION_XML).post(Entity.entity(foo, "application/foo"));
            Assert.assertEquals(HttpResponseCodes.SC_INTERNAL_SERVER_ERROR, response.getStatus());
            ViolationReport report = response.readEntity(ViolationReport.class);
            logger.info("report: " + report);
            TestUtil.countViolations(report, 0, 0, 0, 0, 1);
            ResteasyConstraintViolation cv = report.getReturnValueViolations().iterator().next();
            Assert.assertEquals(WRONG_ERROR_MSG, "s must have length: 4 <= length <= 5", cv.getMessage());
            Assert.assertEquals(WRONG_ERROR_MSG, foo.toString(), cv.getValue());
            response.close();
        }
    }

    protected void doTestText_pre(String mediaType) throws Exception {
        ValidationXMLFoo foo = new ValidationXMLFoo("p");
        Response response = client.target(generateURL("/a/b/c")).request().accept(mediaType).post(Entity.entity(foo, "application/foo"));
        Assert.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
        String entity = response.readEntity(String.class);
        logger.info("report:");
        logger.info(entity);
        String fieldViolation1 =
                "[FIELD]\r" +
                        "[s]\r" +
                        "[size must be between 2 and 4]\r" +
                        "[a]\r";
        String fieldViolation2 =
                "[FIELD]\r" +
                        "[t]\r" +
                        "[size must be between 2 and 4]\r" +
                        "[b]\r";
        String propertyViolation =
                "[PROPERTY]\r" +
                        "[u]\r" +
                        "[size must be between 3 and 5]\r" +
                        "[c]\r";
        String classViolation =
                "[CLASS]\r" +
                        "[]\r" +
                        "[Concatenation of s and u must have length > 5]\r" +
                        "[org.jboss.resteasy.test.validation.resource.ValidationXMLResourceWithAllFivePotentialViolations";
        String parameterViolationP1 =
                "[PARAMETER]\r" +
                        "[post.";
        String parameterViolationP2 =
                "]\r" +
                        "[s must have length: 3 <= length <= 5]\r" +
                        "[ValidationXMLFoo[p]]";
        Assert.assertTrue(WRONG_ERROR_MSG, entity.contains(fieldViolation1));
        Assert.assertTrue(WRONG_ERROR_MSG, entity.contains(fieldViolation2));
        Assert.assertTrue(WRONG_ERROR_MSG, entity.contains(propertyViolation));
        Assert.assertTrue(WRONG_ERROR_MSG, entity.contains(classViolation));
        Assert.assertTrue(WRONG_ERROR_MSG, entity.contains(parameterViolationP1));
        Assert.assertTrue(WRONG_ERROR_MSG, entity.contains(parameterViolationP2));
        response.close();
    }

    protected void doTestText_post(String mediaType) throws Exception {
        ValidationXMLFoo foo = new ValidationXMLFoo("123");
        Response response = client.target(generateURL("/abc/pqr/xyz")).request().accept(mediaType).post(Entity.entity(foo, "application/foo"));
        Assert.assertEquals(HttpResponseCodes.SC_INTERNAL_SERVER_ERROR, response.getStatus());
        String entity = response.readEntity(String.class);
        logger.info("report: " + entity);
        String returnValueViolation =
                "[RETURN_VALUE]\r" +
                        "[post.<return value>]\r" +
                        "[s must have length: 4 <= length <= 5]\r" +
                        "[ValidationXMLFoo[123]]\r\r";
        Assert.assertTrue(WRONG_ERROR_MSG, entity.equals(returnValueViolation));
        response.close();
    }
}
