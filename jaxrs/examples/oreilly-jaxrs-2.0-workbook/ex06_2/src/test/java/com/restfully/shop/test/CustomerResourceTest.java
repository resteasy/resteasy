package com.restfully.shop.test;

import com.restfully.shop.domain.Customer;
import com.restfully.shop.services.JavaMarshaller;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
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
      client.register(JavaMarshaller.class);
   }

   @AfterClass
   public static void closeClient()
   {
      client.close();
   }

   @Test
   public void testCustomerResource() throws Exception
   {
      System.out.println("*** Create a new Customer ***");
      Customer newCustomer = new Customer();
      newCustomer.setFirstName("Bill");
      newCustomer.setLastName("Burke");
      newCustomer.setStreet("256 Clarendon Street");
      newCustomer.setCity("Boston");
      newCustomer.setState("MA");
      newCustomer.setZip("02115");
      newCustomer.setCountry("USA");

      Response response = client.target("http://localhost:8080/services/customers")
              .request().post(Entity.entity(newCustomer, "application/example-java"));
      if (response.getStatus() != 201) throw new RuntimeException("Failed to create");
      String location = response.getLocation().toString();
      System.out.println("Location: " + location);
      response.close();

      System.out.println("*** GET Created Customer **");
      Customer customer = client.target(location).request().get(Customer.class);
      System.out.println(customer);

      customer.setFirstName("William");
      response = client.target(location).request().put(Entity.entity(customer, "application/example-java"));
      if (response.getStatus() != 204) throw new RuntimeException("Failed to update");


      // Show the update
      System.out.println("**** After Update ***");
      customer = client.target(location).request().get(Customer.class);
      System.out.println(customer);
   }

}
