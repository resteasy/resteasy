package org.jboss.resteasy.test.client;

import static org.jboss.resteasy.test.ContainerConstants.DEFAULT_CONTAINER_QUALIFIER;

import java.io.File;
import java.io.FilePermission;
import java.lang.reflect.ReflectPermission;
import java.util.PropertyPermission;
import java.util.logging.LoggingPermission;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.category.NotForBootableJar;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Unit tests
 * @tpSince RESTEasy 3.0.17
 */
@RunWith(Arquillian.class)
@Category(NotForBootableJar.class)  // no log check support for bootable-jar in RESTEasy TS so far
public class ClientBuilderTest {

   @SuppressWarnings(value = "unchecked")
   @Deployment
   public static Archive<?> deploy() {
      WebArchive war = TestUtil.prepareArchive(ClientBuilderTest.class.getSimpleName());
      war.addClass(TestUtil.class);
      war.addClass(NotForBootableJar.class);
      // Arquillian in the deployment and use of TestUtil
      war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(new ReflectPermission("suppressAccessChecks"),
            new FilePermission(TestUtil.getStandaloneDir(DEFAULT_CONTAINER_QUALIFIER) + File.separator + "log" +
                  File.separator + "server.log", "read"),
            new LoggingPermission("control", ""),
            new PropertyPermission("arquillian.*", "read"),
            new PropertyPermission("jboss.home.dir", "read"),
            new PropertyPermission("jboss.server.base.dir", "read"),
            new RuntimePermission("accessDeclaredMembers")
      ), "permissions.xml");
      return TestUtil.finishContainerPrepare(war, null, (Class<?>[]) null);
   }


   public static class FeatureReturningFalse implements Feature {
      @Override
      public boolean configure(FeatureContext context) {
         // false returning feature is not to be registered
         return false;
      }
   }

   private int getWarningCount() {
      return TestUtil.getWarningCount("RESTEASY002155", true, DEFAULT_CONTAINER_QUALIFIER);
   }

   /**
    * @tpTestDetails Register class twice to the client
    * @tpPassCrit Warning will be raised that second class registration is ignored
    * @tpSince RESTEasy 3.0.17
    */
   @Test
   public void testDoubleClassRegistration() {
      int initCount = getWarningCount();
      Client client = ClientBuilder.newClient();
      int count = client.getConfiguration().getClasses().size();
      client.register(FeatureReturningFalse.class).register(FeatureReturningFalse.class);

      Assert.assertEquals("RESTEASY002155 log not found", 1, getWarningCount() - initCount);
      Assert.assertEquals(count + 1, client.getConfiguration().getClasses().size());
      client.close();
   }

   /**
    * @tpTestDetails Register provider instance twice to the client
    * @tpPassCrit Warning will be raised that second provider instance registration is ignored
    * @tpSince RESTEasy 3.0.17
    */
   @Test
   public void testDoubleRegistration() {
      int countRESTEASY002160 = TestUtil.getWarningCount("RESTEASY002160", true, DEFAULT_CONTAINER_QUALIFIER);
      int countRESTEASY002155 = TestUtil.getWarningCount("RESTEASY002155", true, DEFAULT_CONTAINER_QUALIFIER);
      Client client = ClientBuilder.newClient();
      int count = client.getConfiguration().getInstances().size();
      Object reg = new FeatureReturningFalse();

      client.register(reg).register(reg);
      client.register(FeatureReturningFalse.class).register(FeatureReturningFalse.class);
      Assert.assertEquals("Expect 1 warnining messages of Provider instance is already registered", 1, TestUtil.getWarningCount("RESTEASY002160", true, DEFAULT_CONTAINER_QUALIFIER) - countRESTEASY002160);
      Assert.assertEquals("Expect 1 warnining messages of Provider class is already registered", 2, TestUtil.getWarningCount("RESTEASY002155", true, DEFAULT_CONTAINER_QUALIFIER) - countRESTEASY002155);
      Assert.assertEquals(count + 1, client.getConfiguration().getInstances().size());

      client.close();
   }
}
