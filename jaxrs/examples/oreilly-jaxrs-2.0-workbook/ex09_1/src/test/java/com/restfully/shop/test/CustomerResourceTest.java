package com.restfully.shop.test;

import com.restfully.shop.domain.Customer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
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

      System.out.println("*** GET XML Created Customer **");
      String xml = client.target(location).request()
                                .accept(MediaType.APPLICATION_XML_TYPE)
                                .get(String.class);
      System.out.println(xml);

      System.out.println("*** GET JSON Created Customer **");
      String json = client.target(location).request()
              .accept(MediaType.APPLICATION_JSON_TYPE)
              .get(String.class);
      System.out.println(json);
   }
}
