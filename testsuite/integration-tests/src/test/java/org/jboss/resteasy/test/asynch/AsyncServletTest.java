package org.jboss.resteasy.test.asynch;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import javax.ws.rs.client.ClientBuilder;
import org.jboss.resteasy.test.asynch.resource.AsyncServletResource;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.Response;


/**
 * @tpSubChapter Asynchronous RESTEasy
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for asyncHttpServlet module
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class AsyncServletTest {

   static ResteasyClient client;

   @Before
   public void init() {
      client = (ResteasyClient)ClientBuilder.newClient();
   }

   @After
   public void after() throws Exception {
      client.close();
   }

   @Deployment
   public static Archive<?> createTestArchive() {
      WebArchive war =  TestUtil.prepareArchive(AsyncServletTest.class.getSimpleName());
      return TestUtil.finishContainerPrepare(war, null, AsyncServletResource.class);
   }

   private String generateURL(String path) {
      return PortProviderUtil.generateURL(path, AsyncServletTest.class.getSimpleName());
   }

   /**
    * @tpTestDetails Test for correct response
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testAsync() throws Exception {
      Response response = client.target(generateURL("/async")).request().get();
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertEquals("Wrong response content", "hello", response.readEntity(String.class));
   }

   /**
    * @tpTestDetails Service unavailable test
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testTimeout() throws Exception {
      Response response = client.target(generateURL("/async/timeout")).request().get();
      Assert.assertEquals(HttpResponseCodes.SC_SERVICE_UNAVAILABLE, response.getStatus());
   }
}
