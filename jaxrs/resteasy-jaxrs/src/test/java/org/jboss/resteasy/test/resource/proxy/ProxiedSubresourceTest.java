package org.jboss.resteasy.test.resource.proxy;


import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.core.executors.InMemoryClientExecutor;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class ProxiedSubresourceTest
{

   /**
    * This method tests RESTEASY-356
    */
   @Test
   public void testProxiedSubresource() throws Exception
   {
      ResteasyProviderFactory.setInstance(null);
      InMemoryClientExecutor executor = new InMemoryClientExecutor();
      executor.getRegistry().addPerRequestResource(Garage.class);
      ClientResponse<String> result = new ClientRequest("/garage/car", executor).get(String.class);

      assertEquals(200, result.getStatus());
      assertEquals("MT-123AB", result.getEntity());
   }
}
