package org.jboss.resteasy.test.microprofile.config;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.test.microprofile.config.resource.MicroProfileConfigFilter;
import org.jboss.resteasy.test.microprofile.config.resource.MicroProfileConfigResource;
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

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.util.PropertyPermission;

/**
 * @tpSubChapter MicroProfile Config
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression tests for RESTEASY-2131
 * @tpSince RESTEasy 4.0.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class MicroProfileConfigServletTest {

   static ResteasyClient client;

   @Deployment
   public static Archive<?> deploy() {
      WebArchive war = TestUtil.prepareArchive(MicroProfileConfigServletTest.class.getSimpleName())
            .addClass(MicroProfileConfigFilter.class)
            .setWebXML(MicroProfileConfigServletTest.class.getPackage(), "web_servlet.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
      war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
              new PropertyPermission("system", "write")
      ), "permissions.xml");
      return TestUtil.finishContainerPrepare(war, null, MicroProfileConfigResource.class);
   }

   @BeforeClass
   public static void before() throws Exception {
      client = (ResteasyClient)ClientBuilder.newClient();
   }

   @AfterClass
   public static void after() throws Exception {
      client.close();
   }

   private String generateURL(String path) {
      return PortProviderUtil.generateURL(path, MicroProfileConfigServletTest.class.getSimpleName());
   }

   /**
    * @tpTestDetails Verify system variables are accessible and have highest priority; get Config programmatically.
    * @tpSince RESTEasy 4.0.0
    */
   @Test
   public void testSystemProgrammatic() throws Exception {
      Response response = client.target(generateURL("/system/prog")).request().get();
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("system-system", response.readEntity(String.class));
   }

   /**
    * @tpTestDetails Verify system variables are accessible and have highest priority; get Config by injection.
    * @tpSince RESTEasy 4.0.0
    */
   @Test
   public void testSystemInject() throws Exception {
      Response response = client.target(generateURL("/system/inject")).request().get();
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("system-system", response.readEntity(String.class));
   }

   /**
    * @tpTestDetails Verify web.xml servlet init params are accessible and have higher priority than filter params and context params; get Config programmatically.
    * @tpSince RESTEasy 4.0.0
    */
   @Test
   public void testInitProgrammatic() throws Exception {
      Response response = client.target(generateURL("/init/prog")).request().get();
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("init-init", response.readEntity(String.class));
   }

   /**
    * @tpTestDetails Verify web.xml servlet init params are accessible and have higher priority than filter params and context params; get Config by injection.
    * @tpSince RESTEasy 4.0.0
    */
   @Test
   public void testInitInject() throws Exception {
      Response response = client.target(generateURL("/init/inject")).request().get();
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("init-init", response.readEntity(String.class));
   }

   /**
    * @tpTestDetails Verify web.xml context params are accessible; get Config programmatically.
    * @tpSince RESTEasy 4.0.0
    */
   @Test
   public void testContextProgrammatic() throws Exception {
      Response response = client.target(generateURL("/context/prog")).request().get();
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("context-context", response.readEntity(String.class));
   }

   /**
    * @tpTestDetails Verify web.xml context params are accessible; get Config by injection.
    * @tpSince RESTEasy 4.0.0
    */
   @Test
   public void testContextInject() throws Exception {
      Response response = client.target(generateURL("/context/inject")).request().get();
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("context-context", response.readEntity(String.class));
   }

   /**
    * @tpTestDetails Verify former context parameter "resteasy.add.charset" is overridden by system property.
    * @tpSince RESTEasy 4.0.0
    */
   @Test
   public void testActualContextParameter() throws Exception {
      Response response = client.target(generateURL("/actual")).request().get();
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("text/plain", response.getHeaderString("Content-Type"));
   }
}
