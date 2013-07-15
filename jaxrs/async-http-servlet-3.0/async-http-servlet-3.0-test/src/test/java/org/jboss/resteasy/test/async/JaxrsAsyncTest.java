package org.jboss.resteasy.test.async;

import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class JaxrsAsyncTest
{
   @Test
   public void testInjectionFailure() throws Exception
   {
      System.out.println("***INJECTION FAILURE***");
      long start = System.currentTimeMillis();
      Client client = ClientBuilder.newClient();
      Response response = client.target("http://localhost:8080/jaxrs/injection-failure/abcd").request().get();
      Assert.assertEquals(404, response.getStatus());
      long end = System.currentTimeMillis() - start;
      Assert.assertTrue(end < 1000);  // should take less than 1 second
      response.close();
      client.close();
   }

   @Test
   public void testMethodFailure() throws Exception
   {
      System.out.println("***method FAILURE***");
      long start = System.currentTimeMillis();
      Client client = ClientBuilder.newClient();
      Response response = client.target("http://localhost:8080/jaxrs/method-failure").request().get();
      Assert.assertEquals(403, response.getStatus());
      long end = System.currentTimeMillis() - start;
      Assert.assertTrue(end < 1000);  // should take less than 1 second
      response.close();
      client.close();
   }



   @Test
   public void testAsync() throws Exception
   {
      Client client = ClientBuilder.newClient();
      long start = System.currentTimeMillis();
      Response response = client.target("http://localhost:8080/jaxrs").request().get();
      long end = System.currentTimeMillis() - start;
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("hello", response.readEntity(String.class));
      Assert.assertTrue(end < 1000);  // should take less than 1 second
      response.close();
      client.close();
   }

   @Test
   public void testTimeout() throws Exception
   {
      Client client = ClientBuilder.newClient();
      Response response = client.target("http://localhost:8080/jaxrs/timeout").request().get();
      Assert.assertEquals(503, response.getStatus());
      response.close();
      client.close();
   }

   @Test
   public void testCancel() throws Exception
   {
      Client client = ClientBuilder.newClient();
      Response response = client.target("http://localhost:8080/jaxrs/cancel").request().get();
      Assert.assertEquals(503, response.getStatus());
      response.close();
      response = client.target("http://localhost:8080/jaxrs/cancelled").request().get();
      Assert.assertEquals(204, response.getStatus());
      response.close();
      client.close();
   }
}
