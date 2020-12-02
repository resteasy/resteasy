package org.jboss.resteasy.test.microprofile.config;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.test.microprofile.config.resource.MicroProfileConfigUseGlobalApplication1;
import org.jboss.resteasy.test.microprofile.config.resource.MicroProfileConfigUseGlobalApplication2;
import org.jboss.resteasy.test.microprofile.config.resource.MicroProfileConfigUseGlobalResource;
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
 * @tpSubChapter MicroProfile Config: ServletConfig with useGlobal and multiple servlets
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression tests for RESTEASY-2266
 * @tpSince RESTEasy 4.1.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class MicroProfileConfigUseGlobalTest {

   static ResteasyClient client;

   @Deployment
   public static Archive<?> deploy() {
      WebArchive war = TestUtil.prepareArchive(MicroProfileConfigUseGlobalTest.class.getSimpleName())
            .addClass(MicroProfileConfigUseGlobalApplication1.class)
            .addClass(MicroProfileConfigUseGlobalApplication2.class)
            .setWebXML(MicroProfileConfigUseGlobalTest.class.getPackage(), "web_use_global.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
      return TestUtil.finishContainerPrepare(war, null, MicroProfileConfigUseGlobalResource.class);
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
      return PortProviderUtil.generateURL(path, MicroProfileConfigUseGlobalTest.class.getSimpleName());
   }

   /**
    * @tpTestDetails
    * @tpSince RESTEasy 4.1.0
    */
   @Test
   public void testMultipleAppsUseGlobal() throws Exception {
      Response response = client.target(generateURL("/app1/prefix")).request().get();
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("/app1", response.readEntity(String.class));
      response = client.target(generateURL("/app2/prefix")).request().get();
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("/app2", response.readEntity(String.class));
   }
}
