package com.restfully.shop.test;

import com.restfully.shop.domain.Customer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Response;


/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class CustomerResourceTest
{
   private static Client client;

   @BeforeClass
   public static void initClient()
   {
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void closeClient()
   {
      client.close();
   }

   @Test
   public void testCustomerResource() throws Exception
   {
      WebTarget customerTarget = client.target("http://localhost:8080/services/customers/1");
      Response response = customerTarget.request().get();
      Assert.assertEquals(200, response.getStatus());
      Customer cust = response.readEntity(Customer.class);

      EntityTag etag = response.getEntityTag();
      response.close();

      System.out.println("Doing a conditional GET with ETag: " + etag.toString());
      response = customerTarget.request()
                               .header("If-None-Match", etag).get();
      Assert.assertEquals(304, response.getStatus());
      response.close();

      // Update and send a bad etag with conditional PUT
      cust.setCity("Bedford");
      response = customerTarget.request()
              .header("If-Match", "JUNK")
              .put(Entity.xml(cust));
      Assert.assertEquals(412, response.getStatus());
      response.close();
   }
}
