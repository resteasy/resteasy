package org.jboss.resteasy.test.async;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientFactory;
import javax.ws.rs.core.Response;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class JaxrsAsyncTest
{
   @Test
   public void testAsync() throws Exception
   {
      Client client = ClientFactory.newClient();
      Response response = client.target("http://localhost:8080/jaxrs").request().get();
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("hello", response.readEntity(String.class));
      response.close();
      client.close();
   }

   @Test
   public void testTimeout() throws Exception
   {
      Client client = ClientFactory.newClient();
      Response response = client.target("http://localhost:8080/jaxrs/timeout").request().get();
      Assert.assertEquals(503, response.getStatus());
      response.close();
      client.close();
   }

   @Test
   public void testTimeoutFallback() throws Exception
   {
      Client client = ClientFactory.newClient();
      Response response = client.target("http://localhost:8080/jaxrs/timeout/fallback").request().get();
      Assert.assertEquals(400, response.getStatus());
      response.close();
      client.close();
   }

   @Test
   public void testCancel() throws Exception
   {
      Client client = ClientFactory.newClient();
      Response response = client.target("http://localhost:8080/jaxrs/cancel").request().get();
      Assert.assertEquals(500, response.getStatus());
      response.close();
      response = client.target("http://localhost:8080/jaxrs/cancelled").request().get();
      Assert.assertEquals(204, response.getStatus());
      response.close();
      client.close();
   }
}
