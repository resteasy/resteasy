package org.jboss.resteasy.test.microprofile.config;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.microprofile.config.resource.OptionalConfigPropertyInjectionResource;
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
 * @tpSubChapter MicroProfile Config
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for injection of optional MicroProfile Config properties.
 * @tpSince RESTEasy 4.6.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class OptionalConfigPropertyInjectionTest
{

   private static Client client;

   @Deployment
   public static Archive<?> deploy()
   {
      WebArchive war = TestUtil.prepareArchive(OptionalConfigPropertyInjectionTest.class.getSimpleName())
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
      return TestUtil.finishContainerPrepare(war, null, OptionalConfigPropertyInjectionResource.class);
   }

   @BeforeClass
   public static void setup()
   {
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void cleanup()
   {
      client.close();
   }

   /**
    * @tpTestDetails This test checks injection of optional config properties when:
    * - optional property does not exist
    * - optional property exists
    * @tpSince RESTEasy 4.6.0
    */
   @Test
   public void testOptionalPropertiesInjection() {

      String missingOptionalPropertyValue = client.target(generateEndpointURL(OptionalConfigPropertyInjectionResource.MISSING_OPTIONAL_PROPERTY_PATH))
            .request(MediaType.TEXT_PLAIN_TYPE)
            .get(String.class);
      Assert.assertNull(missingOptionalPropertyValue);

      String presentOptionalPropertyValue = client.target(generateEndpointURL(OptionalConfigPropertyInjectionResource.PRESENT_OPTIONAL_PROPERTY_PATH))
            .request(MediaType.TEXT_PLAIN_TYPE)
            .get(String.class);
      Assert.assertEquals(OptionalConfigPropertyInjectionResource.OPTIONAL_PROPERTY_VALUE, presentOptionalPropertyValue);
   }

   private String generateEndpointURL(String path) {
     return PortProviderUtil.generateURL(path, OptionalConfigPropertyInjectionTest.class.getSimpleName());
   }

}
