package org.jboss.resteasy.test.resource.basic;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.resource.basic.resource.ClassLevelMediaTypeResource;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
@RunAsClient
public class ClassLevelMediaTypeTest {

   private static Client client;

   @Deployment
   public static Archive<?> deploy() {
      WebArchive war = TestUtil.prepareArchive(ClassLevelMediaTypeTest.class.getSimpleName());
      return TestUtil.finishContainerPrepare(war, null, ClassLevelMediaTypeResource.class);
   }

   @BeforeClass
   public static void init() {
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void after() throws Exception {
      client.close();
      client = null;
   }

   @Test
   public void testApplicationJsonMediaType() {
      WebTarget base = client.target(generateURL("/test"));
      try {
         Response response = base.request().get();
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
         String body = response.readEntity(String.class);
         Assert.assertEquals("application/json", response.getHeaderString("Content-Type"));
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   private String generateURL(String path) {
      return PortProviderUtil.generateURL(path, ClassLevelMediaTypeTest.class.getSimpleName());
   }
}
