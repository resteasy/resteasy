package org.jboss.resteasy.test.cdi.ejb;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.hibernate.validator.HibernateValidatorPermission;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.api.validation.ViolationReport;
import org.jboss.resteasy.category.ExpectedFailingWithStandaloneMicroprofileConfiguration;
import org.jboss.resteasy.category.NotForBootableJar;
import org.jboss.resteasy.test.cdi.ejb.resource.EJBCDIValidationApplication;
import org.jboss.resteasy.test.cdi.ejb.resource.EJBCDIValidationSingletonResource;
import org.jboss.resteasy.test.cdi.ejb.resource.EJBCDIValidationStatefulResource;
import org.jboss.resteasy.test.cdi.ejb.resource.EJBCDIValidationStatelessResource;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter CDI
 * @tpChapter Integration tests
 * @tpTestCaseDetails EJB, CDI, Validation, and RESTEasy integration test: RESTEASY-1749
 * @tpSince RESTEasy 4.0.0
 */
@RunWith(Arquillian.class)
@RunAsClient
@Category(NotForBootableJar.class) // no EJB layer so far
public class EJBCDIValidationTest {

   private static Client client;

   @Deployment(testable = false)
   public static Archive<?> createTestArchive() {
      WebArchive war = TestUtil.prepareArchive(EJBCDIValidationTest.class.getSimpleName());
      war.addClasses(EJBCDIValidationApplication.class)
      .addClasses(EJBCDIValidationStatelessResource.class)
      .addClasses(EJBCDIValidationStatefulResource.class)
      .addClasses(EJBCDIValidationSingletonResource.class)
      .addClass(ExpectedFailingWithStandaloneMicroprofileConfiguration.class)
      .addClass(NotForBootableJar.class)
      .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
      war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
              new HibernateValidatorPermission("accessPrivateMembers")
      ), "permissions.xml");
      return TestUtil.finishContainerPrepare(war, null);
   }

   private String generateURL(String path) {
      return PortProviderUtil.generateURL(path, EJBCDIValidationTest.class.getSimpleName());
   }

   @BeforeClass
   public static void init() {
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void close() {
      client.close();
   }

   /**
    * @tpTestDetails Verify correct order of validation on stateless EJBs
    * @tpSince RESTEasy 4.0.0
    */
   @Test
   @Category({ExpectedFailingWithStandaloneMicroprofileConfiguration.class})
   public void testStateless() {
      // Expect property, parameter violations.
      WebTarget base = client.target(generateURL("/rest/stateless/"));
      Builder builder = base.path("post/n").request();
      Response response = builder.post(Entity.entity("-1", MediaType.APPLICATION_JSON_TYPE));
      Assert.assertEquals(400, response.getStatus());
      String answer = response.readEntity(String.class);
      ViolationReport r = new ViolationReport(answer);
      TestUtil.countViolations(r, 0, 1, 0, 1, 0);

      // Valid invocation
      response = base.path("set/xyz").request().get();
      Assert.assertEquals(204, response.getStatus());
      response.close();

      // EJB resource has been created: expect parameter violation.
      builder = base.path("post/n").request();
      builder.accept(MediaType.TEXT_PLAIN_TYPE);
      response = builder.post(Entity.entity("-1", MediaType.APPLICATION_JSON_TYPE));
      Assert.assertEquals(400, response.getStatus());
      answer = response.readEntity(String.class);
      r = new ViolationReport(answer);
      TestUtil.countViolations(r, 0, 0, 0, 1, 0);
   }

   /**
    * @tpTestDetails Verify correct order of validation on stateful EJBs
    * @tpSince RESTEasy 4.0.0
    */
   @Test
   public void testStateful() {
      // Expect property, parameter violations
      WebTarget base = client.target(generateURL("/rest/stateful/"));
      Builder builder = base.path("post/n").request();
      Response response = builder.post(Entity.entity("-1", MediaType.APPLICATION_JSON_TYPE));
      Assert.assertEquals(400, response.getStatus());
      String answer = response.readEntity(String.class);
      ViolationReport r = new ViolationReport(answer);
      TestUtil.countViolations(r, 0, 1, 0, 1, 0);

      // Valid invocation
      response = base.path("set/xyz").request().get();
      Assert.assertEquals(204, response.getStatus());
      response.close();

      // EJB resource gets created again: expect property and parameter violations.
      builder = base.path("post/n").request();
      builder.accept(MediaType.TEXT_PLAIN_TYPE);
      response = builder.post(Entity.entity("-1", MediaType.APPLICATION_JSON_TYPE));
      Assert.assertEquals(400, response.getStatus());
      answer = response.readEntity(String.class);
      r = new ViolationReport(answer);
      TestUtil.countViolations(r, 0, 1, 0, 1, 0);
   }

   /**
    * @tpTestDetails Verify correct order of validation on singleton EJBs
    * @tpSince RESTEasy 4.0.0
    */
   @Test
   @Category({ExpectedFailingWithStandaloneMicroprofileConfiguration.class})
   public void testSingleton() {
      doTestSingleton(1); // Expect property violation when EJB resource gets created.
      doTestSingleton(0); // EJB resource has been created: expect no property violation.
   }

   void doTestSingleton(int propertyViolations) {
      // Expect property, parameter violations
      WebTarget base = client.target(generateURL("/rest/singleton/"));
      Builder builder = base.path("post/n").request();
      Response response = builder.post(Entity.entity("-1", MediaType.APPLICATION_JSON_TYPE));
      Assert.assertEquals(400, response.getStatus());
      String answer = response.readEntity(String.class);
      ViolationReport r = new ViolationReport(answer);
      TestUtil.countViolations(r, 0, propertyViolations, 0, 1, 0);

      // Valid invocation
      response = base.path("set/xyz").request().get();
      Assert.assertEquals(204, response.getStatus());
      response.close();

      // EJB resource has been created: expect parameter violation.
      builder = base.path("post/n").request();
      builder.accept(MediaType.TEXT_PLAIN_TYPE);
      response = builder.post(Entity.entity("-1", MediaType.APPLICATION_JSON_TYPE));
      Assert.assertEquals(400, response.getStatus());
      answer = response.readEntity(String.class);
      r = new ViolationReport(answer);
      TestUtil.countViolations(r, 0, 0, 0, 1, 0);
   }
}
