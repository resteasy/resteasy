package org.jboss.resteasy.test.client.proxy;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.test.client.proxy.resource.ClassloaderResource;
import org.jboss.resteasy.test.client.proxy.resource.ClientSmokeResource;
import org.jboss.resteasy.test.core.smoke.resource.ResourceWithInterfaceSimpleClient;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

@RunWith(Arquillian.class)
@RunAsClient
public class ProxyClassloaderTest
{

   @Deployment
   public static Archive<?> deploySimpleResource()
   {
      WebArchive war = TestUtil.prepareArchive(ProxyClassloaderTest.class.getSimpleName());
      war.addClass(ResourceWithInterfaceSimpleClient.class);
      war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
              new RuntimePermission("setContextClassLoader"),
              new RuntimePermission("createClassLoader"),
              new RuntimePermission("getClassLoader")
      ), "permissions.xml");
      return TestUtil.finishContainerPrepare(war, null, ClientSmokeResource.class, ClassloaderResource.class);
   }

   @Test
   public void testNoTCCL() throws Exception
   {
      ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
      String target2 = PortProviderUtil.generateURL("", ProxyClassloaderTest.class.getSimpleName());
      String target = PortProviderUtil.generateURL("/cl/cl?param=" + target2, ProxyClassloaderTest.class.getSimpleName());
      Response response = client.target(target).request().get();
      String entity = response.readEntity(String.class);
      Assert.assertEquals("basic", entity);
      response.close();
      client.close();
   }

}
