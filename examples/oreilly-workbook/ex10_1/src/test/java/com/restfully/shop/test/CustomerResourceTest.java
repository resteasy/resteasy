package com.restfully.shop.test;

import com.restfully.shop.domain.Customer;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.junit.Assert;
import org.junit.Test;


/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class CustomerResourceTest
{
   @Test
   public void testCustomerResource() throws Exception
   {
      //RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
      ClientRequest request = new ClientRequest("http://localhost:9095/customers/1");
      ClientResponse<Customer> response = request.get(Customer.class);
      Assert.assertEquals(200, response.getStatus());
      Customer cust = response.getEntity();

      String etag = response.getHeaders().getFirst("ETag");
      System.out.println("Doing a conditional GET with ETag: " + etag);
      request.clear();
      request.header("If-None-Match", etag);
      response = request.get(Customer.class);
      Assert.assertEquals(304, response.getStatus());

      // Update and send a bad etag with conditional PUT
      cust.setCity("Bedford");
      request.clear();
      request.header("If-Match", "JUNK");
      request.body("application/xml", cust);
      ClientResponse response2 = request.put();
      Assert.assertEquals(412, response2.getStatus());
   }
}
