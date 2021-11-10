package org.jboss.resteasy.test.interceptor;


import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.client.ClientTestBase;
import org.jboss.resteasy.test.interceptor.resource.ClientRequestFilterImpl;
import org.jboss.resteasy.test.interceptor.resource.ClientResource;
import org.jboss.resteasy.test.interceptor.resource.CustomTestApp;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

/**
 * @tpSubChapter Interceptor
 * @tpChapter Integration tests
 * @tpTestCaseDetails Tests @Provider annotation on ClientRequestFilter (RESTEASY-2084)
 * @tpSince RESTEasy 4.0.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ClientRequestFilterRegistrationTest extends ClientTestBase {

   static Client client;

   @Deployment
   public static Archive<?> deploy() {
      WebArchive war = ShrinkWrap.create(WebArchive.class, ClientRequestFilterRegistrationTest.class.getSimpleName() + ".war");
      war.addClasses(CustomTestApp.class, ClientRequestFilterImpl.class, ClientResource.class);
      return war;
   }

   @Before
   public void before() {
      client = ClientBuilder.newClient();
   }

   @After
   public void close() {
      client.close();
   }

   @Test
   public void filterRegisteredTest() throws Exception {
      WebTarget base = client.target(generateURL("/") + "testIt");
      Response response = base.request().get();
      Assert.assertEquals(456, response.getStatus());
   }

}
