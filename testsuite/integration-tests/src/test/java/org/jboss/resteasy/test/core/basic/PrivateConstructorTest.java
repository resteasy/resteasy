package org.jboss.resteasy.test.core.basic;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import javax.ws.rs.client.ClientBuilder;
import org.jboss.resteasy.test.core.basic.resource.PrivateConstructorServiceResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;


/**
 * @tpSubChapter Constructors
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-489
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class PrivateConstructorTest {

   @Deployment
   public static Archive<?> deploySimpleResource() {
      WebArchive war = TestUtil.prepareArchive(PrivateConstructorTest.class.getSimpleName());
      return TestUtil.finishContainerPrepare(war, null, PrivateConstructorServiceResource.class);
   }

   /**
    * @tpTestDetails Exception should not be thrown  on WS with a non-public constructor
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testMapper() throws Exception {
      ResteasyClient client = (ResteasyClient)ClientBuilder.newClient();
      WebTarget base = client.target(PortProviderUtil.generateURL("/test", PrivateConstructorTest.class.getSimpleName()));
      Response response = base.request().get();
      Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
      response.close();
      client.close();
   }
}
