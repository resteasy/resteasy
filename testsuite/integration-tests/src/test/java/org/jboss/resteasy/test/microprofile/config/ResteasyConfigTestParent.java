package org.jboss.resteasy.test.microprofile.config;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Tests ResteasyConfig in the absence of MicroProfile Config facility
 *               when Resteasy is initialized as a servlet filter.
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression tests for RESTEASY-2406
 * @tpSince RESTEasy 3.12.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public abstract class ResteasyConfigTestParent {

   protected static ResteasyClient client;

   @BeforeClass
   public static void before() throws Exception {
      client = (ResteasyClient)ClientBuilder.newClient();
      // Delete org.eclipse.microprofile.config.Config from ResteasyConfig.
      Response response = client.target(generateURL("/delete")).request().get();
      Assert.assertEquals(204, response.getStatus());
   }

   @AfterClass
   public static void after() throws Exception {
      // Restore org.eclipse.microprofile.config.Config to ResteasyConfig.
      Response response = client.target(generateURL("/restore")).request().get();
      Assert.assertEquals(204, response.getStatus());
      client.close();
   }

   protected static String generateURL(String path) {
      return PortProviderUtil.generateURL(path, ResteasyConfigTestParent.class.getSimpleName());
   }

   /**
    * @tpTestDetails Verify ResteasyConfig.getValue("...") returns null when MicroProfile Config is unavailable.
    * @tpSince RESTEasy 3.12.0
    */
   @Test
   public void testNoMPConfig() throws Exception {
      Response response = client.target(generateURL("/noMPconfig")).request().get();
      Assert.assertEquals(204, response.getStatus());
      Assert.assertNull(response.readEntity(String.class));
   }

   /**
    * @tpTestDetails Verify ResteasyConfig.getValue("...", SOURCE.SYSTEM) when MicroProfile Config is unavailable.
    * @tpSince RESTEasy 3.12.0
    */
   @Test
   public void testSystemFromSource() throws Exception {
      Response response = client.target(generateURL("/system")).request().get();
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("system-system", response.readEntity(String.class));
   }

   /**
    * @tpTestDetails Verify ResteasyConfig.getValue("...", SOURCE.SERVLET_CONTEXT) when MicroProfile Config is unavailable.
    * @tpSince RESTEasy 3.12.0
    */
   @Test
   public void testServletContextFromSource() throws Exception {
      Response response = client.target(generateURL("/servletcontext")).request().get();
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("system-context", response.readEntity(String.class));
   }
}
