package org.jboss.resteasy.embedded.test.core.basic;

import org.jboss.resteasy.plugins.server.embedded.EmbeddedJaxrsServer;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.embedded.test.EmbeddedServerTestBase;
import org.jboss.resteasy.embedded.test.core.basic.resource.ApplicationTestAExplicitApplication;
import org.jboss.resteasy.embedded.test.core.basic.resource.ApplicationTestBExplicitApplication;
import org.jboss.resteasy.embedded.test.core.basic.resource.ApplicationTestMappedApplication;
import org.jboss.resteasy.embedded.test.core.basic.resource.ApplicationTestResourceA;
import org.jboss.resteasy.embedded.test.core.basic.resource.ApplicationTestResourceB;
import org.jboss.resteasy.embedded.test.core.basic.resource.ApplicationTestScannedApplication;
import org.jboss.resteasy.embedded.test.core.basic.resource.ApplicationTestSingletonA;
import org.jboss.resteasy.embedded.test.core.basic.resource.ApplicationTestSingletonB;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.List;

import static org.jboss.resteasy.embedded.test.TestPortProvider.generateURL;

/**
 * @tpChapter Embedded Containers
 * @tpTestCaseDetails Test for usage of different types of application class definitions
 * @tpSince RESTEasy 4.1.0
 */
public class ApplicationTest extends EmbeddedServerTestBase {

   private static final String CONTENT_ERROR_MESSAGE = "Wrong content of response";

   static Client client;
   private static EmbeddedJaxrsServer server;
   private static ResteasyDeployment deployment;

   @Before
   public void setup() throws Exception
   {
      server = getServer();

      deployment = server.getDeployment();
      List<String> scannedResourceClasses = deployment.getScannedResourceClasses();
      scannedResourceClasses.add(ApplicationTestResourceA.class.getName());
      scannedResourceClasses.add(ApplicationTestSingletonA.class.getName());
      scannedResourceClasses.add(ApplicationTestResourceB.class.getName());
      scannedResourceClasses.add(ApplicationTestSingletonB.class.getName());
      server.setDeployment(deployment);

      client = ClientBuilder.newBuilder().build();
   }

   @After
   public void end() throws Exception
   {
      try
      {
         client.close();
      }
      catch (Exception e)
      {

      }
      server.stop();
   }

   /**
    * @tpTestDetails Test first application definition.  Declared ApplicationPath,
    * getClasses and getSingletons methods
    * @tpSince RESTEasy 4.1.0
    */
   @Test
   public void testExplicitA() throws Exception {

      deployment.setApplicationClass(ApplicationTestAExplicitApplication.class.getName());
      server.start();
      server.deploy();

      WebTarget base = client.target(generateURL("/a/explicit"));

      String value = base.path("resources/a").request().get(String.class);
      Assert.assertEquals(CONTENT_ERROR_MESSAGE, "a", value);

      Response response = base.path("resources/b").request().get();
      Assert.assertEquals(HttpResponseCodes.SC_NOT_FOUND, response.getStatus());

      value = base.path("singletons/a").request().get(String.class);
      Assert.assertEquals(CONTENT_ERROR_MESSAGE, "a", value);

      response = base.path("singletons/b").request().get();
      Assert.assertEquals(HttpResponseCodes.SC_NOT_FOUND, response.getStatus());
      client.close();
   }

   /**
    * @tpTestDetails Test second application definition.  Declared ApplicationPath,
    * getClasses and getSingletons methods.
    * @tpSince RESTEasy 4.1.0
    */
   @Test
   public void testExplicitB() throws Exception {

      deployment.setApplicationClass(ApplicationTestBExplicitApplication.class.getName());
      server.start();
      server.deploy();

      WebTarget base = client.target(generateURL("/b/explicit"));

      String value = base.path("resources/b").request().get(String.class);
      Assert.assertEquals(CONTENT_ERROR_MESSAGE, "b", value);

      Response response = base.path("resources/a").request().get();
      Assert.assertEquals(HttpResponseCodes.SC_NOT_FOUND, response.getStatus());

      value = base.path("singletons/b").request().get(String.class);
      Assert.assertEquals(CONTENT_ERROR_MESSAGE, "b", value);

      response = base.path("singletons/a").request().get();
      Assert.assertEquals(HttpResponseCodes.SC_NOT_FOUND, response.getStatus());
      client.close();
   }

   /**
    * @tpTestDetails Test scanned application in deployment: Declared ApplicationPath,
    * no declared getClasses and getSingletons methods.
    * @tpSince RESTEasy 4.1.0
    */
   @Test
   public void testScanned() throws Exception {

      deployment.setApplicationClass(ApplicationTestScannedApplication.class.getName());
      server.start();
      server.deploy();

      WebTarget base = client.target(generateURL("/scanned"));

      String value = base.path("resources/a").request().get(String.class);
      Assert.assertEquals(CONTENT_ERROR_MESSAGE, "a", value);

      value = base.path("resources/b").request().get(String.class);
      Assert.assertEquals(CONTENT_ERROR_MESSAGE, "b", value);

      value = base.path("singletons/a").request().get(String.class);
      Assert.assertEquals(CONTENT_ERROR_MESSAGE, "a", value);

      value = base.path("singletons/b").request().get(String.class);
      Assert.assertEquals(CONTENT_ERROR_MESSAGE, "b", value);
      client.close();
   }

   /**
    * @tpTestDetails Test scanned application in deployment: No declared ApplicationPath,
    * no declared getClasses and getSingletons methods. This application is mapped
    * to different location using setRootResourcePath.  This replaces the web.xml
    * statement <url-pattern>/mapped/*</url-pattern> in <servlet-mapping>.
    * @tpSince RESTEasy 4.1.0
    */
   @Test
   public void testMapped() throws Exception {

      deployment.setApplicationClass(ApplicationTestMappedApplication.class.getName());
      server.setRootResourcePath("/mapped");
      server.start();
      server.deploy();

      WebTarget base = client.target(generateURL("/mapped"));

      String value = base.path("resources/a").request().get(String.class);
      Assert.assertEquals(CONTENT_ERROR_MESSAGE, "a", value);

      value = base.path("resources/b").request().get(String.class);
      Assert.assertEquals(CONTENT_ERROR_MESSAGE, "b", value);

      value = base.path("singletons/a").request().get(String.class);
      Assert.assertEquals(CONTENT_ERROR_MESSAGE, "a", value);

      value = base.path("singletons/b").request().get(String.class);
      Assert.assertEquals(CONTENT_ERROR_MESSAGE, "b", value);
      client.close();
   }
}
