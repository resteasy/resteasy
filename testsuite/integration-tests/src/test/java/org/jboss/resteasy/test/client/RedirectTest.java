package org.jboss.resteasy.test.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient43Engine;
import org.jboss.resteasy.test.client.resource.RedirectProxyResource;
import org.jboss.resteasy.test.client.resource.RedirectResource;
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
 * @tpSince RESTEasy 3.5
 * @tpTestCaseDetails https://issues.jboss.org/browse/RESTEASY-1075
 */
@RunWith(Arquillian.class)
@RunAsClient
public class RedirectTest extends ClientTestBase
{
   @Deployment
   public static Archive<?> deploy()
   {
      WebArchive war = TestUtil.prepareArchive(RedirectTest.class.getSimpleName());
      war.addClasses(PortProviderUtil.class);
      return TestUtil.finishContainerPrepare(war, null, RedirectResource.class);
   }

   /**
    * @tpTestDetails Set client to following the redirect
    *                Do not use RESTEasy proxy
    *                Use GET HTTP request
    *                Send request to "end-point 1", that returns 307 HTTP code (Temporary Redirect) and redirect client to another URL (handled by "end-point 2")
    *                Client should follow redirect URL
    *                Client should return data from "end-point 2"
    *                Both end-points return Response data type
    * @tpSince RESTEasy 3.5
    */
   @Test
   public void testRedirect()
   {
      ApacheHttpClient43Engine engine = new ApacheHttpClient43Engine();
      engine.setFollowRedirects(true);
      Client client = new ResteasyClientBuilder().httpEngine(engine).build();
      try
      {
         Response response = client.target(generateURL("/redirect/" + RedirectTest.class.getSimpleName())).request()
               .get();
         Assert.assertEquals(200, response.getStatus());
         Assert.assertEquals("OK", response.readEntity(String.class));
         response.close();
      }
      finally
      {
         client.close();
      }
   }

   /**
    * @tpTestDetails Set client to following the redirect
    *                Do not use RESTEasy proxy
    *                Use POST HTTP request
    *                Send request to "end-point 1", that returns 307 HTTP code (Temporary Redirect) and redirect client to another URL (handled by "end-point 2")
    *                Client should follow redirect URL
    *                Client should return data from "end-point 2"
    *                Both end-points return Response data type
    * @tpSince RESTEasy 3.5
    */
   @Test
   public void testPostRedirect()
   {
      ApacheHttpClient43Engine engine = new ApacheHttpClient43Engine();
      engine.setFollowRedirects(true);
      Client client = new ResteasyClientBuilder().httpEngine(engine).build();
      try
      {
         Response response = client.target(generateURL("/post-redirect")).request()
               .post(Entity.entity(RedirectTest.class.getSimpleName(), "text/plain"));
         Assert.assertEquals(200, response.getStatus());
         Assert.assertEquals("OK", response.readEntity(String.class));
         response.close();
      }
      finally
      {
         client.close();
      }
   }

   /**
    * @tpTestDetails Set client to following the redirect
    *                Use RESTEasy proxy
    *                Use GET HTTP request
    *                Send request to "end-point 1", that returns 307 HTTP code (Temporary Redirect) and redirect client to another URL (handled by "end-point 2")
    *                Client should follow redirect URL
    *                Client should return data from "end-point 2"
    *                Both end-points return Response data type
    * @tpSince RESTEasy 3.5
    */
   @Test
   public void testRedirectProxy()
   {
      ApacheHttpClient43Engine engine = new ApacheHttpClient43Engine();
      engine.setFollowRedirects(true);
      ResteasyClient client = new ResteasyClientBuilder().httpEngine(engine).build();
      try
      {
         RedirectProxyResource proxy = client.target(generateURL("/"))
                 .proxy(RedirectProxyResource.class);
         Response response = proxy.redirect(RedirectTest.class.getSimpleName());
         Assert.assertEquals(200, response.getStatus());
         Assert.assertEquals("OK", response.readEntity(String.class));
         response.close();
      }
      finally
      {
         client.close();
      }
   }

   /**
    * @tpTestDetails Set client to following the redirect
    *                Use RESTEasy proxy
    *                Use GET HTTP request
    *                Send request to "end-point 1", that returns 307 HTTP code (Temporary Redirect) and redirect client to another URL (handled by "end-point 2")
    *                Client should follow redirect URL
    *                Client should return data from "end-point 2"
    *                "end-point 1" returns Response data type, "end-point 2" returns String data type
    * @tpSince RESTEasy 3.5
    */
   @Test
   public void testRedirectDirectResponseProxy()
   {
      ApacheHttpClient43Engine engine = new ApacheHttpClient43Engine();
      engine.setFollowRedirects(true);
      ResteasyClient client = new ResteasyClientBuilder().httpEngine(engine).build();
      try
      {
         RedirectProxyResource proxy = client.target(generateURL("/"))
                 .proxy(RedirectProxyResource.class);
         Response response = proxy.redirectDirectResponse(RedirectTest.class.getSimpleName());
         Assert.assertEquals(200, response.getStatus());
         Assert.assertEquals("ok - direct response", response.readEntity(String.class));
         response.close();
      }
      finally
      {
         client.close();
      }
   }

