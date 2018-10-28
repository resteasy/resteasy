package org.jboss.resteasy.test.exception;

import java.io.IOException;
import java.lang.reflect.ReflectPermission;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.NotSupportedException;
import javax.ws.rs.Path;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.tracing.RESTEasyTracingLogger;
import org.jboss.resteasy.tracing.api.RESTEasyTracing;
import org.jboss.resteasy.tracing.api.RESTEasyTracingLevel;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import static javax.ws.rs.core.Response.Status.NOT_ACCEPTABLE;
import static javax.ws.rs.core.Response.Status.UNSUPPORTED_MEDIA_TYPE;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpSince RESTEasy 4.0.0.CR1
 * @tpTestCaseDetails Regression test for RESTEASY-1142
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @author <a href="jonas.zeiger@talpidae.net">Jonas Zeiger</a>
 * @version $Revision: 1.0 $
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ClosedResponseHandlingTest {

   @Deployment
   public static Archive<?> deploy() {
       WebArchive war = TestUtil.prepareArchive(ClosedResponseHandlingTest.class.getSimpleName());
       war.addClass(ClosedResponseHandlingTest.class);
       war.addClass(PortProviderUtil.class);
       war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
             new ReflectPermission("suppressAccessChecks")
       ), "permissions.xml");

       Map<String, String> params = new HashMap<>();
       params.put(ResteasyContextParameters.RESTEASY_TRACING_TYPE, ResteasyContextParameters.RESTEASY_TRACING_TYPE_ALL);
       params.put(ResteasyContextParameters.RESTEASY_TRACING_THRESHOLD, ResteasyContextParameters.RESTEASY_TRACING_LEVEL_VERBOSE);

       return TestUtil.finishContainerPrepare(war, params, TestResource.class,
             PleaseMapExceptionMapper.class,
             TestEnableVerboseTracingRequestFilter.class);
    }

   /**
    * @tpTestDetails Request is sent to an endpoint that issues a Resteasy client request triggering a 404 error.
    * @tpPassCrit A NotAcceptableException is returned
    * @tpSince RESTEasy 4.0.0.CR1
    */
   @Test(expected = NotAcceptableException.class)
   public void testNotAcceptable() {
      new ResteasyClientBuilderImpl().build().target(generateURL("/testNotAcceptable")).request().get(String.class);
   }

   /**
    * @tpTestDetails Request is sent to an exception mapping and tracing endpoint that issues a Resteasy client request triggering a 404 error.
    * @tpPassCrit A NotSupportedException is returned
    * @tpSince RESTEasy 4.0.0.CR1
    */
   @Test(expected = NotSupportedException.class)
   public void testNotSupportedTraced() {
      new ResteasyClientBuilderImpl().build().target(generateURL("/testNotSupportedTraced")).request().get(String.class);
   }

   @Path("")
   public static class TestResource {

      @Path("/testNotAcceptable/406")
      @GET
      public Response errorNotAcceptable() {
         return Response.status(NOT_ACCEPTABLE).build();
      }

      @Path("/testNotAcceptable")
      @GET
      public String getNotAcceptable(@Context UriInfo uriInfo) {
         URI endpoint406 = UriBuilder.fromUri(uriInfo.getRequestUri()).path("406").build();
         return ClientBuilder.newClient().target(endpoint406).request().get(String.class);
      }

      @Path("/testNotSupportedTraced/415")
      @GET
      public Response errorNotFound() {
         return Response.status(UNSUPPORTED_MEDIA_TYPE).build();
      }

      @Path("/testNotSupportedTraced")
      @GET
      public String getNotSupportedTraced(@Context UriInfo uriInfo) {
         URI endpoint415 = UriBuilder.fromUri(uriInfo.getRequestUri()).path("415").build();
         try {
            return ClientBuilder.newClient().target(endpoint415).request().get(String.class);
         } catch(NotSupportedException e) {
            throw new PleaseMapException(e.getResponse());
         }
      }
   }

   private static class PleaseMapException extends RuntimeException {
      private final Response response;

      private PleaseMapException(Response response) {
         this.response = response;
      }
   }

   /** ExceptionHandler only uses the logger for tracing exception mapping, so we need to map something.
    */
   @Provider
   public static class PleaseMapExceptionMapper implements ExceptionMapper<PleaseMapException> {
      @Override
      public Response toResponse(PleaseMapException e) {
         return e.response;
      }
   }

   @Provider
   @PreMatching
   public static class TestEnableVerboseTracingRequestFilter implements ContainerRequestFilter {
      @Override
      public void filter(ContainerRequestContext containerRequestContext) throws IOException {
         // force verbose tracing, enabling via finishContainerPrepare()'s contextParams didn't work
         containerRequestContext.setProperty(RESTEasyTracing.PROPERTY_NAME,
               RESTEasyTracingLogger.create(RESTEasyTracingLevel.VERBOSE.name(), ClosedResponseHandlingTest.class.getSimpleName()));
      }
   }

   private static String generateURL(String path) {
      return PortProviderUtil.generateURL(path, ClosedResponseHandlingTest.class.getSimpleName());
   }
}
