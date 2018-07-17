package org.jboss.resteasy.test.core.basic;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.core.basic.resource.Jaxrs20MatchingResource;
import org.jboss.resteasy.util.HttpResponseCodes;
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
 * @tpSubChapter Localization
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.6.1
 */
@RunWith(Arquillian.class)
@RunAsClient
public class Jaxrs20MatchingTest {

   private static final String DEPLOYMENT_20 = "JAXRS_20";
   private static final String DEPLOYMENT_21 = "JAXRS_21";
   private static final String DEPLOYMENT_DEFAULT = "JAXRS_Default";

   private static ResteasyClient client;

   @Deployment(name = DEPLOYMENT_20)
   public static Archive<?> deployJaxrs20() {
      return deploy(DEPLOYMENT_20);
   }

   @Deployment(name = DEPLOYMENT_21)
   public static Archive<?> deployJaxrs21() {
      return deploy(DEPLOYMENT_21);
   }

   @Deployment(name = DEPLOYMENT_DEFAULT)
   public static Archive<?> deployJaxrsDefault() {
      return deploy(DEPLOYMENT_DEFAULT);
   }

   public static Archive<?> deploy(String deploymentName) {
      WebArchive war = TestUtil.prepareArchive(deploymentName);
      if (DEPLOYMENT_20.equals(deploymentName)) {
         war.addAsWebInfResource(Jaxrs20MatchingTest.class.getPackage(), "Jaxrs20MatchingTest_Jaxrs20.xml", "web.xml");
      }
      else if (DEPLOYMENT_21.equals(deploymentName)) {
         war.addAsWebInfResource(Jaxrs20MatchingTest.class.getPackage(), "Jaxrs20MatchingTest_Jaxrs21.xml", "web.xml");
      }
      else {
         war.addAsWebInfResource(Jaxrs20MatchingTest.class.getPackage(), "Jaxrs20MatchingTest_Default.xml", "web.xml");
      }
      return TestUtil.finishContainerPrepare(war, null, Jaxrs20MatchingResource.class);
   }

   private static String generateBaseUrl(String deploymentName) {
      return PortProviderUtil.generateBaseUrl("/" + deploymentName);
   }

   @Before
   public void setup() {
      client = new ResteasyClientBuilder().build();
   }

   @After
   public void after() throws Exception {
      client.close();
   }
   ///////////////////////////////////////////////////////////////////////////////////////////
   /**
    * @tpTestDetails Test JAX-RS 2.0 resource matching
    * @tpSince RESTEasy 3.6.1
    */
   @Test
   public void testJaxrs20() throws Exception {
      WebTarget base = client.target(generateBaseUrl(DEPLOYMENT_20));
      Response response = base.path("/path/match").request().post(Entity.entity("entity", "text/plain"));
      Assert.assertEquals(HttpResponseCodes.SC_METHOD_NOT_ALLOWED, response.getStatus());
      response.close();
   }
   
   /**
    * @tpTestDetails Test JAX-RS 2.1 resource matching
    * @tpSince RESTEasy 3.6.1
    */
   @Test
   public void testJaxrs21() throws Exception {
      WebTarget base = client.target(generateBaseUrl(DEPLOYMENT_21));
      Response response = base.path("/path/match").request().post(Entity.entity("entity", "text/plain"));
      Assert.assertEquals(response.getStatus(), HttpResponseCodes.SC_OK);
      Assert.assertEquals("match", response.readEntity(String.class));
      response.close();
   }
   /**
    * @tpTestDetails Test default resource matching (i.e., JAX-RS 2.1)
    * @tpSince RESTEasy 3.6.1
    */
   @Test
   public void testDefault() throws Exception {
      WebTarget base = client.target(generateBaseUrl(DEPLOYMENT_DEFAULT));
      Response response = base.path("/path/match").request().post(Entity.entity("entity", "text/plain"));
      Assert.assertEquals(response.getStatus(), HttpResponseCodes.SC_OK);
      Assert.assertEquals("match", response.readEntity(String.class));
      response.close();
   }
}
