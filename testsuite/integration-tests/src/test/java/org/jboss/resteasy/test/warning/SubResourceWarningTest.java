package org.jboss.resteasy.test.warning;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.category.NotForBootableJar;
import org.jboss.resteasy.test.core.interceptors.resource.TestResource1;
import org.jboss.resteasy.test.core.interceptors.resource.TestResource2;
import org.jboss.resteasy.test.core.interceptors.resource.TestSubResource;
import org.jboss.resteasy.test.warning.resource.SubResourceWarningResource;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;

import static org.jboss.resteasy.test.ContainerConstants.DEFAULT_CONTAINER_QUALIFIER;

/**
 * @tpSubChapter Miscellaneous
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.5
 * Created by rsearls on 9/11/17.
 */
@RunWith(Arquillian.class)
@RunAsClient
@Category(NotForBootableJar.class) // no log check support for bootable-jar in RESTEasy TS so far
public class SubResourceWarningTest {

   // check server.log msg count before app is deployed.  Deploying causes messages to be logged.
   private static int preTestCnt = TestUtil.getWarningCount("have the same path, [test", false, DEFAULT_CONTAINER_QUALIFIER);

   @Deployment
   public static Archive<?> deploySimpleResource() {
      WebArchive war = TestUtil.prepareArchive(SubResourceWarningTest.class.getSimpleName());
      return TestUtil.finishContainerPrepare(war, null, SubResourceWarningResource.class,
            TestResource1.class, TestResource2.class, TestSubResource.class);
   }

   @BeforeClass
   public static void initLogging() throws Exception {
      OnlineManagementClient client = TestUtil.clientInit();
      TestUtil.runCmd(client, "/subsystem=logging/logger=org.jboss.resteasy:add(level=WARN)");
      client.close();
   }

   @AfterClass
   public static void removeLogging() throws Exception {
      OnlineManagementClient client = TestUtil.clientInit();
      TestUtil.runCmd(client, "/subsystem=logging/logger=org.jboss.resteasy:remove()");
      client.close();
   }

   /**
    * Confirms that 2 warning messages about this incorrect coding is printed to the server.log
    * Must check for path because warning text, RESTEASY002195, exist in log for a previous test
    * in the suite.
    * @throws Exception
    */
   @Test
   public void testWarningMsg () throws Exception {
      int cnt = TestUtil.getWarningCount("have the same path, [test", false, DEFAULT_CONTAINER_QUALIFIER);
      Assert.assertEquals( "Improper log WARNING count", preTestCnt+2, cnt);
   }
}
