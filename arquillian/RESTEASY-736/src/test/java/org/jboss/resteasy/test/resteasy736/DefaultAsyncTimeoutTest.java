package org.jboss.resteasy.test.resteasy736;

import static org.junit.Assert.*;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.resteasy736.TestResource;
import org.junit.Test;

public class DefaultAsyncTimeoutTest extends AsyncTimeoutTestCase
{

   @Test
   public void testDefaultAsynchTimeout() throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-736/default/");
      long start = System.currentTimeMillis();
      System.out.println("start:   " + start);
      ClientResponse<String> response = null;
      try
      {
         response = request.get(String.class);
      }
      catch (Exception e)
      {
         System.out.println(e);
      }
      finally
      {
         request.clear();
         System.out.println("finish:  " + System.currentTimeMillis());
         long elapsed = System.currentTimeMillis() - start;
         System.out.println("elapsed: " + elapsed + " ms");;
         System.out.println("status: " + response.getStatus());
         assertTrue(response != null);
         System.out.println("response: " + response.getEntity());
         assertEquals(503, response.getStatus());
         int max = TestResource.getDefaultTimeout() + 5000;
         assertTrue("Expected response time < " + max + ", actual " + elapsed, elapsed < max);
      }
   }
}
