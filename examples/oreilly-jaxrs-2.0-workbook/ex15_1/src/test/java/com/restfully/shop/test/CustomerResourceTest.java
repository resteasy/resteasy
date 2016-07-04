package com.restfully.shop.test;

import com.restfully.shop.domain.Customer;
import com.restfully.shop.features.OneTimePasswordGenerator;
import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
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
              .request().post(Entity.xml(newCustomer));
      if (response.getStatus() != 201) throw new RuntimeException("Failed to create");
      String location = response.getLocation().toString();
      System.out.println("Location: " + location);
      response.close();

      System.out.println("*** GET Created Customer **");
      Customer customer = null;
      WebTarget target = client.target(location);
      try
      {
         customer = target.request().get(Customer.class);
         Assert.fail(); // should have thrown an exception
      }
      catch (NotAuthorizedException e)
      {
      }

      target.register(new OneTimePasswordGenerator("bburke", "geheim"));

      customer = target.request().get(Customer.class);
      System.out.println(customer);

      customer.setFirstName("William");
      response = target.request().put(Entity.xml(customer));
      if (response.getStatus() != 204) throw new RuntimeException("Failed to update");


      // Show the update
      System.out.println("**** After Update ***");
      customer = target.request().get(Customer.class);
      System.out.println(customer);

      // only allowed to update once per day
      customer.setFirstName("Bill");
      response = target.request().put(Entity.xml(customer));
      Assert.assertEquals(Response.Status.FORBIDDEN, response.getStatusInfo());

   }
}
