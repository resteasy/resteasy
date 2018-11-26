package org.jboss.resteasy.test.core.basic;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.core.basic.resource.NoApplicationSubclassResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Configuration
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for discovering root resource classes when no Application subclass is present
 * @tpSince RESTEasy 3.6.2.Final
 */
@RunWith(Arquillian.class)
@RunAsClient
public class NoApplicationSubclassTest {

   private static ResteasyClient client;

   @Deployment
   public static Archive<?> deploy() {
      WebArchive war = ShrinkWrap.create(WebArchive.class, NoApplicationSubclassTest.class.getSimpleName() + ".war");
      war.addClasses(NoApplicationSubclassResource.class);
      war.addAsWebInfResource(NoApplicationSubclassTest.class.getPackage(), "NoApplicationSubclassWeb.xml", "web.xml");
      return war;
   }

   private String generateURL(String path) {
      return PortProviderUtil.generateURL(path, NoApplicationSubclassTest.class.getSimpleName());
   }

   @Before
   public void setup() {
      client = (ResteasyClient) ClientBuilder.newClient();
   }

   @After
   public void after() throws Exception {
      client.close();
   }

   /**
    * @tpTestDetails Check if resource is present in application
    * @tpSince RESTEasy 3.6.2.Final
    */
   @Test
   public void testResource() {
      Response response = client.target(generateURL("/myresources/hello")).request().get();
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertEquals("Wrong content of response", "hello world", response.readEntity(String.class));
      response.close();
   }
}
