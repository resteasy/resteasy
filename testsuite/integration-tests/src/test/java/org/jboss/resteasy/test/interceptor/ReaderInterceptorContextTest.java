package org.jboss.resteasy.test.interceptor;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.interceptor.resource.ReaderInterceptorContextInterceptor;
import org.jboss.resteasy.test.interceptor.resource.ReaderInterceptorContextResource;
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
 * @tpSubChapter Verify ReaderInterceptorContext.getHeaders() returns mutable map: RESTEASY-2298.
 * @tpChapter Integration tests
 * @tpSince RESTEasy 4.2.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ReaderInterceptorContextTest
{
   private static Client client;

   @Deployment
   public static Archive<?> deploy() {
      WebArchive war = TestUtil.prepareArchive(ReaderInterceptorContextTest.class.getSimpleName());
      return TestUtil.finishContainerPrepare(war, null, ReaderInterceptorContextInterceptor.class, ReaderInterceptorContextResource.class);
   }

   private String generateURL(String path) {
      return PortProviderUtil.generateURL(path, ReaderInterceptorContextTest.class.getSimpleName());
   }

   @BeforeClass
   public static void before() throws Exception {
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void after() throws Exception {
      client.close();
   }

   @Test
   public void testInterceptorHeaderMap() throws Exception {
      Response response = client.target(generateURL("/post")).request().post(Entity.entity("dummy", "text/plain"));
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("123789", response.readEntity(String.class));
   }
}
