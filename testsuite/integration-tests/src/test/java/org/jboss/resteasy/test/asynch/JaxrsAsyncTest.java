package org.jboss.resteasy.test.asynch;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.asynch.resource.JaxrsAsyncResource;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Asynchronous RESTEasy
 * @tpChapter Integration tests
 * @tpTestCaseDetails Basic asynchronous test. Resource creates new threads.
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class JaxrsAsyncTest {

   @Deployment
   public static Archive<?> deploy() {
      WebArchive war = TestUtil.prepareArchive(JaxrsAsyncTest.class.getSimpleName());
      return TestUtil.finishContainerPrepare(war, null, JaxrsAsyncResource.class);
   }

   private static String generateURL(String path) {
      return PortProviderUtil.generateURL(path, JaxrsAsyncTest.class.getSimpleName());
   }

   /**
    * @tpTestDetails Correct response excepted.
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testSuccess() throws Exception {
      Client client = ClientBuilder.newClient();
      Response response = client.target(generateURL("")).request().get();
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("Wrong response", "hello", response.readEntity(String.class));
      response.close();
      client.close();
   }

   /**
    * @tpTestDetails Timeout exception should be thrown.
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testTimeout() throws Exception {
      Client client = ClientBuilder.newClient();
      Response response = client.target(generateURL("/timeout")).request().get();
      Assert.assertEquals(503, response.getStatus());
      response.close();
      client.close();
   }

   /**
    * @tpTestDetails Negative timeout value is set to response in end-point. Regression test for JBEAP-4695.
    * @tpSince RESTEasy 3.0.17
    */
   @Test
   public void testNegativeTimeout() throws Exception {
      Client client = ClientBuilder.newClient();
      Response response = client.target(generateURL("/negative")).request().get();
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertEquals("Wrong response", "hello", response.readEntity(String.class));
      response.close();
      client.close();
   }

   /**
    * @tpTestDetails Zero timeout value is set to response in end-point. Regression test for JBEAP-4695.
    * @tpSince RESTEasy 3.0.17
    */
   @Test
   public void testZeroTimeout() throws Exception {
      Client client = ClientBuilder.newClient();
      Response response = client.target(generateURL("/zero")).request().get();
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertEquals("Wrong response", "hello", response.readEntity(String.class));
      response.close();
      client.close();
   }
}
