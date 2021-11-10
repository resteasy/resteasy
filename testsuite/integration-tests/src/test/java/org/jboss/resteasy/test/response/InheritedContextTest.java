package org.jboss.resteasy.test.response;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.response.resource.InheritedContextNewService;
import org.jboss.resteasy.test.response.resource.InheritedContextNewSubService;
import org.jboss.resteasy.test.response.resource.InheritedContextService;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Response
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-952
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class InheritedContextTest {

   @Deployment
   public static Archive<?> deploySimpleResource() {
      WebArchive war = TestUtil.prepareArchive(InheritedContextTest.class.getSimpleName());
      return TestUtil.finishContainerPrepare(war, null, InheritedContextService.class,
            InheritedContextNewService.class, InheritedContextNewSubService.class);
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

   private String generateURL(String path) {
      return PortProviderUtil.generateURL(path, InheritedContextTest.class.getSimpleName());
   }

   /**
    * @tpTestDetails Test basic resource with no inheritance
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testContext() throws Exception {
      Invocation.Builder request = client.target(generateURL("/super/test/BaseService")).request();
      Response response = request.get();
      String s = response.readEntity(String.class);
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertEquals("true", s);
      response.close();
   }

   /**
    * @tpTestDetails Test basic resource with one level of inheritance
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testInheritedContextOneLevel() throws Exception {
      Invocation.Builder request = client.target(generateURL("/sub/test/SomeService")).request();
      Response response = request.get();
      String s = response.readEntity(String.class);
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertEquals("true", s);
      response.close();
   }

   /**
    * @tpTestDetails Test basic resource with two levels of inheritance
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testInheritedContextTwoLevels() throws Exception {
      Invocation.Builder request = client.target(generateURL("/subsub/test/SomeSubService")).request();
      Response response = request.get();
      String s = response.readEntity(String.class);
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertEquals("true", s);
      response.close();
   }
}
