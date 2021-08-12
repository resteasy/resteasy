package org.jboss.resteasy.test.client.exception.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.exception.ResteasyWebApplicationException;
import org.jboss.resteasy.client.exception.WebApplicationExceptionWrapper;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.client.exception.ClientWebApplicationExceptionResteasyProxyTest;
import org.jboss.resteasy.test.client.exception.ClientWebApplicationExceptionTest;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.junit.Assert;

@Path("test")
public class ClientWebApplicationExceptionResteasyProxyResource {

   private static ClientWebApplicationExceptionProxyResourceInterface oldBehaviorProxy;
   private static ClientWebApplicationExceptionProxyResourceInterface newBehaviorProxy;

   static {
      ResteasyClient client = (ResteasyClient) ResteasyClientBuilder.newClient();
      oldBehaviorProxy = client.target(PortProviderUtil.generateURL("/app/test/", ClientWebApplicationExceptionResteasyProxyTest.oldBehaviorDeploymentName))
              .proxy(ClientWebApplicationExceptionProxyResourceInterface.class);
      newBehaviorProxy = client.target(PortProviderUtil.generateURL("/app/test/", ClientWebApplicationExceptionResteasyProxyTest.newBehaviorDeploymentName))
              .proxy(ClientWebApplicationExceptionProxyResourceInterface.class);
   }

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
      throw ClientWebApplicationExceptionTest.oldExceptions[i];
   }

   /**
    * Throws an instance of ResteasyWebApplicationException from newExceptions table.
    * ResteasyWebApplicationException.getResponse() returns a sanitized response.
    * @param i determines element of newExceptions to be thrown
    * @throws Exception
    */
   @GET
   @Path("exception/new/{i}")
   public String newException(@PathParam("i") int i) throws Exception {
      throw ClientWebApplicationExceptionTest.newExceptions[i];
   }

   /**
    * Uses a proxy to call oldException() to get an HTTP response derived from a WebApplicationException.
    * The proxy will throw a WebApplicationException because
    * ResteasyContextParameters.RESTEASY_ORIGINAL_WEBAPPLICATIONEXCEPTION_BEHAVIOR is true.
    *
    * @param i determines element of oldExceptions to be thrown by oldException()
    * @throws Exception
    */
   @GET
   @Path("nocatch/old/old/{i}")
   public String noCatchOldOld(@PathParam("i") int i) throws Exception {
      return oldBehaviorProxy.oldException(i);
   }

   /**
    * Uses a proxy to call oldException() to get an HTTP response derived from a WebApplicationException.
    * The proxy will throw a ResteasyWebApplicationException because
    * ResteasyContextParameters.RESTEASY_ORIGINAL_WEBAPPLICATIONEXCEPTION_BEHAVIOR is false.
    *
    * @param i determines element of oldExceptions to be thrown by oldException()
    * @throws Exception
    */
   @GET
   @Path("nocatch/new/old/{i}")
   public String noCatchNewOld(@PathParam("i") int i) throws Exception {
      return newBehaviorProxy.oldException(i);
   }

   /**
    * Uses a proxy to call newException() to get an HTTP response derived from a ResteasyWebApplicationException.
    * The proxy will throw a WebApplicationException because
    * ResteasyContextParameters.RESTEASY_ORIGINAL_WEBAPPLICATIONEXCEPTION_BEHAVIOR is true.
    *
    * @param i determines element of newExceptions to be thrown by newException()
    * @throws Exception
    */
   @GET
   @Path("nocatch/old/new/{i}")
   public String noCatchOldNew(@PathParam("i") int i) throws Exception {
      return oldBehaviorProxy.newException(i);
   }

   /**
    * Uses a proxy to call newException() to get an HTTP response derived from a ResteasyWebApplicationException.
    * The proxy will throw either a ResteasyWebApplicationException because
    * ResteasyContextParameters.RESTEASY_ORIGINAL_WEBAPPLICATIONEXCEPTION_BEHAVIOR is false.
    *
    * @param i determines element of newExceptions to be thrown by newException()
    * @throws Exception
    */
   @GET
   @Path("nocatch/new/new/{i}")
   public String noCatchNewNew(@PathParam("i") int i) throws Exception {
      return newBehaviorProxy.newException(i);
   }

   /**
    * It is assumed that ResteasyContextParameters.RESTEASY_ORIGINAL_WEBAPPLICATIONEXCEPTION_BEHAVIOR is
    * set to "true" before this method is invoked.
    *
    * Uses a proxy to call oldException(). Since the old behavior is configured, the proxy will throw a
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
         oldBehaviorProxy.oldException(i);
         throw new Exception("expected exception");
      } catch (ResteasyWebApplicationException e) {
         throw new Exception("didn't expect ResteasyWebApplicationException");
      } catch (WebApplicationException e) {
         Response response = e.getResponse();
         Assert.assertEquals(ClientWebApplicationExceptionTest.oldExceptions[i].getResponse().getStatus(), response.getStatus());
         Assert.assertEquals(ClientWebApplicationExceptionTest.oldExceptions[i].getResponse().getHeaderString("foo"), response.getHeaderString("foo"));
         Assert.assertEquals(ClientWebApplicationExceptionTest.oldExceptions[i].getResponse().getEntity(), response.readEntity(String.class));
         Assert.assertEquals(ClientWebApplicationExceptionTest.oldExceptionMap.get(response.getStatus()), e.getClass());
         throw e;
      } catch (Exception e) {
         throw new Exception("expected WebApplicationException, not " + e.getClass());
      }
   }

   /**
    * It is assumed that ResteasyContextParameters.RESTEASY_ORIGINAL_WEBAPPLICATIONEXCEPTION_BEHAVIOR is
    * set to "true" before this method is invoked.
    *
    * Uses a Client to call newException(). Since the old behavior is configured, the Client will throw a
    * WebApplicationException, which is caught and examined for a sanitized Response and matching status.
    *
    * @param i determines element of newExceptions to be thrown by newException()
    * @throws Exception
    */
   @GET
   @Path("catch/old/new/{i}")
   public String catchOldNew(@PathParam("i") int i) throws Exception {
      try {
         return oldBehaviorProxy.newException(i);
      } catch (ResteasyWebApplicationException e) {
         throw new Exception("didn't expect ResteasyWebApplicationException");
      } catch (WebApplicationException e) {
         Response response = e.getResponse();
         Assert.assertNotNull(response);
         Assert.assertEquals(ClientWebApplicationExceptionTest.newExceptions[i].getResponse().getStatus(), response.getStatus());
         Assert.assertNull(response.getHeaderString("foo"));
         Assert.assertTrue(response.readEntity(String.class).length() == 0);
         Assert.assertEquals(ClientWebApplicationExceptionTest.oldExceptionMap.get(response.getStatus()), e.getClass());
         throw e;
      } catch (Exception e) {
         throw new Exception("expected WebApplicationException, not " + e.getClass());
      }
   }

   /**
    * It is assumed that ResteasyContextParameters.RESTEASY_ORIGINAL_WEBAPPLICATIONEXCEPTION_BEHAVIOR holds
    * "false" when this method is invoked.
    *
    * Uses a Client to call oldException().  Since the new behavior is configured, the proxy will throw a
    * WebApplicationExceptionWrapper, which is caught and examined. getResponse() should return a sanitized
    * Response, but the unwrapped Response should match the WebApplicationException
    * thrown by oldException(). That WebApplicationExceptionWrapper is then rethrown.
    *
    * @param i determines element of oldExceptions to be thrown by oldException()
    * @throws Exception
    */
   @GET
   @Path("catch/new/old/{i}")
   public String catchNewOld(@PathParam("i") int i) throws Exception {
      try {
         return newBehaviorProxy.oldException(i);
      } catch (WebApplicationException e) {
         Response sanitizedResponse = e.getResponse();
         Assert.assertEquals(ClientWebApplicationExceptionTest.oldExceptions[i].getResponse().getStatus(), sanitizedResponse.getStatus());
         Assert.assertNull(sanitizedResponse.getHeaderString("foo"));
         Assert.assertFalse(sanitizedResponse.hasEntity());
         Response originalResponse = WebApplicationExceptionWrapper.unwrap(e).getResponse();
         Assert.assertNotNull(originalResponse);
         Assert.assertEquals(ClientWebApplicationExceptionTest.oldExceptions[i].getResponse().getStatus(), originalResponse.getStatus());
         Assert.assertEquals(ClientWebApplicationExceptionTest.oldExceptions[i].getResponse().getHeaderString("foo"), originalResponse.getHeaderString("foo"));
         Assert.assertEquals(ClientWebApplicationExceptionTest.oldExceptions[i].getResponse().getEntity(), originalResponse.readEntity(String.class));
         Assert.assertEquals(ClientWebApplicationExceptionTest.newExceptionMap.get(originalResponse.getStatus()), e.getClass());
         throw e;
      } catch (Exception e) {
         throw new Exception("expected ResteasyWebApplicationException, not " + e.getClass());
      }
   }

   /**
    * It is assumed that ResteasyContextParameters.RESTEASY_ORIGINAL_WEBAPPLICATIONEXCEPTION_BEHAVIOR holds
    * "false" when this method is invoked.
    *
    * Uses a Client to call newException(). Since the new behavior is configured, the proxy will throw a
    * WebApplicationExceptionWrapper, which is caught and examined. getResponse() should return a sanitized
    * Response, but the unwrapped Response should match the WebApplicationException
    * thrown by newException(). That WebApplicationExceptionWrapper is then rethrown.
    *
    * @param i determines element of newExceptions to be thrown by newException()
    * @throws Exception
    */
   @GET
   @Path("catch/new/new/{i}")
   public String catchNewNew(@PathParam("i") int i) throws Exception {
      try {
         return newBehaviorProxy.newException(i);
      } catch (WebApplicationException e) {
         Response sanitizedResponse = e.getResponse();
         Assert.assertEquals(ClientWebApplicationExceptionTest.newExceptions[i].getResponse().getStatus(), sanitizedResponse.getStatus());
         Assert.assertNull(sanitizedResponse.getHeaderString("foo"));
         Assert.assertFalse(sanitizedResponse.hasEntity());
         Response originalResponse = WebApplicationExceptionWrapper.unwrap(e).getResponse();
         Assert.assertNotNull(originalResponse);
         Assert.assertEquals(ClientWebApplicationExceptionTest.newExceptions[i].getResponse().getStatus(), originalResponse.getStatus());
         Assert.assertEquals(ClientWebApplicationExceptionTest.newExceptions[i].getResponse().getHeaderString("foo"), originalResponse.getHeaderString("foo"));
         Assert.assertTrue(originalResponse.readEntity(String.class).isEmpty());
         Assert.assertEquals(ClientWebApplicationExceptionTest.newExceptionMap.get(originalResponse.getStatus()), e.getClass());
         throw e;
      } catch (Exception e) {
         throw new Exception("expected WebApplicationException, not " + e.getClass());
      }
   }
}
