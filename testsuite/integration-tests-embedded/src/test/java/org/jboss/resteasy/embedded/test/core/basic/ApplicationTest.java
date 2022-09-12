package org.jboss.resteasy.embedded.test.core.basic;

import jakarta.ws.rs.SeBootstrap;
import org.jboss.jandex.Index;
import org.jboss.resteasy.core.se.ConfigurationOption;
import org.jboss.resteasy.embedded.test.AbstractBootstrapTest;
import org.jboss.resteasy.embedded.test.core.basic.resource.ApplicationTestResourceA;
import org.jboss.resteasy.embedded.test.core.basic.resource.ApplicationTestResourceB;
import org.jboss.resteasy.embedded.test.core.basic.resource.ApplicationTestSingletonA;
import org.jboss.resteasy.embedded.test.core.basic.resource.ApplicationTestSingletonB;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.embedded.test.core.basic.resource.ApplicationTestAExplicitApplication;
import org.jboss.resteasy.embedded.test.core.basic.resource.ApplicationTestBExplicitApplication;
import org.jboss.resteasy.embedded.test.core.basic.resource.ApplicationTestMappedApplication;
import org.jboss.resteasy.embedded.test.core.basic.resource.ApplicationTestScannedApplication;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @tpChapter Embedded Containers
 * @tpTestCaseDetails Test for usage of different types of application class definitions
 * @tpSince RESTEasy 4.1.0
 */
public class ApplicationTest extends AbstractBootstrapTest {

   private static final String CONTENT_ERROR_MESSAGE = "Wrong content of response";
   private static Index INDEX;

   @BeforeClass
   public static void configureIndex() throws Exception {
      INDEX = Index.of(ApplicationTestResourceA.class,
              ApplicationTestResourceB.class,
              ApplicationTestSingletonA.class,
              ApplicationTestSingletonB.class);
   }

   /**
    * @tpTestDetails Test first application definition.  Declared ApplicationPath,
    * getClasses and getSingletons methods
    * @tpSince RESTEasy 4.1.0
    */
   @Test
   public void testExplicitA() throws Exception {
      start(new ApplicationTestAExplicitApplication());

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
      start(new ApplicationTestBExplicitApplication());

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
      start(new ApplicationTestScannedApplication(), SeBootstrap.Configuration.builder()
              .property(ConfigurationOption.JANDEX_INDEX.key(), INDEX)
              .build());

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

      start(new ApplicationTestMappedApplication(), SeBootstrap.Configuration.builder()
              .property(ConfigurationOption.JANDEX_INDEX.key(), INDEX)
              .rootPath("/mapped")
              .build());

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
