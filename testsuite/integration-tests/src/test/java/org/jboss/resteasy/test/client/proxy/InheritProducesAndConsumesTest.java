package org.jboss.resteasy.test.client.proxy;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.jboss.resteasy.client.jaxrs.ProxyBuilder;
import org.jboss.resteasy.test.client.proxy.resource.ProducesAndConsumesRootResource;
import org.jboss.resteasy.test.client.proxy.resource.ProducesAndConsumesChildResource;
import org.jboss.resteasy.test.client.proxy.resource.ProducesAndConsumesGrandChildResource;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.client.proxy.resource.InheritProducesAndConsumesService;
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
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for RESTEASY-1717.
 * @tpSince RESTEasy 4.0.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class InheritProducesAndConsumesTest {


   private static Client client;
   private static final String DEP = "InheritProducesAndConsumesTest";

   @Deployment
   public static Archive<?> deploy() {
      WebArchive war = TestUtil.prepareArchive(DEP);
      return TestUtil.finishContainerPrepare(war, null,
              InheritProducesAndConsumesService.class);
   }

   @BeforeClass
   public static void setup() {
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void cleanup() {
      client.close();
   }

   private static String generateURL() {
      return PortProviderUtil.generateBaseUrl(DEP);
   }

   @Test
   public void intfRootTest() throws Exception {

      ProducesAndConsumesRootResource rootProxy = ProxyBuilder
              .builder(ProducesAndConsumesRootResource.class, client.target(generateURL()))
              .build();
      Assert.assertEquals("Service Hello", rootProxy.getRootGreeting());
   }

   @Test
   public void intfChildTest() throws Exception {
      ProducesAndConsumesChildResource childProxy = ProxyBuilder
              .builder(ProducesAndConsumesChildResource.class, client.target(generateURL()))
              .build();
      Assert.assertEquals("Service Hello", childProxy.getChildGreeting());
      Assert.assertEquals("Service Hello", childProxy.getRootGreeting());
   }

   @Test
   public void intfGrandTest() throws Exception {
       ProducesAndConsumesGrandChildResource grandChildProxy = ProxyBuilder
       .builder(ProducesAndConsumesGrandChildResource.class, client.target(generateURL()))
       .build();

       Assert.assertEquals("Service Hello", grandChildProxy.getGrandChildGreeting());
       Assert.assertEquals("Service Hello", grandChildProxy.getChildGreeting());
       Assert.assertEquals("Service Hello", grandChildProxy.getRootGreeting());
   }
}
