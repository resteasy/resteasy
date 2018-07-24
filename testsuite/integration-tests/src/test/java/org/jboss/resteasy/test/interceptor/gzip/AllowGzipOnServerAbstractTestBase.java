package org.jboss.resteasy.test.interceptor.gzip;

import org.jboss.arquillian.container.test.api.ContainerController;
import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.After;
import org.junit.Before;

/**
 * Abstract base class for tests with gzip enabled on server side.
 *
 * This abstract class provides deployments and starts and stops custom server.
 *
 * This abstract class is extended by:
 *      AllowGzipOnServerAllowGzipOnClientTest
 *      AllowGzipOnServerNotAllowGzipOnClientTest
 */
public class AllowGzipOnServerAbstractTestBase extends GzipAbstractTestBase {

   /**
    * Name of server with allowed gzip
    */
   protected static final String GZIP_SERVER_NAME = "jbossas-manual-gzip";

   //keep in sync with offset in arquillian.xml
   protected static String gzipServerBaseUrl = "http://" + PortProviderUtil.getHost() + ":" + (PortProviderUtil.getPort() + 1000);

   @ArquillianResource
   protected ContainerController containerController;

   @ArquillianResource
   protected Deployer deployer;

   @Before
   public void startContainerWithGzipEnabledAndDeploy() {
      if (!containerController.isStarted(GZIP_SERVER_NAME)) {
         containerController.start(GZIP_SERVER_NAME);
      }

      deployer.deploy(WAR_WITH_PROVIDERS_FILE);
      deployer.deploy(WAR_WITHOUT_PROVIDERS_FILE);
   }

   @After
   public void undeployAndStopContainerWithGzipEnabled() {
      if (containerController.isStarted(GZIP_SERVER_NAME)) {
         deployer.undeploy(WAR_WITH_PROVIDERS_FILE);
         deployer.undeploy(WAR_WITHOUT_PROVIDERS_FILE);
         containerController.stop(GZIP_SERVER_NAME);
      }
   }

   /**
    * Deployment with javax.ws.rs.ext.Providers file, that contains gzip interceptor definition
    */
   @Deployment(name = WAR_WITH_PROVIDERS_FILE, managed = false, testable = false)
   @TargetsContainer(GZIP_SERVER_NAME)
   public static Archive<?> createWebDeploymentWithGzipProvidersFile() {
      return createWebArchive(WAR_WITH_PROVIDERS_FILE, true);
   }

   /**
    * Deployment without any javax.ws.rs.ext.Providers file
    */
   @Deployment(name = WAR_WITHOUT_PROVIDERS_FILE, managed = false, testable = false)
   @TargetsContainer(GZIP_SERVER_NAME)
   public static Archive<?> createWebDeploymentWithoutGzipProvidersFile() {
      return createWebArchive(WAR_WITHOUT_PROVIDERS_FILE, false);
   }
}
