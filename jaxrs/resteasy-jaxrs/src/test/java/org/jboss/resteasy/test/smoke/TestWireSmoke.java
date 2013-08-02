package org.jboss.resteasy.test.smoke;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * Simple smoke test
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class TestWireSmoke
{

   private static Dispatcher dispatcher;

   @BeforeClass
   public static void before() throws Exception
   {
      dispatcher = EmbeddedContainer.start().getDispatcher();
   }

   @AfterClass
   public static void after() throws Exception
   {
      EmbeddedContainer.stop();
   }

   @Test
   public void testNoDefaultsResource() throws Exception
   {
      int oldSize = dispatcher.getRegistry().getSize();
      dispatcher.getRegistry().addPerRequestResource(SimpleResource.class);
      Assert.assertTrue(oldSize < dispatcher.getRegistry().getSize());

      {
         ClientRequest request = new ClientRequest(generateURL("/basic"));
         ClientResponse<String> response = request.get(String.class);
         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
         Assert.assertEquals("basic", response.getEntity());
      }

      {
         ClientRequest request = new ClientRequest(generateURL("/basic"));
         request.body("text/plain", "basic");
         ClientResponse<?> response = request.put();
         Assert.assertEquals(204, response.getStatus());
      }
      
      {
         ClientRequest request = new ClientRequest(generateURL("/queryParam"));
         request.queryParameter("param", "hello world");
         ClientResponse<String> response = request.get(String.class);
         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
         Assert.assertEquals("hello world", response.getEntity());
      }

      {
         ClientRequest request = new ClientRequest(generateURL("/uriParam/1234"));
         ClientResponse<String> response = request.get(String.class);
         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
         Assert.assertEquals("1234", response.getEntity());         
      }

      dispatcher.getRegistry().removeRegistrations(SimpleResource.class);
      Assert.assertEquals(oldSize, dispatcher.getRegistry().getSize());
   }

   @Test
   public void testLocatingResource() throws Exception
   {
      int oldSize = dispatcher.getRegistry().getSize();
      dispatcher.getRegistry().addPerRequestResource(LocatingResource.class);
      Assert.assertTrue(oldSize < dispatcher.getRegistry().getSize());

      {
         ClientRequest request = new ClientRequest(generateURL("/locating/basic"));
         ClientResponse<String> response = request.get(String.class);
         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
         Assert.assertEquals("basic", response.getEntity());
      }

      {
         ClientRequest request = new ClientRequest(generateURL("/locating/basic"));
         request.body("text/plain", "basic");
         ClientResponse<?> response = request.put(String.class);
         Assert.assertEquals(204, response.getStatus());
      }

      {
         ClientRequest request = new ClientRequest(generateURL("/locating/queryParam"));
         request.queryParameter("param", "hello world");
         ClientResponse<String> response = request.get(String.class);
         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
         Assert.assertEquals("hello world", response.getEntity());
      }

      {
         ClientRequest request = new ClientRequest(generateURL("/locating/uriParam/1234"));
         ClientResponse<String> response = request.get(String.class);
         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
         Assert.assertEquals("1234", response.getEntity());
      }

      dispatcher.getRegistry().removeRegistrations(LocatingResource.class);
      Assert.assertEquals(oldSize, dispatcher.getRegistry().getSize());
   }
}