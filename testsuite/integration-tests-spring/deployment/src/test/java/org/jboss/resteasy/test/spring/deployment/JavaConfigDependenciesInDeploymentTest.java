package org.jboss.resteasy.test.spring.deployment;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.spring.deployment.resource.JavaConfigBeanConfiguration;
import org.jboss.resteasy.test.spring.deployment.resource.JavaConfigResource;
import org.jboss.resteasy.test.spring.deployment.resource.JavaConfigService;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtilSpring;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.FilePermission;
import java.lang.reflect.ReflectPermission;
import java.util.PropertyPermission;
import java.util.logging.LoggingPermission;

/**
 * @tpSubChapter Spring
 * @tpChapter Integration tests - dependencies included in deployment
 * @tpTestCaseDetails This test will verify that the resource invoked by RESTEasy has been
 * initialized by spring when defined using spring's JavaConfig.
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class JavaConfigDependenciesInDeploymentTest {

   private static final String PATH = "/invoke";

   private String generateURL(String path) {
      return PortProviderUtil.generateURL(path, JavaConfigDependenciesInDeploymentTest.class.getSimpleName());
   }

   @Deployment
   private static Archive<?> deploy() {
      WebArchive archive = ShrinkWrap.create(WebArchive.class, JavaConfigDependenciesInDeploymentTest.class.getSimpleName() + ".war")
            .addClass(JavaConfigResource.class)
            .addClass(JavaConfigService.class)
            .addClass(JavaConfigBeanConfiguration.class)
            .addAsWebInfResource(JavaConfigDependenciesInDeploymentTest.class.getPackage(), "javaConfig/web.xml", "web.xml");

      // Permission needed for "arquillian.debug" to run
      // "suppressAccessChecks" required for access to arquillian-core.jar
      // remaining permissions needed to run springframework
      archive.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
            new PropertyPermission("arquillian.*", "read"),
              new PropertyPermission("org.springframework.cglib.test.stressHashCodes", "read"),
            new PropertyPermission("cglib.debugLocation", "read"),
            new RuntimePermission("accessClassInPackage.sun.reflect.annotation"),
            new RuntimePermission("getProtectionDomain"),
            new ReflectPermission("suppressAccessChecks"),
            new RuntimePermission("accessDeclaredMembers"),
            new FilePermission("<<ALL FILES>>", "read"),
            new LoggingPermission("control", "")
      ), "permissions.xml");

      TestUtilSpring.addSpringLibraries(archive);
      return archive;
   }

   /**
    * @tpTestDetails This test will verify that the resource invoked by RESTEasy has been
    * initialized by spring when defined using spring's JavaConfig.
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void test() throws Exception {
      Client client = ResteasyClientBuilder.newClient();
      WebTarget target = client.target(generateURL(PATH));
      Response response = target.request().get();
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertEquals("Unexpected response", "hello", response.readEntity(String.class));
      client.close();
   }
}
