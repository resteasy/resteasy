package org.jboss.resteasy.test.core.basic;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import javax.ws.rs.client.ClientBuilder;
import org.jboss.resteasy.test.core.basic.resource.ApplicationFilterCustomer;
import org.jboss.resteasy.test.core.basic.resource.ApplicationFilterCustomerResource;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.UrlAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @tpSubChapter Configuration
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-541
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ApplicationFilterTest {

   private static ResteasyClient client;

   @Deployment
   public static Archive<?> deploySimpleResource() {
      WebArchive war = TestUtil.prepareArchive(ApplicationFilterTest.class.getSimpleName());
      war.addAsWebInfResource(ApplicationFilterTest.class.getPackage(), "ApplicationFilterWeb.xml", "web.xml");
      war.addClasses(ApplicationFilterCustomer.class, ApplicationFilterCustomerResource.class);
      war.add(new UrlAsset(ApplicationFilterTest.class.getResource("ApplicationFilter.html")), "foo.html");
      List<Class<?>> singletons = new ArrayList<>();
      singletons.add(ApplicationFilterCustomerResource.class);
      return TestUtil.finishContainerPrepare(war, null, singletons, (Class<?>[]) null);
   }

   private String generateURL(String path) {
      return PortProviderUtil.generateURL(path, ApplicationFilterTest.class.getSimpleName());
   }

   @Before
   public void setup() {
      client = (ResteasyClient)ClientBuilder.newClient();
   }

   @After
   public void after() throws Exception {
      client.close();
   }

   /**
    * @tpTestDetails Test for not founded method
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testMethodNotFound() throws Exception {
      String newCustomer = "<customer>"
            + "<first-name>Bill</first-name>"
            + "<last-name>Burke</last-name>"
            + "<street>256 Clarendon Street</street>"
            + "<city>Boston</city>"
            + "<state>MA</state>"
            + "<zip>02115</zip>"
            + "<country>USA</country>"
            + "</customer>";
      Response response = client.target(generateURL("/customers")).request().put(Entity.entity(newCustomer, "application/xml"));
      Assert.assertEquals(405, response.getStatus());
   }

   /**
    * @tpTestDetails Test for getting html file that is not processed by resteasy
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testStaticResource() throws Exception {
      String response = client.target(generateURL("/foo.html")).request().get(String.class);
      Assert.assertNotNull(response);
      Assert.assertTrue(response.indexOf("hello world") > -1);
   }

   /**
    * @tpTestDetails Test common application usage
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testCustomerResource() throws Exception {
      // Create a new customer
      String newCustomer = "<customer>"
            + "<first-name>Bill</first-name>"
            + "<last-name>Burke</last-name>"
            + "<street>256 Clarendon Street</street>"
            + "<city>Boston</city>"
            + "<state>MA</state>"
            + "<zip>02115</zip>"
            + "<country>USA</country>"
            + "</customer>";

      URL postUrl = new URL(generateURL("/customers"));
      HttpURLConnection connection = (HttpURLConnection) postUrl.openConnection();
      connection.setDoOutput(true);
      connection.setInstanceFollowRedirects(false);
      connection.setRequestMethod("POST");
      connection.setRequestProperty("Content-Type", "application/xml");
      OutputStream os = connection.getOutputStream();
      os.write(newCustomer.getBytes());
      os.flush();
      Assert.assertEquals(HttpResponseCodes.SC_CREATED, connection.getResponseCode());
      connection.disconnect();


      // Get the new customer
      URL getUrl = new URL(generateURL("/customers/1"));
      connection = (HttpURLConnection) getUrl.openConnection();
      connection.setRequestMethod("GET");

      BufferedReader reader = new BufferedReader(new
            InputStreamReader(connection.getInputStream()));

      String line = reader.readLine();
      while (line != null) {
         line = reader.readLine();
      }
      Assert.assertEquals(HttpResponseCodes.SC_OK, connection.getResponseCode());
      connection.disconnect();

      // Update the new customer.  Change Bill's name to William
      String updateCustomer = "<customer>"
            + "<first-name>William</first-name>"
            + "<last-name>Burke</last-name>"
            + "<street>256 Clarendon Street</street>"
            + "<city>Boston</city>"
            + "<state>MA</state>"
            + "<zip>02115</zip>"
            + "<country>USA</country>"
            + "</customer>";
      connection = (HttpURLConnection) getUrl.openConnection();
      connection.setDoOutput(true);
      connection.setRequestMethod("PUT");
      connection.setRequestProperty("Content-Type", "application/xml");
      os = connection.getOutputStream();
      os.write(updateCustomer.getBytes());
      os.flush();
      Assert.assertEquals(HttpResponseCodes.SC_NO_CONTENT, connection.getResponseCode());
      connection.disconnect();

      // Show the update
      connection = (HttpURLConnection) getUrl.openConnection();
      connection.setRequestMethod("GET");

      reader = new BufferedReader(new
            InputStreamReader(connection.getInputStream()));

      line = reader.readLine();
      while (line != null) {
         line = reader.readLine();
      }
      Assert.assertEquals(HttpResponseCodes.SC_OK, connection.getResponseCode());
      connection.disconnect();
   }
}
