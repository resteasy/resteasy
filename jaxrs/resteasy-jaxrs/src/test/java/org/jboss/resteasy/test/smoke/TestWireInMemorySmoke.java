package org.jboss.resteasy.test.smoke;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.core.executors.InMemoryClientExecutor;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.plugins.providers.DefaultTextPlain;
import org.jboss.resteasy.plugins.providers.StringTextStar;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.Assert;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;

/**
 * Simple smoke test
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class TestWireInMemorySmoke
{

   @Test
   public void testNoDefaultsResource() throws Exception
   {
      InMemoryClientExecutor executor = new InMemoryClientExecutor(createDispatcher());
      Registry registry = executor.getRegistry();
      int oldSize = registry.getSize();
      registry.addPerRequestResource(SimpleResource.class);
      Assert.assertTrue(oldSize < registry.getSize());

      {
         ClientResponse<String> response = new ClientRequest("/basic", executor).get(String.class);
         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
         Assert.assertEquals("basic", response.getEntity());
      }
      {
         ClientResponse response = new ClientRequest("/basic", executor).body("text/plain", "basic").put();
         Assert.assertEquals(204, response.getStatus());
      }
      {
         ClientResponse<String> response = new ClientRequest("/queryParam", executor).queryParameter("param",
                 "hello world").get(String.class);
         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
         Assert.assertEquals("hello world", response.getEntity());
      }
      {
         ClientResponse<String> response = new ClientRequest("/uriParam/1234", executor).get(String.class);
         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
         Assert.assertEquals("1234", response.getEntity());
      }
      registry.removeRegistrations(SimpleResource.class);
      Assert.assertEquals(oldSize, registry.getSize());
   }

   private Dispatcher createDispatcher()
   {
      ResteasyProviderFactory factory = new ResteasyProviderFactory();
      factory.registerProvider(StringTextStar.class);
      factory.registerProvider(DefaultTextPlain.class);
      return new SynchronousDispatcher(factory);
   }

   @Test
   public void testLocatingResource() throws Exception
   {
      InMemoryClientExecutor executor = new InMemoryClientExecutor(createDispatcher());
      Registry registry = executor.getRegistry();
      int oldSize = registry.getSize();
      registry.addPerRequestResource(LocatingResource.class);
      Assert.assertTrue(oldSize < registry.getSize());

      {
         ClientResponse<String> response = new ClientRequest("/locating/basic", executor).get(String.class);
         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
         Assert.assertEquals("basic", response.getEntity());
      }
      {
         ClientResponse response = new ClientRequest("/locating/basic", executor).body("text/plain", "basic").put();
         Assert.assertEquals(204, response.getStatus());
      }
      {
         ClientResponse<String> response = new ClientRequest("/locating/queryParam", executor).queryParameter("param",
                 "hello world").get(String.class);
         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
         Assert.assertEquals("hello world", response.getEntity());
      }
      {
         ClientResponse<String> response = new ClientRequest("/locating/uriParam/1234", executor).body("text/plain",
                 "basic").get(String.class);
         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
         Assert.assertEquals("1234", response.getEntity());
      }
      registry.removeRegistrations(LocatingResource.class);
      Assert.assertEquals(oldSize, registry.getSize());
   }
}