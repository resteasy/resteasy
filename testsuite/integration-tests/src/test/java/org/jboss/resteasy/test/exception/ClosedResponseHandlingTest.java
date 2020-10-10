package org.jboss.resteasy.test.exception;

import java.lang.reflect.ReflectPermission;

import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.NotSupportedException;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.exception.resource.ClosedResponseHandlingPleaseMapExceptionMapper;
import org.jboss.resteasy.test.exception.resource.ClosedResponseHandlingResource;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

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

   @Deployment
   public static Archive<?> deploy() {
      WebArchive war = TestUtil.prepareArchive(ClosedResponseHandlingTest.class.getSimpleName());
      war.addClass(ClosedResponseHandlingTest.class);
      war.addPackage(ClosedResponseHandlingResource.class.getPackage());
      war.addClass(PortProviderUtil.class);
      war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
           new ReflectPermission("suppressAccessChecks")
      ), "permissions.xml");

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
      WebTarget behaviorTarget = new ResteasyClientBuilder().build().target(generateURL("/behavior"));
      try {
         Response behaviorResponse = behaviorTarget.path("true").request().get();
         Assert.assertEquals(204, behaviorResponse.getStatus());
         new ResteasyClientBuilder().build().target(generateURL("/testNotAcceptable")).request().get(String.class);
      } finally {
         Response behaviorResponse = behaviorTarget.path("false").request().get();
         Assert.assertEquals(204, behaviorResponse.getStatus());
      }
   }

   /**
    * @tpTestDetails RESTEasy client errors that result in a closed Response are correctly handled.
    *                Note that the default behavior has changed after RESTEASY-2728.
    * @tpPassCrit An NotAcceptableException is returned
    * @tpSince RESTEasy 3.6.3
    */
   @Test(expected = NotAcceptableException.class)
   public void testNotAcceptableNewBehavior() {
      new ResteasyClientBuilder().build().target(generateURL("/testNotAcceptable")).request().get(String.class);
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
      WebTarget behaviorTarget = new ResteasyClientBuilder().build().target(generateURL("/behavior"));
      try {
         Response behaviorResponse = behaviorTarget.path("true").request().get();
         Assert.assertEquals(204, behaviorResponse.getStatus());
         new ResteasyClientBuilder().build().target(generateURL("/testNotSupportedTraced")).request().get(String.class);
      } finally {
         Response behaviorResponse = behaviorTarget.path("false").request().get();
         Assert.assertEquals(204, behaviorResponse.getStatus());
      }
   }

   /**
    * @tpTestDetails Closed Response instances should be handled correctly with full tracing enabled.
    *                Note that the default behavior has changed after RESTEASY-2728.
    * @tpPassCrit An NotSupportedException is returned
    * @tpSince RESTEasy 3.6.3
    */
   @Test(expected = NotSupportedException.class)
   public void testNotSupportedTracedNewBehavior() {
      new ResteasyClientBuilder().build().target(generateURL("/testNotSupportedTraced")).request().get(String.class);
   }

   private static String generateURL(String path) {
      return PortProviderUtil.generateURL(path, ClosedResponseHandlingTest.class.getSimpleName());
   }
}
