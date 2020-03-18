package org.jboss.resteasy.test.validation;

import java.lang.reflect.ReflectPermission;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.test.validation.resource.ConstraintViolationExceptionApplication;
import org.jboss.resteasy.test.validation.resource.ConstraintViolationExceptionDTO;
import org.jboss.resteasy.test.validation.resource.ConstraintViolationExceptionResource;
import org.jboss.resteasy.test.validation.resource.ConstraintViolationExceptionResourceImpl;
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
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Validation
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-2522.
 * @tpSince RESTEasy 3.6.1.SP2
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ConstraintViolationExceptionTest
{
   private static ResteasyClient client;

   @Deployment
   public static Archive<?> createTestArchive() {
      WebArchive war = TestUtil.prepareArchive(ConstraintViolationExceptionTest.class.getSimpleName())
            .addClass(ConstraintViolationExceptionDTO.class)
            .addClass(ConstraintViolationExceptionResource.class)
            .addClass(ConstraintViolationExceptionApplication.class)
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
      war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
            new ReflectPermission("suppressAccessChecks")
            ), "permissions.xml");
      return TestUtil.finishContainerPrepare(war, null, ConstraintViolationExceptionResourceImpl.class);
   }

   private static String generateURL(String path) {
      return PortProviderUtil.generateURL(path, ConstraintViolationExceptionTest.class.getSimpleName());
   }

   @BeforeClass
   public static void beforeClass() {
      client = (ResteasyClient)ClientBuilder.newClient();
   }

   @AfterClass
   public static void afterClass() {
      client.close();
   }

   /**
    * @tpTestDetails Verify that ConstraintViolationException is caught
    *                in GeneralValidatorImpl.checkForConstraintViolations().
    * @tpSince RESTEasy 3.6.1.SP2
    */
   @Test
   public void testConstraintViolationException() throws Exception
   {
      System.out.println("URL: " + generateURL("/app"));
      ResteasyWebTarget target = client.target(generateURL("/app"));
      ConstraintViolationExceptionResource customerResource = target.proxy(ConstraintViolationExceptionResource.class);
      Response response = customerResource.validate("x");
      Assert.assertEquals("Response code should be 400", 400, response.getStatus());
      Response response2 = customerResource.validate("abcd");
      Assert.assertEquals("Response code should be 200", 200, response2.getStatus());
      Response response3 = customerResource.validate("y");
      Assert.assertEquals("Response code should be 400", 400, response3.getStatus());
   }
}
