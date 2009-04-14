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
}

