package org.jboss.resteasy.test.client.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.exception.ResteasyWebApplicationException;
import org.jboss.resteasy.test.client.exception.resource.ClientWebApplicationExceptionApplication;
import org.jboss.resteasy.test.client.exception.resource.ClientWebApplicationExceptionExceptions;
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
 * @tpSince RESTEasy 3.11.3.Final
 * @tpTestCaseDetails Test ResteasyWebApplicationException and WebApplicationException in various circumstances
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ClientWebApplicationExceptionOldBehaviorTest {

   private static Client client;
   private static WebTarget target;

   @BeforeClass
   public static void beforeClass() throws Exception {
      client = ClientBuilder.newClient();
      target = client.target(ClientWebApplicationExceptionExceptions.generateURL("/app/test/"));
   }

   @AfterClass
   public static void stop() throws Exception {
      client.close();
   }

   @Deployment
   public static Archive<?> deploy() {
      WebArchive war = TestUtil.prepareArchive("ClientWebApplicationExceptionTest");
      war.addClass(ClientWebApplicationExceptionOldBehaviorTest.class);
      war.addClass(ClientWebApplicationExceptionApplication.class);
      war.addClass(ClientWebApplicationExceptionResource.class);
      war.addClass(ClientWebApplicationExceptionExceptions.class);
      war.addClass(PortProviderUtil.class);
      war.addClass(TestUtil.class);
      war.setWebXML(ClientWebApplicationExceptionOldBehaviorTest.class.getPackage(), "wae_web.xml");
      return TestUtil.finishContainerPrepare(war, null, ClientWebApplicationExceptionResource.class);
   }

   ////////////////////////////////////////////////////////////////////////////////////////////
   /**
    * @tpTestDetails For each WebApplicationException in oldExceptions, calls the resource method oldException() to throw
    *                that WebApplicationException. Since it is running on the client side, the standard behavior of throwing a
    *                WebApplicationException will occur. That WebApplicationException should match the WebApplicationException
    *                thrown by oldException().
    *
    * @tpSince RESTEasy 3.11.3.Final
    */
   @Test
   public void testOldExceptionsDirectly() {
      for (int i = 0; i < ClientWebApplicationExceptionExceptions.oldExceptions.length; i++) {
         try {
            target.path("exception/old/" + i).request().get(String.class);
            Assert.fail("expected exception");
         } catch (ResteasyWebApplicationException rwae) {
            Assert.fail("Didn't expect ResteasyWebApplicationException");
         } catch (WebApplicationException wae) {
            Response response = wae.getResponse();
            WebApplicationException oldException = ClientWebApplicationExceptionExceptions.oldExceptions[i];
            Assert.assertEquals(oldException.getResponse().getStatus(), response.getStatus());
            Assert.assertEquals(oldException.getResponse().getHeaderString("foo"), response.getHeaderString("foo"));
            Assert.assertEquals(oldException.getResponse().getEntity(), response.readEntity(String.class));
            Assert.assertEquals(ClientWebApplicationExceptionExceptions.oldExceptionMap.get(response.getStatus()), wae.getClass());
         } catch (Exception e) {
            Assert.fail("expected WebApplicationException");
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
    * @tpSince RESTEasy 3.11.3.Final
    */
   @Test
   public void testNoCatchOldBehaviorOldExceptions() throws Exception {
      for (int i = 0; i < ClientWebApplicationExceptionExceptions.oldExceptions.length; i++) {
         try {
            target.path("nocatch/old/" + i).request().get(String.class);
            Assert.fail("expected exception");
         } catch (ResteasyWebApplicationException rwae) {
            Assert.fail("Didn't expect ResteasyWebApplicationException");
         } catch (WebApplicationException e) {
            Response response = e.getResponse();
            WebApplicationException wae = ClientWebApplicationExceptionExceptions.oldExceptions[i];
            Assert.assertEquals(wae.getResponse().getStatus(), response.getStatus());
            Assert.assertEquals(wae.getResponse().getHeaderString("foo"), response.getHeaderString("foo"));
            Assert.assertEquals(wae.getResponse().getEntity(), response.readEntity(String.class));
            Assert.assertEquals(ClientWebApplicationExceptionExceptions.oldExceptionMap.get(response.getStatus()), e.getClass());
         } catch (Exception e) {
            Assert.fail("expected WebApplicationException");
         }
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
    * @tpSince RESTEasy 3.11.3.Final
    */
   @Test
   public void testNoCatchOldBehaviorNewExceptions() throws Exception {
      for (int i = 0; i < ClientWebApplicationExceptionExceptions.newExceptions.length; i++) {
         try {
            target.path("nocatch/new/" + i).request().get(String.class);
            Assert.fail("expected exception");
         } catch (ResteasyWebApplicationException rwae) {
            Assert.fail("Didn't expect ResteasyWebApplicationException");
         } catch (WebApplicationException e) {
            Response response = e.getResponse();
            Assert.assertEquals(ClientWebApplicationExceptionExceptions.newExceptions[i].getResponse().getStatus(), response.getStatus());
            Assert.assertNull(response.getHeaderString("foo"));
            Assert.assertTrue(response.readEntity(String.class).isEmpty());
            // We compare the old exception here because this is coming from a client resulting in the exception thrown
            // at the client not wrapped.
            Assert.assertEquals(ClientWebApplicationExceptionExceptions.oldExceptionMap.get(response.getStatus()), e.getClass());
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
    * @tpSince RESTEasy 3.11.3.Final
    */
   @Test
   public void testCatchOldBehaviorOldExceptions() throws Exception {
      for (int i = 0; i < ClientWebApplicationExceptionExceptions.oldExceptions.length; i++) {
         try {
            target.path("catch/old/old/" + i).request().get(String.class);
            Assert.fail("expected exception");
         } catch (ResteasyWebApplicationException rwae) {
            Assert.fail("Didn't expect ResteasyWebApplicationException");
         } catch (WebApplicationException e) {
            Response response = e.getResponse();
            Assert.assertEquals(ClientWebApplicationExceptionExceptions.oldExceptions[i].getResponse().getStatus(), response.getStatus());
            Assert.assertEquals(ClientWebApplicationExceptionExceptions.oldExceptions[i].getResponse().getHeaderString("foo"), response.getHeaderString("foo"));
            Assert.assertEquals(ClientWebApplicationExceptionExceptions.oldExceptions[i].getResponse().getEntity(), response.readEntity(String.class));
            Assert.assertEquals(ClientWebApplicationExceptionExceptions.oldExceptionMap.get(response.getStatus()), e.getClass());
         } catch (Exception e) {
            Assert.fail("expected WebApplicationException");
         }
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
    * @tpSince RESTEasy 3.11.3.Final
    */
   @Test
   public void testCatchOldBehaviorNewExceptions() throws Exception {
      for (int i = 0; i < ClientWebApplicationExceptionExceptions.newExceptions.length; i++) {
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
            Assert.assertEquals(ClientWebApplicationExceptionExceptions.oldExceptionMap.get(response.getStatus()), e.getClass());
         } catch (Exception e) {
            Assert.fail("expected WebApplicationException");
         }
      }
   }
}
