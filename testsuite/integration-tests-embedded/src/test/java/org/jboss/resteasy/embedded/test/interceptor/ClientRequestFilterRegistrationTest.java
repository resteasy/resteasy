package org.jboss.resteasy.embedded.test.interceptor;

import org.jboss.resteasy.plugins.server.embedded.EmbeddedJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.embedded.test.EmbeddedServerTestBase;
import org.jboss.resteasy.embedded.test.interceptor.resource.ClientRequestFilterImpl;
import org.jboss.resteasy.embedded.test.interceptor.resource.ClientResource;
import org.jboss.resteasy.embedded.test.interceptor.resource.CustomTestApp;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static org.jboss.resteasy.embedded.test.TestPortProvider.generateURL;

/**
 * @tpSubChapter
 * @tpChapter Embedded Containers
 * @tpTestCaseDetails Tests @Provider annotation on ClientRequestFilter
 * @tpSince RESTEasy 4.1.0
 */
public class ClientRequestFilterRegistrationTest extends EmbeddedServerTestBase {

   static Client client;
   private static EmbeddedJaxrsServer server;

   @Before
   public void before() throws Exception {
      client = ClientBuilder.newClient();
      server = getServer();
      ResteasyDeployment deployment = server.getDeployment();
      deployment.getScannedResourceClasses().add(ClientResource.class.getName());
      deployment.getScannedProviderClasses().add(ClientRequestFilterImpl.class.getName());
      deployment.setApplicationClass(CustomTestApp.class.getName());
      server.start();
      server.deploy();
   }

   @After
   public void close() {
      client.close();
      server.stop();
   }

   @Test
   public void filterRegisteredTest() throws Exception {
      WebTarget base = client.target(generateURL("/") + "testIt");
      Response response = base.request().get();
      Assert.assertEquals(456, response.getStatus());
   }

}
