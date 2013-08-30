package com.restfully.shop.test;

import com.restfully.shop.domain.Customer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;


/**
 * This example has changed from the description in the book.  Previously, the error body was not being shown.
 *
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
      try
      {
         Customer customer = client.target("http://localhost:8080/services/customers/1").request().get(Customer.class);
         System.out.println("Should never get here!");
      }
      catch (NotFoundException e)
      {
         System.out.println("Caught error!");
         String error = e.getResponse().readEntity(String.class);
         System.out.println(error);
      }
   }
}
