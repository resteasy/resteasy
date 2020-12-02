package org.jboss.resteasy.test.validation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hamcrest.Matchers;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;
import org.jboss.resteasy.api.validation.ViolationReport;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;

import io.restassured.path.json.JsonPath;

import jakarta.ws.rs.client.ClientBuilder;

import org.jboss.resteasy.test.validation.resource.ValidationXMLClassConstraint;
import org.jboss.resteasy.test.validation.resource.ValidationXMLClassValidator;
import org.jboss.resteasy.test.validation.resource.ValidationXMLFoo;
import org.jboss.resteasy.test.validation.resource.ValidationXMLFooConstraint;
import org.jboss.resteasy.test.validation.resource.ValidationXMLFooReaderWriter;
import org.jboss.resteasy.test.validation.resource.ValidationXMLFooValidator;
import org.jboss.resteasy.test.validation.resource.ValidationXMLResourceWithAllFivePotentialViolations;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.HashMap;
import java.util.Map;


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
      Map<String, String> contextParams = new HashMap<>();
      contextParams.put(ResteasyContextParameters.RESTEASY_PREFER_JACKSON_OVER_JSONB, "true");
      return TestUtil.finishContainerPrepare(war, contextParams, ValidationXMLFooReaderWriter.class, ValidationXMLResourceWithAllFivePotentialViolations.class);
   }

   @Before
   public void init() {
      client = (ResteasyClient)ClientBuilder.newClient().register(ValidationXMLFooReaderWriter.class);
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
         String propertyViolation1 = "<propertyViolations><constraintType>PROPERTY</constraintType><path>s</path><message>size must be between 2 and 4</message><value>a</value></propertyViolations>";
         String propertyViolation2 = "<propertyViolations><constraintType>PROPERTY</constraintType><path>t</path><message>size must be between 2 and 4</message><value>b</value></propertyViolations>";
         String propertyViolation3 = "<propertyViolations><constraintType>PROPERTY</constraintType><path>u</path><message>size must be between 3 and 5</message><value>c</value></propertyViolations>";
         String classViolationStart = "<classViolations><constraintType>CLASS</constraintType><path></path><message>Concatenation of s and u must have length &gt; 5</message><value>org.jboss.resteasy.test.validation.resource.ValidationXMLResourceWithAllFivePotentialViolations";
         String classViolationEnd = "</value></classViolations>";
         String parameterViolationP1 = "<parameterViolations><constraintType>PARAMETER</constraintType><path>post.";
         String parameterViolationP2 = "</path><message>s must have length: 3 &lt;= length &lt;= 5</message><value>ValidationXMLFoo[p]</value></parameterViolations>";
         String end = "</violationReport>";
         Assert.assertTrue(WRONG_ERROR_MSG, entity.contains(start));
         Assert.assertTrue(WRONG_ERROR_MSG, entity.contains(propertyViolation1));
         Assert.assertTrue(WRONG_ERROR_MSG, entity.contains(propertyViolation2));
         Assert.assertTrue(WRONG_ERROR_MSG, entity.contains(propertyViolation3));
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
         TestUtil.countViolations(report, 3, 1, 1, 0);
         ResteasyConstraintViolation cv = TestUtil.getViolationByMessageAndValue(report.getPropertyViolations(), "size must be between 2 and 4", "a");
         Assert.assertNotNull(WRONG_ERROR_MSG, cv);
         cv = TestUtil.getViolationByMessageAndValue(report.getPropertyViolations(), "size must be between 2 and 4", "b");
         Assert.assertNotNull(WRONG_ERROR_MSG, cv);
         cv = TestUtil.getViolationByMessage(report.getPropertyViolations(), "size must be between 3 and 5");
         Assert.assertNotNull(WRONG_ERROR_MSG, cv);
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
         TestUtil.countViolations(report, 0, 0, 0, 1);
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
         JsonPath jsonPath = new JsonPath(entity);
         Assert.assertThat(WRONG_ERROR_MSG, jsonPath.getList("propertyViolations.path"), Matchers.hasItems("s", "t"));
         Assert.assertThat(WRONG_ERROR_MSG, jsonPath.getList("propertyViolations.value"), Matchers.hasItems("a", "b"));
         Assert.assertThat(WRONG_ERROR_MSG, jsonPath.getList("propertyViolations.message"), Matchers.hasItems("size must be between 2 and 4", "size must be between 2 and 4"));

         Assert.assertThat(WRONG_ERROR_MSG, jsonPath.getList("propertyViolations.path"), Matchers.hasItem("u"));
         Assert.assertThat(WRONG_ERROR_MSG, jsonPath.getList("propertyViolations.value"), Matchers.hasItem("c"));
         Assert.assertThat(WRONG_ERROR_MSG, jsonPath.getList("propertyViolations.message"), Matchers.hasItem("size must be between 3 and 5"));

         Assert.assertThat(WRONG_ERROR_MSG, jsonPath.getList("classViolations.path"), Matchers.hasItem(""));
         Assert.assertThat(WRONG_ERROR_MSG, jsonPath.getList("classViolations.message"), Matchers.hasItem("Concatenation of s and u must have length > 5"));
         Assert.assertThat(WRONG_ERROR_MSG, jsonPath.getList("parameterViolations.path"), Matchers.hasSize(1));
         Assert.assertThat(WRONG_ERROR_MSG, jsonPath.getList("parameterViolations.path", String.class).get(0), Matchers.startsWith("post."));
         Assert.assertThat(WRONG_ERROR_MSG, jsonPath.getList("parameterViolations.message"), Matchers.hasItem("s must have length: 3 <= length <= 5"));
         Assert.assertThat(WRONG_ERROR_MSG, jsonPath.getList("parameterViolations.value"), Matchers.hasItem("ValidationXMLFoo[p]"));
         Assert.assertThat(WRONG_ERROR_MSG, jsonPath.getList("returnValueViolations"), Matchers.hasSize(0));
         response.close();
      }

      {
         // Unmarshal report,
         ValidationXMLFoo foo = new ValidationXMLFoo("p");
         Response response = client.target(generateURL("/a/b/c")).request().accept(MediaType.APPLICATION_XML).post(Entity.entity(foo, "application/foo"));
         Assert.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
         ViolationReport report = response.readEntity(ViolationReport.class);
         logger.info("report: " + report);
         TestUtil.countViolations(report, 3, 1, 1, 0);
         ResteasyConstraintViolation cv = TestUtil.getViolationByMessageAndValue(report.getPropertyViolations(), "size must be between 2 and 4", "a");
         Assert.assertNotNull(WRONG_ERROR_MSG, cv);
         cv = TestUtil.getViolationByMessageAndValue(report.getPropertyViolations(), "size must be between 2 and 4", "b");
         Assert.assertNotNull(WRONG_ERROR_MSG, cv);
         cv = TestUtil.getViolationByMessage(report.getPropertyViolations(), "size must be between 3 and 5");
         Assert.assertNotNull(WRONG_ERROR_MSG, cv);
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
         JsonPath jsonPath = new JsonPath(entity);
         Assert.assertThat(WRONG_ERROR_MSG, jsonPath.getList("propertyViolations"), Matchers.hasSize(0));
         Assert.assertThat(WRONG_ERROR_MSG, jsonPath.getList("classViolations"), Matchers.hasSize(0));
         Assert.assertThat(WRONG_ERROR_MSG, jsonPath.getList("parameterViolations"), Matchers.hasSize(0));

         Assert.assertThat(WRONG_ERROR_MSG, jsonPath.getList("returnValueViolations.constraintType"), Matchers.hasItem("RETURN_VALUE"));
         Assert.assertThat(WRONG_ERROR_MSG, jsonPath.getList("returnValueViolations.path"), Matchers.hasItem("post.<return value>"));
         Assert.assertThat(WRONG_ERROR_MSG, jsonPath.getList("returnValueViolations.message"), Matchers.hasItem("s must have length: 4 <= length <= 5"));
         Assert.assertThat(WRONG_ERROR_MSG, jsonPath.getList("returnValueViolations.value"), Matchers.hasItem("ValidationXMLFoo[123]"));
         response.close();
      }

      {
         // Unmarshal report,
         ValidationXMLFoo foo = new ValidationXMLFoo("123");
         Response response = client.target(generateURL("/abc/pqr/xyz")).request().accept(MediaType.APPLICATION_XML).post(Entity.entity(foo, "application/foo"));
         Assert.assertEquals(HttpResponseCodes.SC_INTERNAL_SERVER_ERROR, response.getStatus());
         ViolationReport report = response.readEntity(ViolationReport.class);
         logger.info("report: " + report);
         TestUtil.countViolations(report, 0, 0, 0, 1);
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
      String propertyViolation1 =
            "[PROPERTY]\r" +
                  "[s]\r" +
                  "[size must be between 2 and 4]\r" +
                  "[a]\r";
      String propertyViolation2 =
            "[PROPERTY]\r" +
                  "[t]\r" +
                  "[size must be between 2 and 4]\r" +
                  "[b]\r";
      String propertyViolation3 =
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
      Assert.assertTrue(WRONG_ERROR_MSG, entity.contains(propertyViolation1));
      Assert.assertTrue(WRONG_ERROR_MSG, entity.contains(propertyViolation2));
      Assert.assertTrue(WRONG_ERROR_MSG, entity.contains(propertyViolation3));
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
