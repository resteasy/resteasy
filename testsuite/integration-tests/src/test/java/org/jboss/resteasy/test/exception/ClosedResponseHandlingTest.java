package org.jboss.resteasy.test.exception;

import java.lang.reflect.ReflectPermission;

import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.NotSupportedException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.client.exception.ClientWebApplicationExceptionResteasyProxyTest;
import org.jboss.resteasy.test.exception.resource.ClosedResponseHandlingPleaseMapExceptionMapper;
import org.jboss.resteasy.test.exception.resource.ClosedResponseHandlingResource;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.jboss.resteasy.utils.PortProviderUtil.generateURL;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.6.3
 * @tpTestCaseDetails Regression test for RESTEASY-1142
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @author <a href="jonas.zeiger@talpidae.net">Jonas Zeiger</a>
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ClosedResponseHandlingTest {

   public static final String oldBehaviorDeploymentName = "OldBehaviorClosedResponseHandlingTest";
   public static final String newBehaviorDeploymentName = "NewBehaviorClosedResponseHandlingTest";

   @Deployment(name = oldBehaviorDeploymentName)
   public static Archive<?> deployOldBehaviour() {
      WebArchive war = TestUtil.prepareArchive(oldBehaviorDeploymentName);
      war.addClass(ClosedResponseHandlingTest.class);
      war.addPackage(ClosedResponseHandlingResource.class.getPackage());
      war.addClass(PortProviderUtil.class);
      war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
           new ReflectPermission("suppressAccessChecks")
      ), "permissions.xml");

      return TestUtil.finishContainerPrepare(war, null, ClosedResponseHandlingResource.class,
            ClosedResponseHandlingPleaseMapExceptionMapper.class);
   }

   @Deployment(name = newBehaviorDeploymentName)
   public static Archive<?> deployNewBehavior() {
      WebArchive war = TestUtil.prepareArchive(newBehaviorDeploymentName);
      war.addClass(ClosedResponseHandlingTest.class);
      war.addPackage(ClosedResponseHandlingResource.class.getPackage());
      war.addClass(PortProviderUtil.class);
      war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
           new ReflectPermission("suppressAccessChecks")
      ), "permissions.xml");
      war.setWebXML(ClientWebApplicationExceptionResteasyProxyTest.class.getPackage(), "webapplicationexception_web.xml");

      return TestUtil.finishContainerPrepare(war, null, ClosedResponseHandlingResource.class,
            ClosedResponseHandlingPleaseMapExceptionMapper.class);
   }

   /**
    * @tpTestDetails RESTEasy client errors that result in a closed Response are correctly handled.
    *                Note that ResteasyContextParameters.RESTEASY_ORIGINAL_WEBAPPLICATIONEXCEPTION_BEHAVIOR
    *                must be set to "true" to enforce old Client behavior.
    * @tpPassCrit A NotAcceptableException is returned
    * @tpSince RESTEasy 3.6.3
    */
   @Test(expected = NotAcceptableException.class)
   public void testNotAcceptable() {
      new ResteasyClientBuilder().build().target(generateURL("/testNotAcceptable", oldBehaviorDeploymentName)).request().get(String.class);
   }

   /**
    * @tpTestDetails RESTEasy client errors that result in a closed Response are correctly handled.
    *                Note that the default behavior has changed after RESTEASY-2728.
    * @tpPassCrit An NotAcceptableException is returned
    * @tpSince RESTEasy 3.6.3
    */
   @Test(expected = NotAcceptableException.class)
   public void testNotAcceptableNewBehavior() {
      new ResteasyClientBuilder().build().target(generateURL("/testNotAcceptable", newBehaviorDeploymentName)).request().get(String.class);
   }

   /**
    * @tpTestDetails RESTEasy client errors that result in a closed Response are correctly handled.
    *                Note that ResteasyContextParameters.RESTEASY_ORIGINAL_WEBAPPLICATIONEXCEPTION_BEHAVIOR
    *                must be set to "true" to enforce old Client behavior.
    * @tpPassCrit A NotAcceptableException is returned
    * @tpSince RESTEasy 3.6.3
    */
   @Test(expected = NotSupportedException.class)
   public void testNotSupportedTraced() {
      new ResteasyClientBuilder().build().target(generateURL("/testNotSupportedTraced", oldBehaviorDeploymentName)).request().get(String.class);
   }

   /**
    * @tpTestDetails Closed Response instances should be handled correctly with full tracing enabled.
    *                Note that the default behavior has changed after RESTEASY-2728.
    * @tpPassCrit An NotSupportedException is returned
    * @tpSince RESTEasy 3.6.3
    */
   @Test(expected = NotSupportedException.class)
   public void testNotSupportedTracedNewBehavior() {
      new ResteasyClientBuilder().build().target(generateURL("/testNotSupportedTraced", newBehaviorDeploymentName)).request().get(String.class);
   }
}
