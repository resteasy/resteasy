package org.jboss.resteasy.test.core.basic;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import javax.ws.rs.client.ClientBuilder;
import org.jboss.resteasy.test.core.basic.resource.ProviderInjectionSimpleMessageBodyWriter;
import org.jboss.resteasy.test.core.basic.resource.ProviderInjectionSimpleResource;
import org.jboss.resteasy.test.core.basic.resource.ProviderInjectionSimpleResourceImpl;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @tpSubChapter Providers
 * @tpChapter Integration tests
 * @tpTestCaseDetails This test verifies that Providers instance can be injected into a Provider
 *                    using constructor or field injection.
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ProviderInjectionTest {
   static ResteasyClient client;

   @Deployment
   public static Archive<?> deploySimpleResource() {
      WebArchive war = TestUtil.prepareArchive(ProviderInjectionTest.class.getSimpleName());
      war.addClass(ProviderInjectionSimpleResource.class);
      war.addClass(PortProviderUtil.class);
      return TestUtil.finishContainerPrepare(war, null, ProviderInjectionSimpleMessageBodyWriter.class, ProviderInjectionSimpleResourceImpl.class);
   }

   @Before
   public void setUp() throws Exception {
      // do a request (force provider instantiation if providers were created lazily)
      client = (ResteasyClient)ClientBuilder.newClient();
      ProviderInjectionSimpleResource proxy = client.target(PortProviderUtil.generateBaseUrl(ProviderInjectionTest.class.getSimpleName())).proxyBuilder(ProviderInjectionSimpleResource.class).build();
      assertEquals(proxy.foo(), "bar");
   }

   @After
   public void after() throws Exception {
      client.close();
   }

   /**
    * @tpTestDetails Getting constructor
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testConstructorInjection() {
      for (ProviderInjectionSimpleMessageBodyWriter writer : ProviderInjectionSimpleMessageBodyWriter.getInstances()) {
         assertTrue(writer.getConstructorProviders() != null);
      }
   }

   /**
    * @tpTestDetails Getting field
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testFieldInjection() {
      for (ProviderInjectionSimpleMessageBodyWriter writer : ProviderInjectionSimpleMessageBodyWriter.getInstances()) {
         assertTrue(writer.getFieldProviders() != null);
      }
   }

}
