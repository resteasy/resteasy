package org.jboss.resteasy.test.validation.cdi;

import org.hibernate.validator.HibernateValidatorPermission;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;
import org.jboss.resteasy.api.validation.ViolationReport;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import org.jboss.resteasy.client.jaxrs.internal.ClientResponse;
import org.jboss.resteasy.test.validation.cdi.resource.CDIValidationCoreResource;
import org.jboss.resteasy.test.validation.cdi.resource.CDIValidationCoreSumConstraint;
import org.jboss.resteasy.test.validation.cdi.resource.CDIValidationCoreSumValidator;
import org.jboss.resteasy.test.validation.cdi.resource.CDIValidationCoreSubResource;
import org.jboss.resteasy.spi.HttpResponseCodes;
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
import jakarta.ws.rs.client.Invocation;

import static org.junit.Assert.assertEquals;

/**
 * @tpSubChapter Response
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-1008
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class CDIValidationCoreTest {
   @Deployment
   public static Archive<?> createTestArchive() {
      WebArchive war = TestUtil.prepareArchive(CDIValidationCoreTest.class.getSimpleName())
            .addClass(CDIValidationCoreSubResource.class)
            .addClasses(CDIValidationCoreSumConstraint.class, CDIValidationCoreSumValidator.class)
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
      war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
            new HibernateValidatorPermission("accessPrivateMembers")
      ), "permissions.xml");
      return TestUtil.finishContainerPrepare(war, null, CDIValidationCoreResource.class);
   }

   private String generateURL(String path) {
      return PortProviderUtil.generateURL(path, CDIValidationCoreTest.class.getSimpleName());
   }

   protected Client client;

   @Before
   public void beforeTest()
   {
      client = ClientBuilder.newClient();
   }

   @After
   public void afterTest()
   {
      client.close();
      client = null;
   }

   /**
    * @tpTestDetails Check validation with all valid parameters
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testAllValid() throws Exception {
      Invocation.Builder request = client.target(generateURL("/input/11/13/17")).request();
      ClientResponse response = (ClientResponse) request.get();
      int answer = response.readEntity(Integer.class);
      assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      assertEquals("Wrong content of response", 17, answer);
   }

   /**
    * @tpTestDetails Check validation with invalid inputs
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testInputsInvalid() throws Exception {
      Invocation.Builder request = client.target(generateURL("/input/1/2/3")).request();
      ClientResponse response = (ClientResponse) request.get();
      String answer = response.readEntity(String.class);
      assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
      ViolationReport r = new ViolationReport(answer);
      TestUtil.countViolations(r, 2, 1, 1, 0);
      ResteasyConstraintViolation cv = TestUtil.getViolationByMessage(r.getPropertyViolations(), "must be greater than or equal to 3");
      Assert.assertNotNull("Expected validation error is not in response", cv);
      cv = TestUtil.getViolationByMessage(r.getPropertyViolations(), "must be greater than or equal to 5");
      Assert.assertNotNull("Expected validation error is not in response", cv);
      cv = r.getClassViolations().iterator().next();
      Assert.assertTrue("Expected validation error is not in response", cv.getMessage().indexOf("org.jboss.resteasy.ejb.validation.SumConstraint") > 0);
      cv = r.getParameterViolations().iterator().next();
      Assert.assertTrue("Expected validation error is not in response", cv.getMessage().equals("must be greater than or equal to 7"));
   }

   /**
    * @tpTestDetails Check validation with invalid return value
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testReturnValueInvalid() throws Exception {
      Invocation.Builder request = client.target(generateURL("/input/5/7/9")).request();
      ClientResponse response = (ClientResponse) request.get();
      String answer = response.readEntity(String.class);
      assertEquals(HttpResponseCodes.SC_INTERNAL_SERVER_ERROR, response.getStatus());
      ViolationReport r = new ViolationReport(answer);
      TestUtil.countViolations(r, 0, 0, 0, 1);
      ResteasyConstraintViolation cv = r.getReturnValueViolations().iterator().next();
      Assert.assertTrue("Expected validation error is not in response", cv.getMessage().equals("must be greater than or equal to 11"));
   }

   /**
    * @tpTestDetails Check validation with valid locators
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testLocatorAllValid() throws Exception {
      Invocation.Builder request = client.target(generateURL("/locator/5/7/17/19")).request();
      ClientResponse response = (ClientResponse) request.get();
      int result = response.readEntity(int.class);
      assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      assertEquals("Wrong content of response", 19, result);
   }

   /**
    * @tpTestDetails Check validation with invalid subparameters
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testLocatorInvalidSubparameter() throws Exception {
      Invocation.Builder request = client.target(generateURL("/locator/5/7/13/0")).request();
      ClientResponse response = (ClientResponse) request.get();
      String answer = response.readEntity(String.class);
      assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
      ViolationReport r = new ViolationReport(answer);
      TestUtil.countViolations(r, 0, 0, 1, 0);
      ResteasyConstraintViolation cv = r.getParameterViolations().iterator().next();
      Assert.assertTrue("Expected validation error is not in response", cv.getMessage().equals("must be greater than or equal to 13"));
   }

   /**
    * @tpTestDetails Check validation with locators and invalid return values
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testLocatorInvalidReturnValue() throws Exception {
      Invocation.Builder request = client.target(generateURL("/locator/5/7/13/15")).request();
      ClientResponse response = (ClientResponse) request.get();
      String answer = response.readEntity(String.class);
      assertEquals(HttpResponseCodes.SC_INTERNAL_SERVER_ERROR, response.getStatus());
      ViolationReport r = new ViolationReport(answer);
      TestUtil.countViolations(r, 0, 0, 0, 1);
      ResteasyConstraintViolation cv = r.getReturnValueViolations().iterator().next();
      Assert.assertTrue("Expected validation error is not in response", cv.getMessage().equals("must be greater than or equal to 17"));
   }

   /**
    * @tpTestDetails Check validation with invalid inputs and no executables
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testInputsInvalidNoExecutableValidation() throws Exception {
      Invocation.Builder request = client.target(generateURL("/none/1/2/3")).request();
      ClientResponse response = (ClientResponse) request.get();
      String answer = response.readEntity(String.class);
      assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
      ViolationReport r = new ViolationReport(answer);
      TestUtil.countViolations(r, 2, 1, 0, 0);
      ResteasyConstraintViolation cv = TestUtil.getViolationByMessage(r.getPropertyViolations(), "must be greater than or equal to 3");
      Assert.assertNotNull("Expected validation error is not in response", cv);
      cv = TestUtil.getViolationByMessage(r.getPropertyViolations(), "must be greater than or equal to 5");
      Assert.assertNotNull("Expected validation error is not in response", cv);
      cv = r.getClassViolations().iterator().next();
      Assert.assertTrue("Expected validation error is not in response", cv.getMessage().indexOf("org.jboss.resteasy.ejb.validation.SumConstraint") > 0);
   }

   /**
    * @tpTestDetails Check validation with invalid inputs and no parameters are used
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testInputsInvalidNoParameters() throws Exception {
      Invocation.Builder request = client.target(generateURL("/noParams/1/2")).request();
      ClientResponse response = (ClientResponse) request.get();
      String answer = response.readEntity(String.class);
      assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
      ViolationReport r = new ViolationReport(answer);
      TestUtil.countViolations(r, 2, 1, 0, 0);
      ResteasyConstraintViolation cv = TestUtil.getViolationByMessage(r.getPropertyViolations(), "must be greater than or equal to 3");
      Assert.assertNotNull("Expected validation error is not in response", cv);
      cv = TestUtil.getViolationByMessage(r.getPropertyViolations(), "must be greater than or equal to 5");
      Assert.assertNotNull("Expected validation error is not in response", cv);
      cv = r.getClassViolations().iterator().next();
      Assert.assertTrue("Expected validation error is not in response", cv.getMessage().indexOf("org.jboss.resteasy.ejb.validation.SumConstraint") > 0);
   }
}
