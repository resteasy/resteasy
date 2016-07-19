package org.jboss.resteasy.test.resource.proxy;


import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ProxiedSubresourceTest
{
   protected static ResteasyDeployment deployment;
   protected static Dispatcher dispatcher;

   @BeforeClass
   public static void before() throws Exception
   {
      deployment = EmbeddedContainer.start();
      dispatcher = deployment.getDispatcher();
      deployment.getRegistry().addPerRequestResource(Garage.class);
   }

   @After
   public void after() throws Exception
   {
      EmbeddedContainer.stop();
      Thread.sleep(1000);
      dispatcher = null;
      deployment = null;
   }

   /**
    * This method tests RESTEASY-356
    */
   @Test
   public void testProxiedSubresource() throws Exception
   {
      Response result = ClientBuilder.newClient().target("http://localhost:8081/garage/car").request().get();
      Assert.assertEquals(200, result.getStatus());
      Assert.assertEquals("MT-123AB", result.readEntity(String.class));
      result.close();
   }
}
