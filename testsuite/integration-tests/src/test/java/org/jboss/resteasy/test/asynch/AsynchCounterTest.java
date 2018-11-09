package org.jboss.resteasy.test.asynch;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.category.NotForForwardCompatibility;
import org.jboss.resteasy.test.asynch.resource.AsynchCounterResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Asynchronous RESTEasy
 * @tpChapter Integration tests
 * @tpTestCaseDetails Tests use of SecureRandom to generate location job ids, RESTEASY-1483
 * @tpSince RESTEasy 3.1.0.Final
 */
@RunWith(Arquillian.class)
@RunAsClient
@Category({NotForForwardCompatibility.class})
public class AsynchCounterTest {

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
      WebArchive war = TestUtil.prepareArchive(AsynchCounterTest.class.getSimpleName());
      Map<String, String> contextParam = new HashMap<>();
      contextParam.put("resteasy.async.job.service.enabled", "true");
      contextParam.put("resteasy.secure.random.max.use", "2");
      return TestUtil.finishContainerPrepare(war, contextParam, AsynchCounterResource.class);
   }

   private String generateURL(String path) {
      return PortProviderUtil.generateURL(path, AsynchCounterTest.class.getSimpleName());
   }

   /**
    * @tpTestDetails Test that job ids are no longer consecutive
    * @tpInfo RESTEASY-1483
    * @tpSince RESTEasy 3.1.0.Final
    */
   @Test
   public void testAsynchCounter() throws Exception {

      Response response = client.target(generateURL("?asynch=true")).request().get();
      Assert.assertEquals(HttpServletResponse.SC_ACCEPTED, response.getStatus());
      String jobUrl = response.getHeaderString(HttpHeaders.LOCATION);
      int job1 = Integer.parseInt(jobUrl.substring(jobUrl.lastIndexOf('-') + 1));
      response.close();
      response = client.target(generateURL("?asynch=true")).request().get();
      Assert.assertEquals(HttpServletResponse.SC_ACCEPTED, response.getStatus());
      jobUrl = response.getHeaderString(HttpHeaders.LOCATION);
      int job2 = Integer.parseInt(jobUrl.substring(jobUrl.lastIndexOf('-') + 1));
      Assert.assertTrue(job2 != job1 + 1);
      response.close();
   }
}
