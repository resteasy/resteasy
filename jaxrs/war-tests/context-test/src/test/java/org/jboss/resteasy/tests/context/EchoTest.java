package org.jboss.resteasy.tests.context;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.Assert;
import org.junit.Test;

/**
 * RESTEASY-184
 */
public class EchoTest
{
   @Test
   public void testForward() throws Exception
   {
      RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
      ClientRequest request = new ClientRequest("http://localhost:9095/test/forward");
      ClientResponse<String> response = request.get(String.class);
      Assert.assertEquals("hello world", response.getEntity());
   }

   @Test
   public void testRepeat() throws Exception
   {
      RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
      ClientRequest request = new ClientRequest("http://localhost:9095/test/test");
      ClientResponse<String> response = request.get(String.class);
      Assert.assertEquals("http://localhost:9095/test/", response.getEntity());
   }

   @Test
   public void testEmpty() throws Exception
   {
      RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
      ClientRequest request = new ClientRequest("http://localhost:9095/test/");
      ClientResponse<String> response = request.get(String.class);
      Assert.assertEquals("http://localhost:9095/test/", response.getEntity());
   }

   @Test
   public void testServletContext() throws Exception
   {
      RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
      ClientRequest request = new ClientRequest("http://localhost:9095/test/test/servletcontext");
      ClientResponse<String> response = request.get(String.class);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("ok", response.getEntity());
      Assert.assertTrue(response.getResponseHeaders().containsKey("before-encoder"));
      Assert.assertTrue(response.getResponseHeaders().containsKey("after-encoder"));
      Assert.assertTrue(response.getResponseHeaders().containsKey("end"));
      Assert.assertTrue(response.getResponseHeaders().containsKey("encoder"));
   }

   @Test
   public void testServletConfig() throws Exception
   {
      RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
      ClientRequest request = new ClientRequest("http://localhost:9095/test/test/servletconfig");
      ClientResponse<String> response = request.get(String.class);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("ok", response.getEntity());
   }

   @Test
   public void testXmlMappings() throws Exception
   {
      RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
      ClientRequest request = new ClientRequest("http://localhost:9095/test/stuff.xml");
      ClientResponse<String> response = request.get(String.class);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("xml", response.getEntity());

   }

   @Test
   public void testJsonMappings() throws Exception
   {
      RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
      ClientRequest request = new ClientRequest("http://localhost:9095/test/stuff.json");
      ClientResponse<String> response = request.get(String.class);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("json", response.getEntity());

   }
}

