package org.jboss.resteasy.test.providers.disabled;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.providers.disabled.resource.DisabledProvidersApplication1;
import org.jboss.resteasy.test.providers.disabled.resource.DisabledProvidersApplication2;
import org.jboss.resteasy.test.providers.disabled.resource.DisabledProvidersApplication3;
import org.jboss.resteasy.test.providers.disabled.resource.DisabledProvidersResource;
import org.jboss.resteasy.test.providers.disabled.resource.Foo;
import org.jboss.resteasy.test.providers.disabled.resource.FooReaderWriter;
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
 * @tpSubChapter Disabled providers: context parameter "resteasy.disable.providers" is used to disable providers.
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression tests for RESTEASY-1510
 * @tpSince RESTEasy 3.10.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class DisabledProvidersTest {

   static Client client;

   @Deployment(name = "enabled")
   public static Archive<?> enabled() {
      WebArchive war = TestUtil.prepareArchive(DisabledProvidersTest.class.getSimpleName() + "_enabled");
      war.addClasses(Foo.class);
      war = (WebArchive) TestUtil.finishContainerPrepare(war, null, FooReaderWriter.class, DisabledProvidersResource.class);
      return war;
   }

   @Deployment(name = "disabledApplicationClassProviders")
   public static Archive<?> actualProviders() {
      WebArchive war = TestUtil.prepareArchiveWithApplication(DisabledProvidersTest.class.getSimpleName() + "_disabled_application_class_providers", DisabledProvidersApplication1.class);
      war.addClass(Foo.class);
      war.addClass(FooReaderWriter.class);
      war.addClass(DisabledProvidersResource.class);
      war.addAsWebInfResource(DisabledProvidersTest.class.getPackage(), "web_disabled.xml", "web.xml");
      return war;
   }

   @Deployment(name = "disabledApplicationSingletonProviders")
   public static Archive<?> applicationSingletonProviders() {
      WebArchive war = TestUtil.prepareArchiveWithApplication(DisabledProvidersTest.class.getSimpleName() + "_disabled_application_singleton_providers", DisabledProvidersApplication2.class);
      war.addClass(Foo.class);
      war.addClass(FooReaderWriter.class);
      war.addClass(DisabledProvidersResource.class);
      war.addAsWebInfResource(DisabledProvidersTest.class.getPackage(), "web_disabled.xml", "web.xml");
      return war;
   }

   @Deployment(name = "disabledScannedProviders")
   public static Archive<?> scannedProviders() {
      WebArchive war = TestUtil.prepareArchiveWithApplication(DisabledProvidersTest.class.getSimpleName() + "_disabled_scanned_providers", DisabledProvidersApplication3.class);
      war.addClass(Foo.class);
      war.addClass(FooReaderWriter.class);
      war.addClass(DisabledProvidersResource.class);
      war.addAsWebInfResource(DisabledProvidersTest.class.getPackage(), "web_disabled.xml", "web.xml");
      return war;
   }

   @Deployment(name = "disabledConfiguredProviders")
   public static Archive<?> configuredProviders() {
      WebArchive war = TestUtil.prepareArchive(DisabledProvidersTest.class.getSimpleName() + "_disabled_configured_providers");
      war.addClass(Foo.class);
      war.addClass(FooReaderWriter.class);
      war.addClass(DisabledProvidersResource.class);
      war.addAsWebInfResource(DisabledProvidersTest.class.getPackage(), "web_disabled_with_providers.xml", "web.xml");
      war = (WebArchive) TestUtil.finishContainerPrepare(war, null, DisabledProvidersResource.class);
      return war;
   }

   @BeforeClass
   public static void init() {
      client = ClientBuilder.newClient();
      client.register(FooReaderWriter.class);
   }

   @AfterClass
   public static void close() {
      client.close();
   }

   private String generateURL(String path, String suffix) {
      return PortProviderUtil.generateURL(path, DisabledProvidersTest.class.getSimpleName() + suffix);
   }

   /**
    * @tpTestDetails Regression test for RESTEASY-1510
    *                Nothing is disabled in this case.
    *
    * @tpSince RESTEasy 3.10.0
    */
   @Test
   public void testEnabled() throws Exception {
      Response response = client.target(generateURL("/foo", "_enabled")).request().get();
      Assert.assertEquals(200, response.getStatus());
      Foo foo = response.readEntity(Foo.class);
      Assert.assertEquals("bar", foo.getS());
   }

   /**
    * @tpTestDetails Regression test for RESTEASY-1510
    *
    * In deployment "disabledApplicationClassProviders", providers are derived
    * in ResteasyDeployment from Application.getClasses().
    *
    * @tpSince RESTEasy 3.10.0
    */
   @Test
   public void testApplicationClassProvidersDisabled() throws Exception {
      Response response = client.target(generateURL("/foo", "_disabled_application_class_providers")).request().accept("application/foo").get();
      Assert.assertEquals(500, response.getStatus());
      String message = response.readEntity(String.class);
      Assert.assertTrue(message.contains("Could not find MessageBodyWriter"));
   }

   /**
    * @tpTestDetails Regression test for RESTEASY-1510
    *
    * In deployment "disabledApplicationSingletonProviders", providers are derived
    * in ResteasyDeployment from Application.getSingletons().
    *
    * @tpSince RESTEasy 3.10.0
    */
   @Test
   public void testApplicationSingletonProvidersDisabled() throws Exception {
      Response response = client.target(generateURL("/foo", "_disabled_application_singleton_providers")).request().accept("application/foo").get();
      Assert.assertEquals(500, response.getStatus());
      String message = response.readEntity(String.class);
      Assert.assertTrue(message.contains("Could not find MessageBodyWriter"));
   }

   /**
    * @tpTestDetails Regression test for RESTEASY-1510
    *
    * In deployment "disabledScannedProviders", Application.getClasses() and Application.getSingletons()
    * return empty sets, and so wildfly-jaxrs scans available classes looking for providers. The names of
    * those providers are passed to RESTEasy in the web context parameter "resteasy.scanned.providers".
    *
    * @tpSince RESTEasy 3.10.0
    */
   @Test
   public void testScannedProvidersDisabled() throws Exception {
      Response response = client.target(generateURL("/foo", "_disabled_scanned_providers")).request().get();
      Assert.assertEquals(500, response.getStatus());
      String message = response.readEntity(String.class);
      Assert.assertTrue(message.contains("Could not find MessageBodyWriter"));
   }

   /**
    * @tpTestDetails Regression test for RESTEASY-1510
    *
    * In deployment "disabledConfiguredProviders", a list of providers is passed to RESTEasy
    * in the web context parameter "resteasy.providers".
    *
    * @tpSince RESTEasy 3.10.0
    */
   @Test
   public void testConfiguredProvidersDisabled() throws Exception {
      Response response = client.target(generateURL("/foo", "_disabled_configured_providers")).request().get();
      Assert.assertEquals(500, response.getStatus());
      String message = response.readEntity(String.class);
      Assert.assertTrue(message.contains("Could not find MessageBodyWriter"));
   }

   /**
    * @tpTestDetails Regression test for RESTEASY-1510
    *
    * This test uses deployment "disabledConfiguredProviders", in which web.xml has uses context parameter
    * "resteasy.disable.providers" to disable builtin provider StringTextStar.
    *
    * @tpSince RESTEasy 3.10.0
    */
   @Test
   public void testBuiltinProviderDisabled() throws Exception {
      Response response = client.target(generateURL("/string", "_disabled_application_class_providers")).request().get();
      Assert.assertEquals(500, response.getStatus());
      String message = response.readEntity(String.class);
      Assert.assertTrue(message.contains("Could not find MessageBodyWriter"));
   }
}
