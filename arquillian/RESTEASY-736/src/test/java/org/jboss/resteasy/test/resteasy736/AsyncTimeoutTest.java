package org.jboss.resteasy.test.resteasy736;

import static org.junit.Assert.*;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.junit.Test;

public class AsyncTimeoutTest extends AsyncTimeoutTestCase
{

   @Test
   public void testAsynchTimeout() throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-736/test/");
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
         assertTrue("Expected response time < 10000, actual " + elapsed, elapsed < 10000 + 500);
      }
   }

}
