package org.jboss.resteasy.test.microprofile.restclient;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.microprofile.restclient.resource.RestClientProxyRedeployRemoteService;
import org.jboss.resteasy.test.microprofile.restclient.resource.RestClientProxyRedeployResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
@RunAsClient
public class RestClientProxyRedeployTest
{
   @Deployment(name="deployment1", order = 1)
   public static Archive<?> deploy1() {
      WebArchive war = TestUtil.prepareArchive(RestClientProxyRedeployTest.class.getSimpleName() + "1");
      war.addClass(RestClientProxyRedeployRemoteService.class);
      war.addAsManifestResource(new StringAsset("Dependencies: org.eclipse.microprofile.restclient"), "MANIFEST.MF");
      war.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
      return TestUtil.finishContainerPrepare(war, null, RestClientProxyRedeployResource.class);
   }

   @Deployment(name="deployment2", order = 2)
   public static Archive<?> deploy2() {
      WebArchive war = TestUtil.prepareArchive(RestClientProxyRedeployTest.class.getSimpleName() + "2");
      war.addClass(RestClientProxyRedeployRemoteService.class);
      war.addAsManifestResource(new StringAsset("Dependencies: org.eclipse.microprofile.restclient"), "MANIFEST.MF");
      war.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
      return TestUtil.finishContainerPrepare(war, null, RestClientProxyRedeployResource.class);
   }

   private String generateURL(String path, String suffix) {
      return PortProviderUtil.generateURL(path, RestClientProxyRedeployTest.class.getSimpleName() + suffix);
   }

   @Test
   public void testGet1() throws Exception {
      Client client = ClientBuilder.newClient();
      Response response = client.target(generateURL("/test/1", "1")).request().get();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.readEntity(String.class);
      Assert.assertEquals("OK", entity);
      client.close();
   }

   @Test
   public void testGet2() throws Exception {
      Client client = ClientBuilder.newClient();
      Response response = client.target(generateURL("/test/1", "2")).request().get();
      Assert.assertEquals(200, response.getStatus());
      String entity = response.readEntity(String.class);
      Assert.assertEquals("OK", entity);
      client.close();
   }
}