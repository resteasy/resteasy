package org.jboss.resteasy.test.interceptor;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.resteasy.test.interceptor.resource.AddDynamicFeature;
import org.jboss.resteasy.test.interceptor.resource.DynamicFeatureResource;
import org.jboss.resteasy.test.interceptor.resource.GreetingInterceptor;
import org.jboss.resteasy.utils.LogCounter;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import static org.jboss.resteasy.test.ContainerConstants.DEFAULT_CONTAINER_QUALIFIER;

/**
 * @tpSubChapter Resteasy-interceptor
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for DynamicFeature
 * @tpSince RESTEasy 3.8.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class DynamicFeatureTest {

   private static Client client;
   private static LogCounter logCounter;

   @Deployment
   public static Archive<?> deploy() {
      // LogCounter needs to be created before deployment because it can only count logs written after its creation
      logCounter = new LogCounter("This should be happening exactly once", false, DEFAULT_CONTAINER_QUALIFIER);

      WebArchive war = TestUtil.prepareArchive(DynamicFeatureTest.class.getSimpleName());
      war.addClasses(GreetingInterceptor.class);
      return TestUtil.finishContainerPrepare(war, null, DynamicFeatureResource.class, AddDynamicFeature.class);
   }

   @BeforeClass
   public static void setup() {
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void cleanup() {
      client.close();
   }

   /**
    * @tpTestDetails This test checks that:
    * - dynamic features are processed at application deployment time
    * - dynamic features for filters and interceptors are resolved once for each resource method
    * @tpSince RESTEasy 3.8.0
    */
   @Test
   @InSequence(1)
   public void testDynamicFeatureProcessing() {
      int counter = logCounter.count();
      Assert.assertNotEquals("Dynamic features were not processed at application deployment time", 0, counter);
      Assert.assertEquals("Dynamic features for filters and interceptors should be resolved only once for each resource method", 1, counter);
   }

   /**
    * @tpTestDetails This test checks that dynamic feature works by checking that interceptor was used.
    * @tpSince RESTEasy 3.8.0
    */
   @Test
   @InSequence(2)
   public void testInterceptor() {
      WebTarget target = client.target(PortProviderUtil.generateURL("/dynamic-feature/hello", DynamicFeatureTest.class.getSimpleName()));
      Entity<String> entity = Entity.entity( "Tomas", MediaType.TEXT_PLAIN);
      String response = target.request().post(entity, String.class);
      Assert.assertEquals("Hello Tomas !", response);
   }

}
