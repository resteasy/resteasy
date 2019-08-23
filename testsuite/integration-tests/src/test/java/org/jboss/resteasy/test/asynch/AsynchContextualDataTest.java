package org.jboss.resteasy.test.asynch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import javax.ws.rs.client.ClientBuilder;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.test.asynch.resource.AsynchContextualDataProduct;
import org.jboss.resteasy.test.asynch.resource.AsynchContextualDataResource;
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
 * @tpSubChapter Asynchronous RESTEasy: RESTEASY-1225
 * @tpChapter Integration tests
 * @tpTestCaseDetails Tests that Providers context is not discarded prematurely
 * @tpSince RESTEasy 3.1.1.Final
 */
@RunWith(Arquillian.class)
@RunAsClient
public class AsynchContextualDataTest {

   public static Client client;

   @Deployment()
   public static Archive<?> deploy() {
      WebArchive war = TestUtil.prepareArchive(AsynchContextualDataTest.class.getSimpleName());
      war.addClass(AsynchContextualDataProduct.class);
      List<Class<?>> singletons = new ArrayList<Class<?>>();
      singletons.add(AsynchContextualDataResource.class);
      Map<String, String> contextParam = new HashMap<>();
      contextParam.put(ResteasyContextParameters.RESTEASY_PREFER_JACKSON_OVER_JSONB, "true");
      return TestUtil.finishContainerPrepare(war, contextParam, singletons);
   }

   private String generateURL(String path) {
      return PortProviderUtil.generateURL(path, AsynchContextualDataTest.class.getSimpleName());
   }

   @BeforeClass
   public static void initClient() {
      client = ((ResteasyClientBuilder)ClientBuilder.newBuilder()).connectionPoolSize(10).build();
   }

   @AfterClass
   public static void closeClient() {
      client.close();
   }

   /**
    * @tpTestDetails Test stack handling of context data map
    * @tpSince RESTEasy 3.1.1.Final
    */
   @Test
   public void testContextualData() throws Exception {
      String id = "334";

      //Start the request to the waiting endpoint, but don't block
      WebTarget target = client.target(generateURL("/products/wait/" + id));
      Future<Response> response = target.request().async().get();

      //Let the server set the resumable field, timing thing!
      Thread.sleep(3000);

      //While the other request is waiting, fire off a request to /res/ which will allow the other request to complete
      WebTarget resTarget = client.target(generateURL("/products/res/" + id));
      Response resResponse = resTarget.request().get();

      String entity = response.get().readEntity(String.class);
      String resEntity = resResponse.readEntity(String.class);

      Assert.assertEquals(200, response.get().getStatus());
      Assert.assertEquals("{\"name\":\"Iphone\",\"id\":" + id + "}", entity);

      Assert.assertEquals(200, resResponse.getStatus());
      Assert.assertEquals("{\"name\":\"Nexus 7\",\"id\":" + id + "}", resEntity);

      response.get().close();
      resResponse.close();
   }
}
