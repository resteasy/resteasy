package org.jboss.resteasy.tests.encoding.sample.test;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.tests.encoding.sample.HelloClient;
import org.junit.Assert;
import org.junit.Test;

/**
 * RESTEASY-208, RESTEASY-214
 */
public class EchoTest
{

   private static final String SPACES_REQUEST = "something something";
   private static final String QUERY = "select p from VirtualMachineEntity p where guest.guestId = :id";

   @Test
   public void testEcho()
   {
      RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
      HelloClient client = ProxyFactory.create(HelloClient.class, "http://localhost:9095");
      Assert.assertEquals(SPACES_REQUEST, client.sayHi(SPACES_REQUEST));

      Assert.assertEquals(QUERY, client.compile(QUERY));
   }

   @Test
   public void testIt() throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:9095/sayhello/widget/08%2F26%2F2009");
      ClientResponse<String> response = request.get(String.class);
      Assert.assertEquals(200, response.getStatus());

   }
}

