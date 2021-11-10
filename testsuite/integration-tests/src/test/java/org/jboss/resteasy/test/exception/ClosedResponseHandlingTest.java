package org.jboss.resteasy.test.exception;

import java.lang.reflect.ReflectPermission;
import java.util.HashMap;
import java.util.Map;

import jakarta.ws.rs.NotAcceptableException;
import jakarta.ws.rs.NotSupportedException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.category.TracingRequired;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.test.exception.resource.ClosedResponseHandlingEnableTracingRequestFilter;
import org.jboss.resteasy.test.client.exception.ClientWebApplicationExceptionResteasyProxyTest;
import org.jboss.resteasy.test.exception.resource.ClosedResponseHandlingPleaseMapExceptionMapper;
import org.jboss.resteasy.test.exception.resource.ClosedResponseHandlingResource;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static org.jboss.resteasy.utils.PortProviderUtil.generateURL;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpSince RESTEasy 4.0.0.CR1
 * @tpTestCaseDetails Regression test for RESTEASY-1142
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @author <a href="jonas.zeiger@talpidae.net">Jonas Zeiger</a>
 */
@RunWith(Arquillian.class)
@RunAsClient
@Category(TracingRequired.class)
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

       Map<String, String> params = new HashMap<>();
       params.put(ResteasyContextParameters.RESTEASY_TRACING_TYPE, ResteasyContextParameters.RESTEASY_TRACING_TYPE_ALL);
       params.put(ResteasyContextParameters.RESTEASY_TRACING_THRESHOLD, ResteasyContextParameters.RESTEASY_TRACING_LEVEL_VERBOSE);

       return TestUtil.finishContainerPrepare(war, params, ClosedResponseHandlingResource.class,
             ClosedResponseHandlingPleaseMapExceptionMapper.class,
             ClosedResponseHandlingEnableTracingRequestFilter.class);
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
    * @tpPassCrit A NotAcceptableException is returned
    * @tpSince RESTEasy 4.0.0.CR1
    */
   @Test(expected = NotAcceptableException.class)
   public void testNotAcceptable() {
       new ResteasyClientBuilderImpl().build().target(generateURL("/testNotAcceptable", oldBehaviorDeploymentName)).request().get(String.class);
   }

   /**
    * @tpTestDetails RESTEasy client errors that result in a closed Response are correctly handled.
    *                Note that the default behavior has changed after RESTEASY-2728.
    * @tpPassCrit An NotAcceptableException is returned
    * @tpSince RESTEasy 4.6.0
    */
   @Test(expected = NotAcceptableException.class)
   public void testNotAcceptableNewBehavior() {
      new ResteasyClientBuilderImpl().build().target(generateURL("/testNotAcceptable", newBehaviorDeploymentName)).request().get(String.class);
   }

   /**
    * @tpTestDetails RESTEasy client errors that result in a closed Response are correctly handled.
    *                Note that ResteasyContextParameters.RESTEASY_ORIGINAL_WEBAPPLICATIONEXCEPTION_BEHAVIOR
    *                must be set to "true" to enforce old Client behavior.
    * @tpPassCrit A NotAcceptableException is returned
    * @tpSince RESTEasy 4.0.0.CR1
    */
   @Test(expected = NotSupportedException.class)
   public void testNotSupportedTraced() {

      new ResteasyClientBuilderImpl().build().target(generateURL("/testNotSupportedTraced", oldBehaviorDeploymentName)).request().get(String.class);
   }

   /**
    * @tpTestDetails Closed Response instances should be handled correctly with full tracing enabled.
    *                Note that the default behavior has changed after RESTEASY-2728.
    * @tpPassCrit An NotSupportedException is returned
    * @tpSince RESTEasy 4.6.0
    */
   @Test(expected = NotSupportedException.class)
   public void testNotSupportedTracedNewBehavior() {

      new ResteasyClientBuilderImpl().build().target(generateURL("/testNotSupportedTraced", newBehaviorDeploymentName)).request().get(String.class);
   }
}
