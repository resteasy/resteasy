package org.jboss.resteasy.test.client.exception.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

public interface ClientWebApplicationExceptionProxyResourceInterface {

   /**
    * Sets the System property ResteasyContextParameters.RESTEASY_ORIGINAL_WEBAPPLICATIONEXCEPTION_BEHAVIOR
    * @param value value property is set to
    */
   @GET
   @Path("behavior/{value}")
   void setBehavior(@PathParam("value") String value);

   /**
    * Throws an instance of WebApplicationException from oldExceptions table. The Response returned by
    * WebApplicationException.getResponse() will be used by the container to create an HTTP response.
    *
    * @param i determines element of oldExceptions to be thrown
    * @throws Exception
    */
   @GET
   @Path("exception/old/{i}")
   String oldException(@PathParam("i") int i) throws Exception;

   /**
    * Throws an instance of ResteasyWebApplicationException from newExceptions table.
    * ResteasyWebApplicationException.getResponse() returns null, so the container will return
    * an HTTP response with status 500.
    *
    * @param i determines element of newExceptions to be thrown
    * @throws Exception
    */
   @GET
   @Path("exception/new/{i}")
   String newException(@PathParam("i") int i) throws Exception;

   /**
    * Uses a Client or proxy to call oldException() to get an HTTP response derived from a WebApplicationException.
    * Based on that response, the Client or proxy will throw either a WebApplicationException or ResteasyWebApplicationException,
    * depending on the value of ResteasyContextParameters.RESTEASY_ORIGINAL_WEBAPPLICATIONEXCEPTION_BEHAVIOR.
    *
    * @param i determines element of oldExceptions to be thrown by oldException()
    * @throws Exception
    */
   @GET
   @Path("nocatch/old/{i}")
   String noCatchOld(@PathParam("i") int i) throws Exception;

   /**
    * Uses a Client or proxy to call newException() to get an HTTP response derived from a ResteasyWebApplicationException.
    * Based on that response, the Client or proxy will throw either a WebApplicationException or ResteasyWebApplicationException,
    * depending on the value of ResteasyContextParameters.RESTEASY_ORIGINAL_WEBAPPLICATIONEXCEPTION_BEHAVIOR.
    *
    * @param i determines element of newExceptions to be thrown by newException()
    * @throws Exception
    */
   @GET
   @Path("nocatch/new/{i}")
   String noCatchNew(@PathParam("i") int i) throws Exception;

   /**
    * It is assumed that ResteasyContextParameters.RESTEASY_ORIGINAL_WEBAPPLICATIONEXCEPTION_BEHAVIOR is
    * set to "true" before this method is invoked.
    *
    * Uses a Client or proxy to call oldException(). Since the old behavior is configured, the Client or proxy will throw a
    * WebApplicationException, which is caught and examined. It should match the WebApplicationException
    * thrown by oldException(). That WebApplicationException is then rethrown.
    *
    * @param i determines element of oldExceptions to be thrown by oldException()
    * @throws Exception
    */
   @GET
   @Path("catch/old/old/{i}")
   String catchOldOld(@PathParam("i") int i) throws Exception;

   /**
    * It is assumed that ResteasyContextParameters.RESTEASY_ORIGINAL_WEBAPPLICATIONEXCEPTION_BEHAVIOR is
    * set to "true" before this method is invoked.
    *
    * Uses a Client or proxy to call newException(). Since the old behavior is configured, the Client or proxy will throw a
    * WebApplicationException, which is caught and examined. It should have status 500 and represent
    * a stacktrace. That WebApplicationException is then rethrown.
    *
    * @param i determines element of newExceptions to be thrown by newException()
    * @throws Exception
    */
   @GET
   @Path("catch/old/new/{i}")
   String catchOldNew(@PathParam("i") int i) throws Exception;

   /**
    * It is assumed that ResteasyContextParameters.RESTEASY_ORIGINAL_WEBAPPLICATIONEXCEPTION_BEHAVIOR holds
    * "false" when this method is invoked.
    *
    * Uses a Client or proxy to call oldException(). Since the new behavior is configured, the Client or proxy will throw a
    * ResteasyWebApplicationException, which is caught and examined. getResponse() should return null, but
    * getOriginalResponse() should return a Response which matches the WebApplicationException
    * thrown by oldException(). That ResteasyWebApplicationException is then rethrown.
    *
    * @param i determines element of oldExceptions to be thrown by oldException()
    * @throws Exception
    */
   @GET
   @Path("catch/new/old/{i}")
   String catchNewOld(@PathParam("i") int i) throws Exception;

   /**
    * It is assumed that ResteasyContextParameters.RESTEASY_ORIGINAL_WEBAPPLICATIONEXCEPTION_BEHAVIOR holds
    * "false" when this method is invoked.
    *
    * Uses a Client or proxy to call newException(). Since the new behavior is configured, the Client or proxy will throw a
    * ResteasyWebApplicationException, which is caught and examined. getResponse() should return null, but
    * getOriginalResponse() should return a Response which has status 500 and represents
    * a stacktrace. That ResteasyWebApplicationException is then rethrown.
    *
    * @param i determines element of newExceptions to be thrown by newException()
    * @throws Exception
    */
   @GET
   @Path("catch/new/new/{i}")
   String catchNewNew(@PathParam("i") int i) throws Exception;
}