   /**
    * @tpTestDetails Set client to following the redirect
    *                Use RESTEasy proxy
    *                Use GET HTTP request
    *                Send request to "end-point 1", that returns 301 HTTP code (Moved Permanently) and redirect client to another URL (handled by "end-point 2")
    *                Client should follow redirect URL
    *                Client should return data from "end-point 2"
    *                "end-point 1" returns Response data type, "end-point 2" returns String data type
    * @tpSince RESTEasy 3.5
    */
   @Test
   public void testMovedPermanentlyDirectResponseProxy()
   {
      ApacheHttpClient43Engine engine = new ApacheHttpClient43Engine();
      engine.setFollowRedirects(true);
      ResteasyClient client = new ResteasyClientBuilder().httpEngine(engine).build();
      try
      {
         RedirectProxyResource proxy = client.target(generateURL("/"))
                 .proxy(RedirectProxyResource.class);
         Response response = proxy.movedPermanently(RedirectTest.class.getSimpleName());
         Assert.assertEquals(200, response.getStatus());
         Assert.assertEquals("ok - direct response", response.readEntity(String.class));
         response.close();
      }
      finally
      {
         client.close();
      }
   }

   /**
    * @tpTestDetails Set client to following the redirect
    *                Use RESTEasy proxy
    *                Use GET HTTP request
    *                Send request to "end-point 1", that returns 302 HTTP code (Found) and redirect client to another URL (handled by "end-point 2")
    *                Client should follow redirect URL
    *                Client should return data from "end-point 2"
    *                "end-point 1" returns Response data type, "end-point 2" returns String data type
    * @tpSince RESTEasy 3.5
    */
   @Test
   public void testFoundDirectResponseProxy()
   {
      ApacheHttpClient43Engine engine = new ApacheHttpClient43Engine();
      engine.setFollowRedirects(true);
      ResteasyClient client = new ResteasyClientBuilder().httpEngine(engine).build();
      try
      {
         RedirectProxyResource proxy = client.target(generateURL("/"))
                 .proxy(RedirectProxyResource.class);
         Response response = proxy.found(RedirectTest.class.getSimpleName());
         Assert.assertEquals(200, response.getStatus());
         Assert.assertEquals("ok - direct response", response.readEntity(String.class));
         response.close();
      }
      finally
      {
         client.close();
      }
   }

   /**
    * @tpTestDetails Do not use RESTEasy proxy
    *                Use GET HTTP request
    *                Do not set client to following the redirect
    *                Send request to "end-point 1", that returns 307 HTTP code (Temporary Redirect) and redirect client to another URL (handled by "end-point 2")
    *                Client should not follow redirect URL
    *                Client should return 307 HTTP code
    * @tpSince RESTEasy 3.5
    */
   @Test
   public void testNoRedirect()
   {
      Client client = ClientBuilder.newClient();
      try
      {
         Response response = client.target(generateURL("/redirect/" + RedirectTest.class.getSimpleName())).request()
               .get();
         Assert.assertEquals(307, response.getStatus());
         response.close();
      }
      finally
      {
         client.close();
      }
   }

   /**
    * @tpTestDetails Do not use RESTEasy proxy
    *                Use POST HTTP request
    *                Do not set client to following the redirect
    *                Send request to "end-point 1", that returns 307 HTTP code (Temporary Redirect) and redirect client to another URL (handled by "end-point 2")
    *                Client should not follow redirect URL
    *                Client should return 307 HTTP code
    * @tpSince RESTEasy 3.5
    */
   @Test
   public void testNoPostRedirect()
   {
      Client client = ClientBuilder.newClient();
      try
      {
         Response response = client.target(generateURL("/post-redirect")).request()
               .post(Entity.entity(RedirectTest.class.getSimpleName(), "text/plain"));
         Assert.assertEquals(303, response.getStatus());
         response.close();
      }
      finally
      {
         client.close();
      }
   }

   /**
    * @tpTestDetails Use RESTEasy proxy
    *                Do not set client to following the redirect
    *                Send request to "end-point 1", that returns 307 HTTP code (Temporary Redirect) and redirect client to another URL (handled by "end-point 2")
    *                Client should not follow redirect URL
    *                Client should return 307 HTTP code
    * @tpSince RESTEasy 3.5
    */
   @Test
   public void testNoRedirectProxy()
   {
      ResteasyClient client = new ResteasyClientBuilder().build();
      try
      {
         RedirectProxyResource proxy = client.target(generateURL("/"))
                 .proxy(RedirectProxyResource.class);
         Response response = proxy.redirect(RedirectTest.class.getSimpleName());
         Assert.assertEquals(307, response.getStatus());
         response.close();
      }
      finally
      {
         client.close();
      }
   }
}
