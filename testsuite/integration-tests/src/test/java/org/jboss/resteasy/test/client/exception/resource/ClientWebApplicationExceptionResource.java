package org.jboss.resteasy.test.client.exception.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.exception.ResteasyWebApplicationException;
import org.jboss.resteasy.client.exception.WebApplicationExceptionWrapper;
import org.junit.Assert;

@Path("test")
public class ClientWebApplicationExceptionResource {

   private static Client client = ClientBuilder.newClient();
   private static WebTarget target = client.target(ClientWebApplicationExceptionExceptions.generateURL("/app/test/"));

   /**
    * Throws an instance of WebApplicationException from oldExceptions table. The Response returned by
    * WebApplicationException.getResponse() will be used by the container to create an HTTP response.
    *
    * @param i determines element of oldExceptions to be thrown
    * @throws Exception
    */
   @GET
   @Path("exception/old/{i}")
   public String oldException(@PathParam("i") int i) throws Exception {
      throw ClientWebApplicationExceptionExceptions.oldExceptions[i];
   }

   /**
    * Throws an instance of ResteasyWebApplicationException from newExceptions table.
    * ResteasyWebApplicationException.getResponse() returns a sanitized response.
    *
    * @param i determines element of newExceptions to be thrown
    * @throws Exception
    */
   @GET
   @Path("exception/new/{i}")
   public String newException(@PathParam("i") int i) throws Exception {
      throw ClientWebApplicationExceptionExceptions.newExceptions[i];
   }

   /**
    * Uses a Client to call oldException() to get an HTTP response derived from a WebApplicationException.
    * Based on that response, the Client will throw either a WebApplicationException or ResteasyWebApplicationException,
    * depending on the value of ResteasyContextParameters.RESTEASY_ORIGINAL_WEBAPPLICATIONEXCEPTION_BEHAVIOR.
    *
    * @param i determines element of oldExceptions to be thrown by oldException()
    * @throws Exception
    */
   @GET
   @Path("nocatch/old/{i}")
   public String noCatchOld(@PathParam("i") int i) throws Exception {
      return target.path("exception/old/" + i).request().get(String.class);
   }

   /**
    * Uses a Client to call newException() to get an HTTP response derived from a ResteasyWebApplicationException.
    * Based on that response, the Client will throw either a WebApplicationException or ResteasyWebApplicationException,
    * depending on the value of ResteasyContextParameters.RESTEASY_ORIGINAL_WEBAPPLICATIONEXCEPTION_BEHAVIOR.
    *
    * @param i determines element of newExceptions to be thrown by newException()
    * @throws Exception
    */
   @GET
   @Path("nocatch/new/{i}")
   public String noCatchNew(@PathParam("i") int i) throws Exception {
      return target.path("exception/new/" + i).request().get(String.class);
   }

   /**
    * It is assumed that ResteasyContextParameters.RESTEASY_ORIGINAL_WEBAPPLICATIONEXCEPTION_BEHAVIOR is
    * set to "true" before this method is invoked.
    *
    * Uses a Client to call oldException(). Since the old behavior is configured, the Client will throw a
    * WebApplicationException, which is caught and examined. It should match the WebApplicationException
    * thrown by oldException(). That WebApplicationException is then rethrown.
    *
    * @param i determines element of oldExceptions to be thrown by oldException()
    * @throws Exception
    */
   @GET
   @Path("catch/old/old/{i}")
   public String catchOldOld(@PathParam("i") int i) throws Exception {
      try {
         target.path("exception/old/" + i).request().get(String.class);
         throw new Exception("expected exception");
      } catch (ResteasyWebApplicationException e) {
         throw new Exception("didn't expect ResteasyWebApplicationException");
      } catch (WebApplicationException e) {
         Response response = e.getResponse();
         Assert.assertEquals(ClientWebApplicationExceptionExceptions.oldExceptions[i].getResponse().getStatus(), response.getStatus());
         Assert.assertEquals(ClientWebApplicationExceptionExceptions.oldExceptions[i].getResponse().getHeaderString("foo"), response.getHeaderString("foo"));
         Assert.assertEquals(ClientWebApplicationExceptionExceptions.oldExceptions[i].getResponse().getEntity(), response.readEntity(String.class));
         Assert.assertEquals(ClientWebApplicationExceptionExceptions.oldExceptionMap.get(response.getStatus()), e.getClass());
         throw e;
      } catch (Exception e) {
         throw new Exception("expected ResteasyWebApplicationException, not " + e.getClass());
      }
   }

