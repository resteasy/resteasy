package org.jboss.resteasy.test.cdi.injection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.test.cdi.injection.resource.ProviderFinalClassResource;
import org.jboss.resteasy.test.cdi.injection.resource.ProviderFinalClassStringHandler;
import org.jboss.resteasy.test.cdi.injection.resource.ProviderFinalClassStringHandlerBodyWriter;
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
 *                    declared final.
 * @tpSince RESTEasy 3.7
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ProviderFinalClassTest {

   protected static final Logger logger = LogManager.getLogger(
      ProviderFinalClassTest.class.getName());

   Client client;

   @Deployment
   public static Archive<?> deploy() {
      JavaArchive jar = ShrinkWrap.create(JavaArchive.class, "SingleLibrary.jar");
      jar.addClasses(ProviderFinalClassStringHandler.class,
         ProviderFinalClassStringHandlerBodyWriter.class);

      WebArchive war = ShrinkWrap.create(WebArchive.class,
         ProviderFinalClassTest.class.getSimpleName()+".war");
      war.addClass(ProviderFinalClassResource.class);
      war.addAsWebInfResource(ProviderFinalClassTest.class.getPackage(),
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
   public void testAppcliationScope() throws Exception {
      String url = PortProviderUtil.generateURL("/new/a",
         ProviderFinalClassTest.class.getSimpleName());
      WebTarget base = client.target(url);

      Response response = base.request().get();
      assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      response.close();
   }
}
