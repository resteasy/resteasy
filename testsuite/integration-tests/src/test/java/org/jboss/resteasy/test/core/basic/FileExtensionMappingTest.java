package org.jboss.resteasy.test.core.basic;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.core.basic.resource.FileExtensionMappingApplication;
import org.jboss.resteasy.test.core.basic.resource.FileExtensionMappingResource;
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
 * @tpSubChapter MediaType
 * @tpChapter Integration tests
 * @tpTestCaseDetails Mapping file extensions to media types
 * @tpSince RESTEasy 3.0.20
 */
@RunWith(Arquillian.class)
@RunAsClient
public class FileExtensionMappingTest
{
   static Client client;

   @BeforeClass
   public static void setup() {
       client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void close() {
       client.close();
   }

   @Deployment
   public static Archive<?> deploy() {
       WebArchive war = TestUtil.prepareArchive(FileExtensionMappingTest.class.getSimpleName());
       war.addClass(FileExtensionMappingApplication.class);
       war.addAsWebInfResource(FileExtensionMappingTest.class.getPackage(), "FileExtensionMapping.xml", "web.xml");
       Archive<?> archive = TestUtil.finishContainerPrepare(war, null, FileExtensionMappingResource.class);
       return archive;
   }

   private String generateURL(String path) {
       return PortProviderUtil.generateURL(path, FileExtensionMappingTest.class.getSimpleName());
   }
   
   /**
    * @tpTestDetails Map suffix .txt to Accept: text/plain
    * @tpSince RESTEasy 3.0.20
    */
   @Test
   public void testFileExtensionMappingPlain() throws Exception {
      Response response = client.target(generateURL("/test.txt")).queryParam("query", "whosOnFirst").request().get();
      String entity = response.readEntity(String.class);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("plain: whosOnFirst", entity);
   }
   
   /**
    * @tpTestDetails Map suffix .html to Accept: text/html
    * @tpSince RESTEasy 3.0.20
    */
   @Test
   public void testFileExtensionMappingHtml() throws Exception
   {
      Response response = client.target(generateURL("/test.html")).queryParam("query", "whosOnFirst").request().get();
      String entity = response.readEntity(String.class);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("html: whosOnFirst", entity);
   }
}
