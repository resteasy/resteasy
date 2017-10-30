package org.jboss.resteasy.test.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient43Engine;
import org.jboss.resteasy.test.client.resource.TestResource;
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
 * @tpSince RESTEasy 4.0.0
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
      return TestUtil.finishContainerPrepare(war, null, TestResource.class);
   }

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
}
