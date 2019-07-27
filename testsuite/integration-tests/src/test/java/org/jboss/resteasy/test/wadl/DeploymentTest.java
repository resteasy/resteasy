package org.jboss.resteasy.test.wadl;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import java.lang.reflect.ReflectPermission;

import static org.junit.Assert.assertNotNull;

@RunWith(Arquillian.class)
@RunAsClient
public class DeploymentTest {

   private static ResteasyClient client;

   @Deployment
   public static Archive<?> deploy() {
      WebArchive war = TestUtil.prepareArchiveWithApplication(DeploymentTest.class.getSimpleName(), WadlTestApplication.class);
      war.addPackages(true, "org.jboss.resteasy.wadl");
      war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
              new RuntimePermission("getClassLoader"),
              new ReflectPermission("suppressAccessChecks"),
              new RuntimePermission("accessDeclaredMembers")
      ), "permissions.xml");
      TestUtil.finishContainerPrepare(war, null, ExtendedResource.class, ListType.class);
      return war;
   }

   private static String generateURL(String path) {
      return PortProviderUtil.generateURL(path, DeploymentTest.class.getSimpleName());
   }

   //////////////////////////////////////////////////////////////////////////////
   @BeforeClass
   public static void beforeClass() {
      client = (ResteasyClient) ClientBuilder.newClient();
   }

   @AfterClass
   public static void after() {
      client.close();
   }

   @Test
   public void testBasic() {
      {
         ResteasyWebTarget target = client.target(generateURL("/application.xml"));
         Response response = target.request().get();
         int status = response.getStatus();
         Assert.assertEquals(200, status);

         // get Application
         org.jboss.resteasy.wadl.jaxb.Application application = response.readEntity(org.jboss.resteasy.wadl.jaxb.Application.class);
         assertNotNull(application);
      }

      {
         ResteasyWebTarget target = client.target(generateURL("/wadl-extended/xsd0.xsd"));
         Response response = target.request().get();
         int status = response.getStatus();
         Assert.assertEquals(200, status);

         assertNotNull(response.readEntity(String.class));
      }
   }
}
