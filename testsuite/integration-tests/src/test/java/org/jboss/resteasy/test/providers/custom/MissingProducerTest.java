package org.jboss.resteasy.test.providers.custom;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.category.NotForBootableJar;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static org.jboss.resteasy.test.ContainerConstants.DEFAULT_CONTAINER_QUALIFIER;

/**
 * @tpSubChapter Core
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.17
 * @tpTestCaseDetails Regression test for JBEAP-4719
 */
@RunWith(Arquillian.class)
@RunAsClient
@Category(NotForBootableJar.class) // no log check support for bootable-jar in RESTEasy TS so far
public class MissingProducerTest {
   private static final String ERR_MSG = "Warning was not logged";
   private static int initLogMsg1Count = parseLog1();
   private static int initLogMsg2Count = parseLog2();
   private static int initLogMsg3Count = parseLog3();

   private static int parseLog1() {
      return TestUtil.getWarningCount("RESTEASY002120: ClassNotFoundException: ", false, DEFAULT_CONTAINER_QUALIFIER);
   }
   private static int parseLog2() {
      return TestUtil.getWarningCount("Unable to load builtin provider org.jboss.resteasy.Missing from ", false, DEFAULT_CONTAINER_QUALIFIER);
   }
   private static int parseLog3() {
      return TestUtil.getWarningCount("classes/META-INF/services/javax.ws.rs.ext.Providers", false, DEFAULT_CONTAINER_QUALIFIER);
   }

   @SuppressWarnings(value = "unchecked")
   @Deployment
   public static Archive<?> createTestArchive() {
      WebArchive war = TestUtil.prepareArchive(MissingProducerTest.class.getSimpleName());
      war.addAsResource(MissingProducerTest.class.getPackage(), "MissingProducer.Providers", "META-INF/services/javax.ws.rs.ext.Providers");
      return TestUtil.finishContainerPrepare(war, null, (Class<?>[]) null);
   }

   /**
    * @tpTestDetails Check logs for RESTEASY002120 warning message.
    * @tpSince RESTEasy 3.0.17
    */
   @Test
   public void testMissingProducer() {
      Assert.assertEquals(ERR_MSG, 1, parseLog1() - initLogMsg1Count);
      Assert.assertEquals(ERR_MSG, 1, parseLog2() - initLogMsg2Count);
      Assert.assertEquals(ERR_MSG, 1, parseLog3() - initLogMsg3Count);
   }
}
