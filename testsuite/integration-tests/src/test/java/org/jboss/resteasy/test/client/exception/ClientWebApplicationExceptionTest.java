package org.jboss.resteasy.test.client.exception;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.NotAllowedException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.NotSupportedException;
import javax.ws.rs.RedirectionException;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.ServiceUnavailableException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.exception.ResteasyBadRequestException;
import org.jboss.resteasy.client.exception.ResteasyClientErrorException;
import org.jboss.resteasy.client.exception.ResteasyForbiddenException;
import org.jboss.resteasy.client.exception.ResteasyInternalServerErrorException;
import org.jboss.resteasy.client.exception.ResteasyNotAcceptableException;
import org.jboss.resteasy.client.exception.ResteasyNotAllowedException;
import org.jboss.resteasy.client.exception.ResteasyNotAuthorizedException;
import org.jboss.resteasy.client.exception.ResteasyNotFoundException;
import org.jboss.resteasy.client.exception.ResteasyNotSupportedException;
import org.jboss.resteasy.client.exception.ResteasyRedirectionException;
import org.jboss.resteasy.client.exception.ResteasyServerErrorException;
import org.jboss.resteasy.client.exception.ResteasyServiceUnavailableException;
import org.jboss.resteasy.client.exception.ResteasyWebApplicationException;
import org.jboss.resteasy.test.client.exception.resource.ClientWebApplicationExceptionApplication;
import org.jboss.resteasy.test.client.exception.resource.ClientWebApplicationExceptionResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Client throws ResteasyWebApplicationException on server side
 * @tpSince RESTEasy 3.14.0.Final
 * @tpTestCaseDetails Test ResteasyWebApplicationException and WebApplicationException in various circumstances
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ClientWebApplicationExceptionTest {

   public  static Response commonResponse = Response.ok("msg").status(444).header("foo", "bar").build();

   private static ResponseBuilder commonBuilder = Response.ok("msg").header("foo", "bar");

   public static WebApplicationException[] oldExceptions = {
         // The first four WebApplicationExceptions test the four WebApplicationException constructors
         // that take a Response parameter.
         new WebApplicationException(commonResponse),
         new WebApplicationException("msg", commonResponse),
         new WebApplicationException(new Exception(), commonResponse),
         new WebApplicationException("msg", new Exception(), commonResponse),

         // The other WebApplicationExceptions test the WebApplicationException subclasses that are
         // thrown according to the status used. The relationship between status and subclass is given
         // in oldExceptionMap.
         new WebApplicationException(commonBuilder.status(333).build()),
         new WebApplicationException(commonBuilder.status(400).build()),
         new WebApplicationException(commonBuilder.status(401).build()),
         new WebApplicationException(commonBuilder.status(403).build()),
         new WebApplicationException(commonBuilder.status(404).build()),
         new WebApplicationException(commonBuilder.status(405).build()),
         new WebApplicationException(commonBuilder.status(406).build()),
         new WebApplicationException(commonBuilder.status(415).build()),
         new WebApplicationException(commonBuilder.status(500).build()),
         new WebApplicationException(commonBuilder.status(503).build()),
         new WebApplicationException(commonBuilder.status(555).build()),
   };

   public static Map<Integer, Class<?>> oldExceptionMap = new HashMap<Integer, Class<?>>();
   static {
      oldExceptionMap.put(333, RedirectionException.class);
      oldExceptionMap.put(400, BadRequestException.class);
      oldExceptionMap.put(401, NotAuthorizedException.class);
      oldExceptionMap.put(403, ForbiddenException.class);
      oldExceptionMap.put(404, NotFoundException.class);
      oldExceptionMap.put(405, NotAllowedException.class);
      oldExceptionMap.put(406, NotAcceptableException.class);
      oldExceptionMap.put(415, NotSupportedException.class);
      oldExceptionMap.put(444, ClientErrorException.class);
      oldExceptionMap.put(500, InternalServerErrorException.class);
      oldExceptionMap.put(503, ServiceUnavailableException.class);
      oldExceptionMap.put(555, ServerErrorException.class);
   }

   public static ResteasyWebApplicationException[] newExceptions = {
         // The first four ResteasyWebApplicationExceptions test the four ResteasyWebApplicationException
         // constructors that take a Response parameter.
         new ResteasyWebApplicationException(commonResponse),
         new ResteasyWebApplicationException("msg", commonResponse),
         new ResteasyWebApplicationException(new Exception(), commonResponse),
         new ResteasyWebApplicationException("msg", new Exception(), commonResponse),

         // The other ResteasyWebApplicationExceptions test the ResteasyWebApplicationExceptions subclasses
         // that are thrown according to the status used. The relationship between status and subclass is given
         // in newExceptionMap.
         new ResteasyWebApplicationException(commonBuilder.status(333).build()),
         new ResteasyWebApplicationException(commonBuilder.status(400).build()),
         new ResteasyWebApplicationException(commonBuilder.status(401).build()),
         new ResteasyWebApplicationException(commonBuilder.status(403).build()),
         new ResteasyWebApplicationException(commonBuilder.status(404).build()),
         new ResteasyWebApplicationException(commonBuilder.status(405).build()),
         new ResteasyWebApplicationException(commonBuilder.status(406).build()),
         new ResteasyWebApplicationException(commonBuilder.status(415).build()),
         new ResteasyWebApplicationException(commonBuilder.status(500).build()),
         new ResteasyWebApplicationException(commonBuilder.status(503).build()),
         new ResteasyWebApplicationException(commonBuilder.status(555).build()),
   };

   public static Map<Integer, Class<?>> newExceptionMap = new HashMap<Integer, Class<?>>();
   static {
      newExceptionMap.put(333, ResteasyRedirectionException.class);
      newExceptionMap.put(400, ResteasyBadRequestException.class);
      newExceptionMap.put(401, ResteasyNotAuthorizedException.class);
      newExceptionMap.put(403, ResteasyForbiddenException.class);
      newExceptionMap.put(404, ResteasyNotFoundException.class);
      newExceptionMap.put(405, ResteasyNotAllowedException.class);
      newExceptionMap.put(406, ResteasyNotAcceptableException.class);
      newExceptionMap.put(415, ResteasyNotSupportedException.class);
      newExceptionMap.put(444, ResteasyClientErrorException.class);
      newExceptionMap.put(500, ResteasyInternalServerErrorException.class);
      newExceptionMap.put(503, ResteasyServiceUnavailableException.class);
      newExceptionMap.put(555, ResteasyServerErrorException.class);
   }

   private static Client client;
   private static WebTarget behaviorTarget;
   private static WebTarget target;

   @BeforeClass
   public static void beforeClass() throws Exception {
      client = ClientBuilder.newClient();
      behaviorTarget = client.target(generateURL("/app/test/behavior/"));
      target = client.target(generateURL("/app/test/"));
   }

   @AfterClass
   public static void stop() throws Exception {
      client.close();
   }

   @Deployment
   public static Archive<?> deploy() {
      WebArchive war = TestUtil.prepareArchive(ClientWebApplicationExceptionTest.class.getSimpleName());
      war.addClass(ClientWebApplicationExceptionTest.class);
      war.addClass(ClientWebApplicationExceptionApplication.class);
      war.addClass(ClientWebApplicationExceptionResource.class);
      war.addClass(PortProviderUtil.class);
      war.addClass(TestUtil.class);
      return TestUtil.finishContainerPrepare(war, null, ClientWebApplicationExceptionResource.class);
   }

   public static String generateURL(String path) {
      return PortProviderUtil.generateURL(path, ClientWebApplicationExceptionTest.class.getSimpleName());
   }

   ////////////////////////////////////////////////////////////////////////////////////////////
   /**
    * @tpTestDetails For each WebApplicationException in oldExceptions, calls the resource method oldException() to throw
    *                that WebApplicationException. Since it is running on the client side, the standard behavior of throwing a
    *                WebApplicationException will occur. That WebApplicationException should match the WebApplicationException
    *                thrown by oldException().
    *
    * @tpSince RESTEasy 3.14.0.Final
    */
   @Test
   public void testOldExceptionsDirectly() {
      for (int i = 0; i < oldExceptions.length; i++) {
         try {
            target.path("exception/old/" + i).request().get(String.class);
            Assert.fail("expected exception");
         } catch (ResteasyWebApplicationException rwae) {
            Assert.fail("Didn't expect ResteasyWebApplicationException");
         } catch (WebApplicationException wae) {
            Response response = wae.getResponse();
            WebApplicationException oldException = oldExceptions[i];
            Assert.assertEquals(oldException.getResponse().getStatus(), response.getStatus());
            Assert.assertEquals(oldException.getResponse().getHeaderString("foo"), response.getHeaderString("foo"));
            Assert.assertEquals(oldException.getResponse().getEntity(), response.readEntity(String.class));
            Assert.assertEquals(oldExceptionMap.get(response.getStatus()), wae.getClass());
         } catch (Exception e) {
            Assert.fail("expected WebApplicationException");
         }

      }
   }

   /**
    *  @tpTestDetails For each ResteasyWebApplicationException in newExceptions, calls the resource method newException() to throw
    *                 that ResteasyWebApplicationException. Since it is running on the client side, the standard behavior of throwing a
    *                 WebApplicationException will occur. That WebApplicationException should match the result returned by newException(),
    *                 which has a status of 500 and an entity representing a stacktrace.
    * @tpSince RESTEasy 3.14.0.Final
    */
   @Test
   public void testNewExceptionsDirectly() throws Exception {
      for (int i = 0; i < newExceptions.length; i++) {
         try {
            target.path("exception/new/" + i).request().get(String.class);
            Assert.fail("expected exception");
         } catch (ResteasyWebApplicationException rwae) {
            Assert.fail("Didn't expect ResteasyWebApplicationException");
         } catch (WebApplicationException e) {
            Response response = e.getResponse();
            Assert.assertEquals(500, response.getStatus());
            Assert.assertNull(response.getHeaderString("foo"));
            Assert.assertTrue(response.readEntity(String.class).contains("Caused by"));
            Assert.assertEquals(oldExceptionMap.get(response.getStatus()), e.getClass());
         }
      }
   }

   /**
    * @tpTestDetails  1. The value of ResteasyContextParameters.RESTEASY_ORIGINAL_WEBAPPLICATIONEXCEPTION_BEHAVIOR is
    *                    set to "true" to compel the original Client behavior on the server side.
    *
    *                 2. For each WebApplicationException in oldExceptions, the resource method noCatchOld() is called.
    *
    *                 3. noCatchOld() calls oldException(), which throws the chosen member of oldExceptions. The resulting
    *                    HTTP response contains the status, headers, and entity in that WebApplicationException.
    *
    *                 4. In noCatchOld(), the original behavior causes the HTTP response to be turned into a WebApplicationException,
    *                    which is thrown by the Client. The resulting HTTP response contains the status, headers, and entity in that
    *                    WebApplicationException.
    *
    *                 5. The client side Client constructs and throws a WebApplicationException which is checked against the matching
    *                    WebApplicationException in oldExceptins.
    *
    * @tpSince RESTEasy 3.14.0.Final
    */
   @Test
   public void testNoCatchOldBehaviorOldExceptions() throws Exception {
      Response behaviorResponse = behaviorTarget.path("true").request().get();
      Assert.assertEquals(204, behaviorResponse.getStatus());
      try {
         for (int i = 0; i < oldExceptions.length; i++) {
            try {
               target.path("nocatch/old/" + i).request().get(String.class);
               Assert.fail("expected exception");
            } catch (ResteasyWebApplicationException rwae) {
               Assert.fail("Didn't expect ResteasyWebApplicationException");
            } catch (WebApplicationException e) {
               Response response = e.getResponse();
               WebApplicationException wae = oldExceptions[i];
               Assert.assertEquals(wae.getResponse().getStatus(), response.getStatus());
               Assert.assertEquals(wae.getResponse().getHeaderString("foo"), response.getHeaderString("foo"));
               Assert.assertEquals(wae.getResponse().getEntity(), response.readEntity(String.class));
               Assert.assertEquals(oldExceptionMap.get(response.getStatus()), e.getClass());
            } catch (Exception e) {
               Assert.fail("expected WebApplicationException");
            }
         }
      } finally {
         behaviorResponse = behaviorTarget.path("false").request().get();
         Assert.assertEquals(204, behaviorResponse.getStatus());
      }
   }

   /**
    * @tpTestDetails 1. The value of ResteasyContextParameters.RESTEASY_ORIGINAL_WEBAPPLICATIONEXCEPTION_BEHAVIOR is
    *                   set to "true" to compel the original Client behavior on the server side.
    *
    *                2. For each ResteasyWebApplicationException in newExceptions, the resource method noCatchNew() is called.
    *
    *                3. noCatchNew() calls newException(), which throws the matching member of newExceptions. The resulting
    *                   HTTP response has status 500, no added headers, and an entity that represents a stacktrace.
    *
    *                4. In noCatchNew(), the original behavior causes the HTTP response to be turned into a WebApplicationException,
    *                   which is thrown by the Client. The resulting HTTP response has status 500, no added headers, and an entity
    *                   that represents a stacktrace.
    *
    *                5. The client side Client constructs and throws a WebApplicationException which is checked for status 500, no
    *                   added headers, and entity representing a stacktrace.
    *
    * @tpSince RESTEasy 3.14.0.Final
    */
   @Test
   public void testNoCatchOldBehaviorNewExceptions() throws Exception {
      Response behaviorResponse = behaviorTarget.path("true").request().get();
      Assert.assertEquals(204, behaviorResponse.getStatus());
      try {
         for (int i = 0; i < newExceptions.length; i++) {
            try {
               target.path("nocatch/new/" + i).request().get(String.class);
               Assert.fail("expected exception");
            } catch (ResteasyWebApplicationException rwae) {
               Assert.fail("Didn't expect ResteasyWebApplicationException");
            } catch (WebApplicationException e) {
               Response response = e.getResponse();
               Assert.assertEquals(500, response.getStatus());
               Assert.assertNull(response.getHeaderString("foo"));
               Assert.assertTrue(response.readEntity(String.class).contains("Caused by"));
               Assert.assertEquals(oldExceptionMap.get(response.getStatus()), e.getClass());
            } catch (Exception e) {
               Assert.fail("expected WebApplicationException");
            }
         }
      } finally {
         behaviorResponse = behaviorTarget.path("false").request().get();
         Assert.assertEquals(204, behaviorResponse.getStatus());
      }
   }

   /**
    * @tpTestDetails 1. For each WebApplicationException in oldExceptions, the resource method noCatchOld() is called.
    *
    *                2. noCatchOld() calls oldException(), which throws the matching member of oldExceptions. The resulting
    *                   HTTP response contains the status, headers, and entity in that WebApplicationException.
    *
    *                3. In noCatchOld(), the new behavior causes the HTTP response to be turned into a ResteasyWebApplicationException,
    *                   which is thrown by the Client. ResteasyWebApplicationException.getResponse() returns null, so the resulting
    *                   HTTP response has status 500, no added headers, and an entity that represents a stacktrace.
    *
    *                4. The client side Client constructs and throws a WebApplicationException which is checked for status 500, no
    *                   added headers, and entity representing a stacktrace.
    *
    * @tpSince RESTEasy 3.14.0.Final
    */
   @Test
   public void testNoCatchNewBehaviorOldExceptions() throws Exception {
      for (int i = 0; i < oldExceptions.length; i++) {
         try {
            target.path("nocatch/old/" + i).request().get(String.class);
            Assert.fail("expected exception");
         } catch (ResteasyWebApplicationException rwae) {
            Assert.fail("Didn't expect ResteasyWebApplicationException");
         } catch (WebApplicationException e) {
            Response response = e.getResponse();
            Assert.assertEquals(500, response.getStatus());
            Assert.assertNull(response.getHeaderString("foo"));
            Assert.assertTrue(response.readEntity(String.class).contains("Caused by"));
            Assert.assertEquals(oldExceptionMap.get(response.getStatus()), e.getClass());
         } catch (Exception e) {
            Assert.fail("expected WebApplicationException");
         }
      }
   }

   /**
    * @tpTestDetails 1. For each ResteasyWebApplicationException in newExceptions, the resource method noCatchNew() is called.
    *
    *                2. noCatchNew() calls newException(), which throws the matching member of newExceptions. Since
    *                   ResteasyWebApplicationException.getResponse() returns null, the resulting
    *                   HTTP response has status 500, no added headers, and an entity that represents a stacktrace.
    *
    *                3. In noCatchNew(), the new behavior causes the HTTP response to be turned into a ResteasyWebApplicationException,
    *                   which is thrown by the Client. The resulting  HTTP response has status 500, no added headers, and an entity
    *                   that represents a stacktrace.
    *
    *                4. The client side Client constructs and throws a WebApplicationException which is checked for status 500, no
    *                   added headers, and entity representing a stacktrace.
    *
    * @tpSince RESTEasy 3.14.0.Final
    */
   @Test
   public void testNoCatchNewBehaviorNewExceptions() throws Exception {
      for (int i = 0; i < newExceptions.length; i++) {
         try {
            target.path("nocatch/new/" + i).request().get(String.class);
            Assert.fail("expected exception");
         } catch (ResteasyWebApplicationException rwae) {
            Assert.fail("Didn't expect ResteasyWebApplicationException");
         } catch (WebApplicationException e) {
            Response response = e.getResponse();
            Assert.assertEquals(500, response.getStatus());
            Assert.assertNull(response.getHeaderString("foo"));
            Assert.assertTrue(response.readEntity(String.class).contains("Caused by"));
            Assert.assertEquals(oldExceptionMap.get(response.getStatus()), e.getClass());
         } catch (Exception e) {
            Assert.fail("expected WebApplicationException");
         }
      }
   }

   /**
    * @tpTestDetails 1. The value of ResteasyContextParameters.RESTEASY_ORIGINAL_WEBAPPLICATIONEXCEPTION_BEHAVIOR is
    *                   set to "true" to compel the original Client behavior on the server side.
    *
    *                2. For each WebApplicationException in oldExceptions, the resource method catchOldOld() is called.
    *
    *                3. catchOldOld() calls oldException(), which throws the chosen member of oldExceptions. The resulting
    *                   HTTP response contains the status, headers, and entity in that WebApplicationException.
    *
    *                4. In catchOldOld(), the original behavior causes the HTTP response to be turned into a WebApplicationException,
    *                   which is thrown by the Client. That WebApplicationException is caught, verified to match the matching
    *                   WebApplicationException in oldExceptins, and then rethrown. The resulting HTTP response contains the
    *                   status, headers, and entity in that WebApplicationException.
    *
    *                5. The client side Client constructs and throws a WebApplicationException which is checked against the matching
    *                   WebApplicationException in oldExceptins.
    *
    * @tpSince RESTEasy 3.14.0.Final
    */
   @Test
   public void testCatchOldBehaviorOldExceptions() throws Exception {
      Response behaviorResponse = behaviorTarget.path("true").request().get();
      Assert.assertEquals(204, behaviorResponse.getStatus());
      try {
         for (int i = 0; i < oldExceptions.length; i++) {
            try {
               target.path("catch/old/old/" + i).request().get(String.class);
               Assert.fail("expected exception");
            } catch (ResteasyWebApplicationException rwae) {
               Assert.fail("Didn't expect ResteasyWebApplicationException");
            } catch (WebApplicationException e) {
               Response response = e.getResponse();
               Assert.assertEquals(oldExceptions[i].getResponse().getStatus(), response.getStatus());
               Assert.assertEquals(oldExceptions[i].getResponse().getHeaderString("foo"), response.getHeaderString("foo"));
               Assert.assertEquals(oldExceptions[i].getResponse().getEntity(), response.readEntity(String.class));
               Assert.assertEquals(oldExceptionMap.get(response.getStatus()), e.getClass());
            } catch (Exception e) {
               Assert.fail("expected WebApplicationException");
            }
         }
      } finally {
         behaviorResponse = behaviorTarget.path("false").request().get();
         Assert.assertEquals(204, behaviorResponse.getStatus());
      }
   }

   /**
    * @tpTestDetails 1. The value of ResteasyContextParameters.RESTEASY_ORIGINAL_WEBAPPLICATIONEXCEPTION_BEHAVIOR is
    *                   set to "true" to compel the original Client behavior on the server side.
    *
    *                2. For each ResteasyWebApplicationException in newExceptions, the resource method catchOldNew() is called.
    *
    *                3. catchOldNew() calls newException(), which throws the chosen member of newExceptions. Since
    *                   ResteasyWebApplicationException.getResponse() returns null, the resulting
    *                   HTTP response has status 500, no added headers, and an entity that represents a stacktrace..
    *
    *                4. In catchOldNew(), the original behavior causes the HTTP response to be turned into a WebApplicationException,
    *                   which is thrown by the Client. That WebApplicationException is caught, verified to
    *                   have status 500, no added headers, and an entity that represents a stacktrace, and then rethrown.
    *                   The resulting HTTP response has status 500, no added headers, and an entity that represents a stacktrace.
    *
    *                5. The client side Client constructs and throws a WebApplicationException which is verified to have
    *                   status 500, no added headers, and an entity that represents a stacktrace. Its class is also verified
    *                   to correspond to the ResteasyWebApplicationException thrown by newException() on the server.
    *
    * @tpSince RESTEasy 3.14.0.Final
    */
   @Test
   public void testCatchOldBehaviorNewExceptions() throws Exception {
      Response behaviorResponse = behaviorTarget.path("true").request().get();
      Assert.assertEquals(204, behaviorResponse.getStatus());
      try {
         for (int i = 0; i < newExceptions.length; i++) {
            try {
               target.path("catch/old/new/" + i).request().get(String.class);
               Assert.fail("expected exception");
            } catch (ResteasyWebApplicationException e) {
               Assert.fail("didn't expect ResteasyWebApplicationException");
            } catch (WebApplicationException e) {
               Response response = e.getResponse();
               Assert.assertNotNull(response);
               Assert.assertEquals(500, response.getStatus());
               Assert.assertNull(response.getHeaderString("foo"));
               Assert.assertTrue(response.readEntity(String.class).contains("Caused by"));
               Assert.assertEquals(oldExceptionMap.get(response.getStatus()), e.getClass());
            } catch (Exception e) {
               Assert.fail("expected WebApplicationException");
            }
         }
      } finally {
         behaviorResponse = behaviorTarget.path("false").request().get();
         Assert.assertEquals(204, behaviorResponse.getStatus());
      }
   }

   /**
    * @tpTestDetails 1. For each WebApplicationException in oldExceptions, the resource method catchNewOld() is called.
    *
    *                2. catchNewOld() calls oldException(), which throws the matching member of oldExceptions. The resulting
    *                   HTTP response contains the status, headers, and entity in that WebApplicationException.
    *
    *                3. In catchNewOld(), the new behavior causes the HTTP response to be turned into a ResteasyWebApplicationException,
    *                   which is thrown by the Client, caught, tested, and rethrown. The ResteasyWebApplicationException.getOriginalResponse()
    *                   should match the Response in the matching WebApplicationException. The HTTP response constructed from the rethrown
    *                   ResteasyWebApplicationException will have status 500, no added headers, and an entity that represents a
    *                   stacktrace.
    *
    *                4. The client side Client constructs and throws a WebApplicationException which is checked for status 500, no
    *                   added headers, and entity representing a stacktrace.
    *
    * @tpSince RESTEasy 3.14.0.Final
    */
   @Test
   public void testCatchNewBehaviorOldExceptions() throws Exception {
      for (int i = 0; i < oldExceptions.length; i++) {
         try {
            target.path("catch/new/old/" + i).request().get(String.class);
            Assert.fail("expected exception");
         } catch (ResteasyWebApplicationException e) {
            Assert.fail("didn't expect ResteasyWebApplicationException");
         } catch (WebApplicationException e) {
            Response response = e.getResponse();
            Assert.assertNotNull(response);
            Assert.assertEquals(500, response.getStatus());
            Assert.assertNull(response.getHeaderString("foo"));
            Assert.assertTrue(response.readEntity(String.class).contains("Caused by"));
            Assert.assertEquals(oldExceptionMap.get(response.getStatus()), e.getClass());
         } catch (Exception e) {
            Assert.fail("expected WebApplicationException");
         }
      }
   }

   /**
    * @tpTestDetails 1. For each ResteasyWebApplicationException in newExceptions, the resource method catchNewNew() is called.
    *
    *                2. catchNewNew() calls newException(), which throws the matching member of newExceptions. The resulting
    *                   HTTP response will have status 500, no added headers, and an entity that represents a
    *                   stacktrace.
    *
    *                3. In catchNewNew(), the new behavior causes the HTTP response to be turned into a ResteasyWebApplicationException,
    *                   which is thrown by the Client, caught, tested, and rethrown. The ResteasyWebApplicationException.getOriginalResponse()
    *                   should have status 500, no added headers, and an entity that represents a stacktrace.The HTTP response constructed
    *                   from the rethrown ResteasyWebApplicationException will have status 500, no added headers, and an entity
    *                   that represents a stacktrace.
    *
    *                4. The client side Client constructs and throws a WebApplicationException which is checked for status 500, no
    *                   added headers, and entity representing a stacktrace.
    *
    * @tpSince RESTEasy 3.14.0.Final
    */
   @Test
   public void testCatchNewBehaviorNewExceptions() throws Exception {
      for (int i = 0; i < newExceptions.length; i++) {
         try {
            target.path("catch/new/new/" + i).request().get(String.class);
            Assert.fail("expected exception");
         } catch (ResteasyWebApplicationException e) {
            Assert.fail("didn't expect ResteasyWebApplicationException");
         } catch (WebApplicationException e) {
            Response response = e.getResponse();
            Assert.assertNotNull(response);
            Assert.assertEquals(500, response.getStatus());
            Assert.assertNull(response.getHeaderString("foo"));
            Assert.assertTrue(response.readEntity(String.class).contains("Caused by"));
            Assert.assertEquals(oldExceptionMap.get(response.getStatus()), e.getClass());
         } catch (Exception e) {
            Assert.fail("expected WebApplicationException");
         }
      }
   }
}
