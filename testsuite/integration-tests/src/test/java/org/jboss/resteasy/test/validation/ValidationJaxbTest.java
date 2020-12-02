package org.jboss.resteasy.test.validation;

import static org.hamcrest.CoreMatchers.containsString;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.hamcrest.Matchers;
import org.hibernate.validator.HibernateValidatorPermission;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;
import org.jboss.resteasy.api.validation.Validation;
import org.jboss.resteasy.api.validation.ViolationReport;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.validation.resource.ValidationCoreClassConstraint;
import org.jboss.resteasy.test.validation.resource.ValidationCoreClassValidator;
import org.jboss.resteasy.test.validation.resource.ValidationCoreFoo;
import org.jboss.resteasy.test.validation.resource.ValidationCoreFooConstraint;
import org.jboss.resteasy.test.validation.resource.ValidationCoreFooReaderWriter;
import org.jboss.resteasy.test.validation.resource.ValidationCoreFooValidator;
import org.jboss.resteasy.test.validation.resource.ValidationCoreResourceWithAllViolationTypes;
import org.jboss.resteasy.test.validation.resource.ValidationCoreResourceWithReturnValues;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.restassured.path.json.JsonPath;

/**
 * @tpSubChapter Validation
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for JBEAP-3280
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ValidationJaxbTest {
   ResteasyClient client;
   private static final String UNEXPECTED_VALIDATION_ERROR_MSG = "Unexpected validation error";
   private static final String WAR_WITH_JSONB = "ValidationJaxbTest";
   private static final String WAR_WITH_JACKSON2 = "ValidationJaxbTestJackson2";

   /**
    * Prepare deployment with resteasy.preferJacksonOverJsonB = false
    */
   @Deployment(name = WAR_WITH_JSONB)
   public static Archive<?> deployWithJsonB() {
      return deploy(WAR_WITH_JSONB, false);
   }

   /**
    * Prepare deployment with resteasy.preferJacksonOverJsonB = true
    */
   @Deployment(name = WAR_WITH_JACKSON2)
   public static Archive<?> deployWithoutJsonB() {
      return deploy(WAR_WITH_JACKSON2, true);
   }


   @Before
   public void init() {
      client = (ResteasyClient)ClientBuilder.newClient().register(ValidationCoreFooReaderWriter.class);
   }

   @After
   public void after() throws Exception {
      client.close();
   }

   /**
    * Prepare deployment with specific archive name and specific resteasy.preferJacksonOverJsonB value
    */
   public static Archive<?> deploy(String archiveName, Boolean useJackson) {
      WebArchive war = TestUtil.prepareArchive(archiveName)
            .addClasses(ValidationCoreFoo.class, ValidationCoreFooConstraint.class, ValidationCoreFooReaderWriter.class, ValidationCoreFooValidator.class)
            .addClasses(ValidationCoreClassConstraint.class, ValidationCoreClassValidator.class)
            .addClasses(ValidationCoreResourceWithAllViolationTypes.class, ValidationCoreResourceWithReturnValues.class)
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
      war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
            new HibernateValidatorPermission("accessPrivateMembers")
      ), "permissions.xml");
      Map<String, String> contextParams = new HashMap<>();
      contextParams.put(ResteasyContextParameters.RESTEASY_PREFER_JACKSON_OVER_JSONB, useJackson.toString());
      return TestUtil.finishContainerPrepare(war, contextParams, (Class<?>[]) null);
   }

   private static String generateURL(String path) {
      return PortProviderUtil.generateURL(path, WAR_WITH_JSONB);
   }

   private static String generateJacksonURL(String path) {
      return PortProviderUtil.generateURL(path, WAR_WITH_JACKSON2);
   }


   /**
    * @tpTestDetails Raw XML check.
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testRawXML() throws Exception {
      doRawXMLTest(MediaType.APPLICATION_XML_TYPE, "<propertyViolations><constraintType>PROPERTY</constraintType><path>s</path>", generateURL("/all/a/z"));
   }

   /**
    * @tpTestDetails Raw JSON check.
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testRawJSON() throws Exception {
      ValidationCoreFoo foo = new ValidationCoreFoo("p");
      Response response = client.target(generateURL("/all/a/z")).request().accept(MediaType.APPLICATION_JSON_TYPE).post(Entity.entity(foo, "application/foo"));
      assertValidationReport(response);
   }


   @Test
   public void testRawJSONWithJackson2() throws Exception {
      ValidationCoreFoo foo = new ValidationCoreFoo("p");
      Response response = client.target(generateJacksonURL("/all/a/z")).request().accept(MediaType.APPLICATION_JSON_TYPE).post(Entity.entity(foo, "application/foo"));
      assertValidationReport(response);
   }
   /**
    * @tpTestDetails ViolationReport from XML check.
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testXML() throws Exception {
      doTest(MediaType.APPLICATION_XML_TYPE, client.target(generateURL("/all/a/z")));
   }

   /**
    * @tpTestDetails ViolationReport from JSON check.
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testJSON() throws Exception
   {
      doTest(MediaType.APPLICATION_JSON_TYPE, client.target(generateURL("/all/a/z")));
   }

   @Test
   public void testJSONJackson() throws Exception
   {
      doTest(MediaType.APPLICATION_JSON_TYPE, client.target(generateJacksonURL("/all/a/z")));
   }

   public void doTest(MediaType mediaType, WebTarget target) throws Exception {
      ValidationCoreFoo foo = new ValidationCoreFoo("p");
      Response response = target.request().accept(mediaType).post(Entity.entity(foo, "application/foo"));
      Assert.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
      String header = response.getHeaderString(Validation.VALIDATION_HEADER);
      Assert.assertNotNull("Validation header is missing", header);
      Assert.assertTrue("Wrong value of validation header", Boolean.valueOf(header));
      ViolationReport r = response.readEntity(ViolationReport.class);
      TestUtil.countViolations(r, 2, 1, 1, 0);
      ResteasyConstraintViolation violation = TestUtil.getViolationByPath(r.getPropertyViolations(), "s");
      Assert.assertNotNull(UNEXPECTED_VALIDATION_ERROR_MSG, violation);
      violation = TestUtil.getViolationByPath(r.getPropertyViolations(), "t");
      Assert.assertNotNull(UNEXPECTED_VALIDATION_ERROR_MSG, violation);
      violation = r.getClassViolations().iterator().next();
      Assert.assertEquals(UNEXPECTED_VALIDATION_ERROR_MSG, "", violation.getPath());
      violation = r.getParameterViolations().iterator().next();
      String[] paths = new String[]{"post.arg0", "post.foo"};
      Assert.assertTrue(UNEXPECTED_VALIDATION_ERROR_MSG + paths, Arrays.asList(paths).contains(violation.getPath()));
      response.close();
   }

   public void doRawXMLTest(MediaType mediaType, String expected, String targetURL) throws Exception {
      ValidationCoreFoo foo = new ValidationCoreFoo("p");
      Response response = client.target(targetURL).request().accept(mediaType).post(Entity.entity(foo, "application/foo"));
      Assert.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
      String header = response.getHeaderString(Validation.VALIDATION_HEADER);
      Assert.assertNotNull("Validation header is missing", header);
      Assert.assertTrue("Wrong value of validation header", Boolean.valueOf(header));
      String report = response.readEntity(String.class);
      Assert.assertThat(UNEXPECTED_VALIDATION_ERROR_MSG, report, containsString(expected));
      response.close();
   }

   private void assertValidationReport(Response response)  {
      JsonPath jsonPath = new JsonPath(response.readEntity(String.class));
      Assert.assertThat(UNEXPECTED_VALIDATION_ERROR_MSG, jsonPath.getList("propertyViolations.constraintType"), Matchers.hasItem("PROPERTY"));
      Assert.assertThat(UNEXPECTED_VALIDATION_ERROR_MSG, jsonPath.getList("propertyViolations.path"), Matchers.hasItem("s"));
   }
}

