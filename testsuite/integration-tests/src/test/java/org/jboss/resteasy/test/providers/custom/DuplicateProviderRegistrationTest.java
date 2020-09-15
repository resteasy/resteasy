package org.jboss.resteasy.test.providers.custom;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.category.NotForBootableJar;
import org.jboss.resteasy.test.ContainerConstants;
import org.jboss.resteasy.test.providers.custom.resource.DuplicateProviderRegistrationFeature;
import org.jboss.resteasy.test.providers.custom.resource.DuplicateProviderRegistrationFilter;
import org.jboss.resteasy.test.providers.custom.resource.DuplicateProviderRegistrationInterceptor;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Feature;
import javax.ws.rs.ext.ReaderInterceptor;
import java.io.File;
import java.io.FilePermission;
import java.lang.reflect.ReflectPermission;
import java.util.PropertyPermission;
import java.util.logging.LoggingPermission;

import static org.jboss.resteasy.test.ContainerConstants.DEFAULT_CONTAINER_QUALIFIER;

/**
 * @tpSubChapter Providers
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for JBEAP-4703
 * @tpSince RESTEasy 3.0.17
 */
@RunWith(Arquillian.class)
@Category(NotForBootableJar.class)  // no log check support for bootable-jar in RESTEasy TS so far
public class DuplicateProviderRegistrationTest {

   private static final String RESTEASY_002155_ERR_MSG = "Wrong count of RESTEASY002155 warning message";
   private static final String RESTEASY_002160_ERR_MSG = "Wrong count of RESTEASY002160 warning message";

   @Deployment
   public static Archive<?> createTestArchive() {
      WebArchive war = TestUtil.prepareArchive(DuplicateProviderRegistrationTest.class.getSimpleName());
      war.addClasses(DuplicateProviderRegistrationFeature.class, DuplicateProviderRegistrationFilter.class,
            TestUtil.class, DuplicateProviderRegistrationInterceptor.class, ContainerConstants.class);
      war.addClass(NotForBootableJar.class);
      // Arquillian in the deployment, test reads the server.log
      war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
            new ReflectPermission("suppressAccessChecks"),
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

   private static int getRESTEASY002155WarningCount() {
      return TestUtil.getWarningCount("RESTEASY002155", true, DEFAULT_CONTAINER_QUALIFIER);
   }

   private static int getRESTEASY002160WarningCount() {
      return TestUtil.getWarningCount("RESTEASY002160", true, DEFAULT_CONTAINER_QUALIFIER);
   }

   /**
    * @tpTestDetails Basic test
    * @tpSince RESTEasy 3.0.17
    */
   @Test
   public void testDuplicateProvider() {
      int initRESTEASY002160WarningCount = getRESTEASY002160WarningCount();
      Client client = ClientBuilder.newClient();
      try {
         WebTarget webTarget = client.target("http://www.changeit.com");
         // DuplicateProviderRegistrationFeature will be registered third on the same webTarget even if
         //   webTarget.getConfiguration().isRegistered(DuplicateProviderRegistrationFeature.class)==true
         webTarget.register(DuplicateProviderRegistrationFeature.class).register(new DuplicateProviderRegistrationFeature()).register(new DuplicateProviderRegistrationFeature());
      } finally {
         client.close();
      }
      Assert.assertEquals(RESTEASY_002160_ERR_MSG, 2, getRESTEASY002160WarningCount() - initRESTEASY002160WarningCount);
   }

   /**
    * @tpTestDetails This test is taken from javax.ws.rs.core.Configurable javadoc
    * @tpSince RESTEasy 3.0.17
    */
   @Test
   public void testFromJavadoc() {
      int initRESTEASY002155WarningCount = getRESTEASY002155WarningCount();
      int initRESTEASY002160WarningCount = getRESTEASY002160WarningCount();
      Client client = ClientBuilder.newClient();
      try {
         WebTarget webTarget = client.target("http://www.changeit.com");
         webTarget.register(DuplicateProviderRegistrationInterceptor.class, ReaderInterceptor.class);
         webTarget.register(DuplicateProviderRegistrationInterceptor.class);       // Rejected by runtime.
         webTarget.register(new DuplicateProviderRegistrationInterceptor());       // Rejected by runtime.
         webTarget.register(DuplicateProviderRegistrationInterceptor.class, 6500); // Rejected by runtime.

         webTarget.register(new DuplicateProviderRegistrationFeature());
         webTarget.register(new DuplicateProviderRegistrationFeature()); // rejected by runtime.
         webTarget.register(DuplicateProviderRegistrationFeature.class);   // rejected by runtime.
         webTarget.register(DuplicateProviderRegistrationFeature.class, Feature.class);  // Rejected by runtime.
      } finally {
         client.close();
      }
      Assert.assertEquals(RESTEASY_002155_ERR_MSG, 4, getRESTEASY002155WarningCount() - initRESTEASY002155WarningCount);
      Assert.assertEquals(RESTEASY_002160_ERR_MSG, 2, getRESTEASY002160WarningCount() - initRESTEASY002160WarningCount);
   }
}
