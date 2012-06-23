package org.jboss.resteasy.test.async;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class AsyncTest
{
   @Test
   public void testMock() throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:8080/");
      ClientResponse<String> response = request.get(String.class);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("hello", response.getEntity());
   }

   @Test
   @Ignore // Jetty retries request???!?!
   public void testTimeout() throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:8080/timeout");
      ClientResponse<String> response = request.get(String.class);
      Assert.assertEquals(503, response.getStatus());
   }
}
