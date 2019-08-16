package org.jboss.resteasy.test.cdi.injection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.cdi.injection.resource.FinalMethodSuperclass;
import org.jboss.resteasy.test.cdi.injection.resource.NonProxyableProviderResource;
import org.jboss.resteasy.test.cdi.injection.resource.ProviderFinalClassStringHandler;
import org.jboss.resteasy.test.cdi.injection.resource.ProviderFinalClassStringHandlerBodyWriter;
import org.jboss.resteasy.test.cdi.injection.resource.ProviderFinalInheritedMethodStringHandler;
import org.jboss.resteasy.test.cdi.injection.resource.ProviderFinalInheritedMethodStringHandlerBodyWriter;
import org.jboss.resteasy.test.cdi.injection.resource.ProviderOneArgConstructorStringHandler;
import org.jboss.resteasy.test.cdi.injection.resource.ProviderOneArgConstructorStringHandlerBodyWriter;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

/**
 * @tpSubChapter CDI
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for RESTEASY-1015
 *                    Test that proxy class is not created for provider class
 *                    that cannot be proxied.
 * @tpSince RESTEasy 3.7
 */
@RunWith(Arquillian.class)
@RunAsClient
public class NonProxyableProviderTest {

   protected static final Logger logger = LogManager.getLogger(
      NonProxyableProviderTest.class.getName());

   Client client;

   @Deployment
   public static Archive<?> deploy() {
      JavaArchive jar = ShrinkWrap.create(JavaArchive.class, "SingleLibrary.jar");
      jar.addClasses(ProviderFinalClassStringHandler.class,
         ProviderFinalClassStringHandlerBodyWriter.class,
         ProviderFinalInheritedMethodStringHandler.class,
         ProviderFinalInheritedMethodStringHandlerBodyWriter.class,
         FinalMethodSuperclass.class,
         ProviderOneArgConstructorStringHandler.class,
         ProviderOneArgConstructorStringHandlerBodyWriter.class);

      WebArchive war = ShrinkWrap.create(WebArchive.class,
         NonProxyableProviderTest.class.getSimpleName()+".war");
      war.addClass(NonProxyableProviderResource.class);
      war.addAsWebInfResource(
          NonProxyableProviderTest.class.getPackage(),
         "ProviderFinalClass_web.xml", "web.xml");
      war.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
      war.addAsLibrary(jar);
      return war;
   }

   @Before
   public void init() {
      client = ClientBuilder.newClient();
   }

   @After
   public void close() {
      client.close();
   }

   /**
    * @tpTestDetails Test CDI does not create proxy class for provider bean declared final
    * @tpSince RESTEasy 3.7
    */
   @Test
   public void testFinalProvider() throws Exception {
      test("a");
   }

   /**
    * @tpTestDetails Test CDI does not create proxy class for provider bean with an inherited final method
    * @tpSince RESTEasy 3.7
    */
   @Test
   public void testInheritedFinalMethodProvider() throws Exception {
      test("b");
   }

   /**
    * @tpTestDetails Test CDI does not create proxy class for provider bean without a non-private no-arg constructor
    * @tpSince RESTEasy 3.7
    */
   @Test
   public void testOneArgConstructorProvider() throws Exception {
      test("c");
   }

   private void test(String subpath) {
      String url = PortProviderUtil.generateURL("/new/" + subpath,
            NonProxyableProviderTest.class.getSimpleName());
      WebTarget base = client.target(url);

      Response response = base.request().get();
      assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      response.close();
   }
}
