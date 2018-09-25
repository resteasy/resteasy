package org.jboss.resteasy.test.core.basic;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import javax.ws.rs.client.ClientBuilder;
import org.jboss.resteasy.test.core.basic.resource.AppConfigApplication;
import org.jboss.resteasy.test.core.basic.resource.AppConfigResources;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * @tpSubChapter Configuration
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for resource and provider defined in one class together.
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class AppConfigTest {

   private static ResteasyClient client;

   @Deployment
   public static Archive<?> deploySimpleResource() {
      WebArchive war = ShrinkWrap.create(WebArchive.class, AppConfigTest.class.getSimpleName() + ".war");
      war.addClass(AppConfigResources.class);
      war.addClass(AppConfigApplication.class);
      war.addAsWebInfResource(AppConfigTest.class.getPackage(), "AppConfigWeb.xml", "web.xml");
      return war;
   }

   private String generateURL(String path) {
      return PortProviderUtil.generateURL(path, AppConfigTest.class.getSimpleName());
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
    * @tpTestDetails Test for apache client
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void apacheClient() throws Exception {
      CloseableHttpClient httpclient = HttpClients.createDefault();
      HttpGet httpGet = new HttpGet(generateURL("/my"));
      CloseableHttpResponse response1 = httpclient.execute(httpGet);

      try {
         Assert.assertEquals(HttpResponseCodes.SC_OK, response1.getStatusLine().getStatusCode());
         Assert.assertEquals("\"hello\"", TestUtil.readString(response1.getEntity().getContent()));
      } finally {
         response1.close();
      }
   }
}
