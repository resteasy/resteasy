package org.jboss.resteasy.test.client.exception;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import javax.ws.rs.client.ClientBuilder;
import org.jboss.resteasy.test.client.exception.resource.UnauthorizedExceptionInterface;
import org.jboss.resteasy.test.client.exception.resource.UnauthorizedExceptionResource;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.NotAuthorizedException;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Client tests
 * @tpSince RESTEasy 3.0.16
 * @tpTestCaseDetails Regression test for RESTEASY-435
 */
@RunWith(Arquillian.class)
@RunAsClient
public class UnauthorizedExceptionTest {

   @Deployment
   public static Archive<?> deploy() {
      WebArchive war = TestUtil.prepareArchive(UnauthorizedExceptionTest.class.getSimpleName());
      war.addClass(UnauthorizedExceptionInterface.class);
      return TestUtil.finishContainerPrepare(war, null, UnauthorizedExceptionResource.class);
   }

   private String generateURL(String path) {
      return PortProviderUtil.generateURL(path, UnauthorizedExceptionTest.class.getSimpleName());
   }

   /**
    * @tpTestDetails Check thrown exception on client side.
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testMe() throws Exception {
      ResteasyClient client = (ResteasyClient)ClientBuilder.newClient();
      UnauthorizedExceptionInterface proxy = client.target(generateURL("")).proxy(UnauthorizedExceptionInterface.class);
      try {
         proxy.postIt("hello");
         Assert.fail();
      } catch (NotAuthorizedException e) {
         Assert.assertEquals(HttpResponseCodes.SC_UNAUTHORIZED, e.getResponse().getStatus());
      }
      client.close();
   }

}
