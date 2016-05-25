package org.jboss.resteasy.test.async;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class AsyncTest
{
   @Test
   public void testAsync() throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:8080/");
      ClientResponse<String> response = request.get(String.class);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("hello", response.getEntity());
   }

   @Test
   public void testTimeout() throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:8080/timeout");
      ClientResponse<String> response = request.get(String.class);
      Assert.assertEquals(408, response.getStatus()); // exception mapper from another test overrides 503 to 408
   }
}
