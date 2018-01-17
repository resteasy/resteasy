package org.jboss.resteasy.test.client.proxy;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.client.proxy.resource.ProxyNullInputStreamClientResponseFilter;
import org.jboss.resteasy.test.client.proxy.resource.ProxyNullInputStreamResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for RESTEASY-1671
 * @tpSince RESTEasy 3.5
 *
 * Created by rsearls on 8/24/17.
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ProxyNullInputStreamTest {

   @Deployment
   public static Archive<?> deploySimpleResource() {
      WebArchive war = TestUtil.prepareArchive(ProxyNullInputStreamTest.class.getSimpleName());
      war.addClasses(ProxyNullInputStreamResource.class,
              ProxyNullInputStreamClientResponseFilter.class);
      return TestUtil.finishContainerPrepare(war, null, (Class<?>[]) null);
   }

   private String generateURL(String path) {
      return PortProviderUtil.generateURL(path, ProxyNullInputStreamTest.class.getSimpleName());
   }


   @Test
   public void testNullPointerEx () throws Exception {
      ResteasyClient client = new ResteasyClientBuilder().register(ProxyNullInputStreamClientResponseFilter.class).build();
      ProxyNullInputStreamResource pResource = client.target(generateURL("/test/user/mydb"))
              .proxyBuilder(ProxyNullInputStreamResource.class)
              .build();
      try
      {
         pResource.getUserHead("myDb");
      } catch (Exception e) {
         Assert.assertEquals("HTTP 404 Not Found", e.getMessage());
      }

   }
}
