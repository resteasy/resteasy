package org.jboss.resteasy.test.cdi.interceptors;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.cdi.interceptors.resource.NameBoundCDIProxiesApplication;
import org.jboss.resteasy.test.cdi.interceptors.resource.NameBoundCDIProxiesInterceptor;
import org.jboss.resteasy.test.cdi.interceptors.resource.NameBoundCDIProxiesResource;
import org.jboss.resteasy.test.cdi.interceptors.resource.NameBoundProxiesAnnotation;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter CDI
 * @tpChapter Integration tests
 * @tpTestCaseDetails Name bound interceptors and Application CDI proxies
 * @tpSince RESTEasy 4.0.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class NameBoundCDIProxiesTest {

   @Deployment
   public static Archive<?> deploySimpleResource() {
      WebArchive war = prepareArchive(NameBoundCDIProxiesTest.class.getSimpleName());
      war.addClass(NameBoundProxiesAnnotation.class);
      war.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
      return TestUtil.finishContainerPrepare(war, null, NameBoundCDIProxiesResource.class, NameBoundCDIProxiesInterceptor.class);
   }

   // Use specific Application subclass
   private static WebArchive prepareArchive(String deploymentName) {
      WebArchive war = ShrinkWrap.create(WebArchive.class, deploymentName + ".war");
      war.addClass(NameBoundCDIProxiesApplication.class);
      return war;
   }
   private String generateURL(String path) {
      return PortProviderUtil.generateURL(path, NameBoundCDIProxiesTest.class.getSimpleName());
   }

   /**
    * @tpTestDetails Verify that 
    * @tpSince RESTEasy 4.0.0
    */
   @Test
   public void testNameBoundInterceptor() throws Exception {
      Client client = ClientBuilder.newClient();
      String answer = client.target(generateURL("/test")).request().get(String.class);
      Assert.assertEquals("in-test-out", answer);
      client.close();
   }
}
