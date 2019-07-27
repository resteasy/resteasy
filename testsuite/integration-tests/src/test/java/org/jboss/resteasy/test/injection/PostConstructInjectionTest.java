package org.jboss.resteasy.test.injection;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.hibernate.validator.HibernateValidatorPermission;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.api.validation.Validation;
import org.jboss.resteasy.test.injection.resource.PostConstructInjectionEJBInterceptorResource;
import org.jboss.resteasy.test.injection.resource.PostConstructInjectionResource;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Validation and @PostConstruct methods
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression tests for RESTEASY-2227
 * @tpSince RESTEasy 3.6
 */
@RunWith(Arquillian.class)
@RunAsClient
public class PostConstructInjectionTest {

   static Client client;

   // deployment names
   private static final String WAR_CDI_ON = "war_with_cdi_on";
   private static final String WAR_CDI_OFF = "war_with_cdi_off";

   /**
    * Deployment with CDI activated
    */
   @Deployment(name = WAR_CDI_ON)
   public static Archive<?> deployCdiOn() {
      WebArchive war = TestUtil.prepareArchive(PostConstructInjectionTest.class.getSimpleName() + "_CDI_ON");
      war.addAsWebInfResource(PostConstructInjectionTest.class.getPackage(), "PostConstructInjection_beans_cdi_on.xml", "beans.xml");
      war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
              new HibernateValidatorPermission("accessPrivateMembers")
      ), "permissions.xml");
      return TestUtil.finishContainerPrepare(war, null, PostConstructInjectionResource.class, PostConstructInjectionEJBInterceptorResource.class);
   }

   /**
    * Deployment with CDI not activated
    */
   @Deployment(name = WAR_CDI_OFF)
   public static Archive<?> deployCdiOff() {
      WebArchive war = TestUtil.prepareArchive(PostConstructInjectionTest.class.getSimpleName() + "_CDI_OFF");
      war.addAsWebInfResource(PostConstructInjectionTest.class.getPackage(), "PostConstructInjection_beans_cdi_off.xml", "beans.xml");
      war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
              new HibernateValidatorPermission("accessPrivateMembers")
      ), "permissions.xml");
      return TestUtil.finishContainerPrepare(war, null, PostConstructInjectionResource.class);
   }

   @BeforeClass
   public static void init() {
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void after() {
      client.close();
   }

   private String generateURL(String jar, String path) {
      return PortProviderUtil.generateURL(path, PostConstructInjectionTest.class.getSimpleName() + "_CDI_" + jar);
   }

   /**
    * @tpTestDetails In an environment with managed beans, a @PostConstruct method on either an ordinary
    *                resource or an EJB interceptor should execute before class and property validation is done.
    * @tpSince RESTEasy 3.7.0
    */
   @Test
   public void TestPostInjectCdiOn() throws Exception {
      doTestPostInjectCdiOn("ON", "normal");
      doTestPostInjectCdiOn("ON", "ejb");
   }

   /**
    * @tpTestDetails In an environment without managed beans, a @PostConstruct method on a resource will not be called.
    * @tpSince RESTEasy 3.7.0
    */
   @Test
   public void TestPostInjectCdiOff() throws Exception {
      Response response = client.target(generateURL("OFF", "/normal/get")).request().get();
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("ab", response.readEntity(String.class));
      response.close();
   }

   void doTestPostInjectCdiOn(String cdi, String resource) {
      Response response = client.target(generateURL(cdi, "/normal/get")).request().get();
      Assert.assertEquals(400, response.getStatus());
      String header = response.getHeaderString(Validation.VALIDATION_HEADER);
      Assert.assertNotNull(header);
      response.close();
   }
}
