package org.jboss.resteasy.test.interceptor;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.plugins.interceptors.encoding.AcceptEncodingGZIPFilter;
import org.jboss.resteasy.plugins.interceptors.encoding.GZIPDecodingInterceptor;
import org.jboss.resteasy.plugins.interceptors.encoding.GZIPEncodingInterceptor;
import org.jboss.resteasy.test.interceptor.resource.GZIPAnnotationInterface;
import org.jboss.resteasy.test.interceptor.resource.GZIPAnnotationResource;
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
 * @tpSubChapter Interceptor
 * @tpChapter Integration tests
 * @tpTestCaseDetails Tests @GZIP annotation on client (RESTEASY-1265)
 * @tpSince RESTEasy 3.0.20
 */
@RunWith(Arquillian.class)
@RunAsClient
public class GZIPAnnotationTest {
   
   static Client client;

   @BeforeClass
   public static void setup() {
       client = ClientBuilder.newClient()
                   .register(AcceptEncodingGZIPFilter.class)
                   .register(GZIPEncodingInterceptor.class)
                   .register(GZIPDecodingInterceptor.class);
   }

   @AfterClass
   public static void close() {
       client.close();
   }

   @Deployment
   public static Archive<?> deploy() {
       WebArchive war = TestUtil.prepareArchive(GZIPAnnotationTest.class.getSimpleName());
       war.addClass(GZIPAnnotationInterface.class);
       war.addAsManifestResource("org/jboss/resteasy/test/client/javax.ws.rs.ext.Providers", "services/javax.ws.rs.ext.Providers");
       return TestUtil.finishContainerPrepare(war, null, GZIPAnnotationResource.class);
   }

   private String generateURL(String path) {
       return PortProviderUtil.generateURL(path, GZIPAnnotationTest.class.getSimpleName());
   }

   /**
    * @tpTestDetails Test that org.jboss.resteasy.plugins.interceptors.encoding.ClientContentEncodingAnnotationFilter
    *                and org.jboss.resteasy.plugins.interceptors.encoding.AcceptEncodingGZIPFilter
    *                are called on client side
    * @tpSince RESTEasy 3.0.20
    */
   @Test
   public void testGZIP() {
      ResteasyWebTarget target = (ResteasyWebTarget) client.target(generateURL(""));
      GZIPAnnotationInterface resource = target.proxy(GZIPAnnotationInterface.class);
      String s = resource.getFoo("test");
      Assert.assertTrue(s.contains("gzip"));
      Assert.assertTrue(s.substring(s.indexOf("gzip") + 4).contains("gzip"));
   }
}