   /**
    * It is assumed that ResteasyContextParameters.RESTEASY_ORIGINAL_WEBAPPLICATIONEXCEPTION_BEHAVIOR is
    * set to "true" before this method is invoked.
    *
    * Uses a Client to call newException(). Since the old behavior is configured, the Client will throw a
    * WebApplicationException, which is caught and examined. It should have status 500 and represent
    * a stacktrace. That WebApplicationException is then rethrown.
    *
    * @param i determines element of newExceptions to be thrown by newException()
    * @throws Exception
    */
   @GET
   @Path("catch/old/new/{i}")
   public String catchOldNew(@PathParam("i") int i) throws Exception {
      try {
         target.path("exception/new/" + i).request().get(String.class);
         throw new Exception("expected exception");
      } catch (ResteasyWebApplicationException e) {
         throw new Exception("didn't expect ResteasyWebApplicationException");
      } catch (WebApplicationException e) {
         Response response = e.getResponse();
         Assert.assertEquals(500, response.getStatus());
         Assert.assertNull(response.getHeaderString("foo"));
         Assert.assertTrue(response.readEntity(String.class).contains("Caused by"));
         Assert.assertEquals(ClientWebApplicationExceptionExceptions.oldExceptionMap.get(response.getStatus()), e.getClass());
         throw e;
      } catch (Exception e) {
         throw new Exception("expected ResteasyWebApplicationException, not " + e.getClass());
      }
   }

   /**
    * It is assumed that ResteasyContextParameters.RESTEASY_ORIGINAL_WEBAPPLICATIONEXCEPTION_BEHAVIOR holds
    * "false" when this method is invoked.
    *
    * Uses a Client to call oldException(). Since the new behavior is configured, the Client will throw a
    * ResteasyWebApplicationException, which is caught and examined. getResponse() should return null, but
    * getOriginalResponse() should return a Response which matches the WebApplicationException
    * thrown by oldException(). That ResteasyWebApplicationException is then rethrown.
    *
    * @param i determines element of oldExceptions to be thrown by oldException()
    * @throws Exception
    */
   @GET
   @Path("catch/new/old/{i}")
   public String catchNewOld(@PathParam("i") int i) throws Exception {
      try {
         target.path("exception/old/" + i).request().get(String.class);
         throw new Exception("expected exception");
      } catch (ResteasyWebApplicationException e) {
         Response originalResponse = WebApplicationExceptionWrapper.unwrap(e).getResponse();
         Assert.assertNotNull(originalResponse);
         Assert.assertEquals(ClientWebApplicationExceptionExceptions.oldExceptions[i].getResponse().getStatus(), originalResponse.getStatus());
         Assert.assertEquals(ClientWebApplicationExceptionExceptions.oldExceptions[i].getResponse().getHeaderString("foo"), originalResponse.getHeaderString("foo"));
         Assert.assertEquals(ClientWebApplicationExceptionExceptions.oldExceptions[i].getResponse().getEntity(), originalResponse.readEntity(String.class));
         Assert.assertEquals(ClientWebApplicationExceptionExceptions.newExceptionMap.get(originalResponse.getStatus()), e.getClass());
         throw e;
      } catch (Exception e) {
         throw new Exception("expected ResteasyWebApplicationException, not " + e.getClass());
      }
   }

   /**
    * It is assumed that ResteasyContextParameters.RESTEASY_ORIGINAL_WEBAPPLICATIONEXCEPTION_BEHAVIOR holds
    * "false" when this method is invoked.
    *
    * Uses a Client to call newException(). Since the new behavior is configured, the Client will throw a
    * ResteasyWebApplicationException, which is caught and examined. getResponse() should return null, but
    * getOriginalResponse() should return a Response which has status 500 and represents
    * a stacktrace. That ResteasyWebApplicationException is then rethrown.
    *
    * @param i determines element of newExceptions to be thrown by newException()
    * @throws Exception
    */
   @GET
   @Path("catch/new/new/{i}")
   public String catchNewNew(@PathParam("i") int i) throws Exception {
      try {
         target.path("exception/new/" + i).request().get(String.class);
         throw new Exception("expected exception");
      } catch (ResteasyWebApplicationException e) {
         Response originalResponse = WebApplicationExceptionWrapper.unwrap(e).getResponse();
         Assert.assertNotNull(originalResponse);
         Assert.assertEquals(500, originalResponse.getStatus());
         Assert.assertNull(originalResponse.getHeaderString("foo"));
         Assert.assertTrue(originalResponse.readEntity(String.class).contains("Caused by"));
         throw e;
      } catch (Exception e) {
         throw new Exception("expected ResteasyWebApplicationException, not " + e.getClass());
      }
   }
}
