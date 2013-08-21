package com.restfully.shop.test;

import com.restfully.shop.domain.Customers;
import org.jboss.resteasy.client.ClientRequest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;


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
   public void testQueryCustomers() throws Exception
   {
      String url = "http://localhost:8080/services/customers";
      while (url != null)
      {
         WebTarget target = client.target(url);
         String output = target.request().get(String.class);
         System.out.println("** XML from " + url);
         System.out.println(output);

         Customers customers = target.request().get(Customers.class);
         url = customers.getNext();
      }
   }
}
