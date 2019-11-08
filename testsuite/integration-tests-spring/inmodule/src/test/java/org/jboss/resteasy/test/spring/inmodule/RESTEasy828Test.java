package org.jboss.resteasy.test.spring.inmodule;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.spring.inmodule.resource.RESTEasy828Resource;

import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.FilePermission;
import java.lang.reflect.ReflectPermission;
import java.util.PropertyPermission;
import java.util.logging.LoggingPermission;

@RunWith(Arquillian.class)
@RunAsClient
public class RESTEasy828Test {

   static Client client;

   private String generateURL(String path) {
      return PortProviderUtil.generateURL(path, RESTEasy828Test.class.getSimpleName());
   }

   @Before
   public void init() {
      client = ClientBuilder.newClient();
   }

   @After
   public void after() {
      client.close();
   }

   @Deployment
   private static Archive<?> deploy() {
      WebArchive archive = ShrinkWrap.create(WebArchive.class, RESTEasy828Test.class.getSimpleName() + ".war")
              .addAsWebInfResource(RESTEasy828Test.class.getPackage(), "resteasy828/web.xml", "web.xml");
      archive.addAsWebInfResource(RESTEasy828Test.class.getPackage(),
              "resteasy828/applicationContext.xml", "applicationContext.xml");

      archive.addClass(RESTEasy828Resource.class);

      archive.addAsLibraries(Maven
              .resolver()
              .loadPomFromFile("pom.xml")
              .resolve("org.springframework:spring-webmvc")
              .withTransitivity()
              .asFile());

      archive.addAsLibraries(Maven
              .resolver()
              .loadPomFromFile("pom.xml")
              .resolve("org.jboss.resteasy:resteasy-spring")
              .withTransitivity()
              .asFile());

      archive.addAsLibraries(Maven
              .resolver()
              .loadPomFromFile("pom.xml")
              .resolve("org.jboss.resteasy:resteasy-core")
              .withTransitivity()
              .asFile());

      archive.addAsLibraries(Maven
              .resolver()
              .loadPomFromFile("pom.xml")
              .resolve("org.jboss.resteasy:resteasy-core-spi")
              .withTransitivity()
              .asFile());


      // Permission needed for "arquillian.debug" to run
      // "suppressAccessChecks" required for access to arquillian-core.jar
      // remaining permissions needed to run springframework
      archive.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
              new PropertyPermission("arquillian.*", "read"),
              new ReflectPermission("suppressAccessChecks"),
              new RuntimePermission("accessDeclaredMembers"),
              new FilePermission("<<ALL FILES>>", "read"),
              new LoggingPermission("control", "")
      ), "permissions.xml");


      return archive;
   }

   @Test
   public void testResteasy828() throws InterruptedException {
      WebTarget target = client.target(generateURL("/resteasy828"));
      Response response = target.request().get();
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertNotNull(target.request().get(String.class));
   }
}
