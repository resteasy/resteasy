package org.jboss.resteasy.test.interceptor;


import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.client.ClientTestBase;
import org.jboss.resteasy.test.interceptor.resource.PreMatchingClientRequestFilterImpl;
import org.jboss.resteasy.test.interceptor.resource.PreMatchingClientResource;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Interceptor
 * @tpChapter Integration tests
 * @tpTestCaseDetails Tests @PreMatching annotation on ClientRequestFilter (RESTEASY-1696)
 * @tpSince RESTEasy 4.0.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class PreMatchingClientRequestFilterTest extends ClientTestBase {

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   static Client client;

   @Deployment
   public static Archive<?> deploy() {
      WebArchive war = TestUtil.prepareArchive(PreMatchingClientRequestFilterTest.class.getSimpleName());
      //rls //war.addClass(ClientExceptionsData.class);
      return TestUtil.finishContainerPrepare(war, null, PreMatchingClientResource.class);
   }

   @Before
   public void before() {
      client = ClientBuilder.newClient();
   }

   @After
   public void close() {
      client.close();
   }

   /**
    * @tpTestDetails Test that annotation @PreMatching on an implementation of ClientRequestFilter
    *                is ignored. This annotation is only valid on ContainerRequestFilter implementations.
    * @tpSince RESTEasy 4.0.0
    */
   @Test
   public void preMatchingTest() throws Exception {
      WebTarget base = client.target(generateURL("/") + "testIt");
      Response response = base.register(PreMatchingClientRequestFilterImpl.class).request().get();
      Assert.assertEquals(404, response.getStatus());
   }

}
