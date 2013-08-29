package com.restfully.shop.test;

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

      String xml = "<customer>"
              + "<first-name>Sacha</first-name>"
              + "<last-name>Labourey</last-name>"
              + "<street>Le Swiss Street</street>"
              + "<city>Neuchatel</city>"
              + "<state>French</state>"
              + "<zip>211222</zip>"
              + "<country>Switzerland</country>"
              + "</customer>";

      Response response = client.target("http://localhost:8080/services/customers/europe-db")
              .request().post(Entity.xml(xml));
      if (response.getStatus() != 201) throw new RuntimeException("Failed to create");
      String location = response.getLocation().toString();
      System.out.println("Location: " + location);
      response.close();

      System.out.println("*** GET Created Customer **");
      String customer = client.target(location).request().get(String.class);
      System.out.println(customer);
   }

   @Test
   public void testFirstLastCustomerResource() throws Exception
   {
      System.out.println("*** Create a new Customer ***");

      String xml = "<customer>"
              + "<first-name>Bill</first-name>"
              + "<last-name>Burke</last-name>"
              + "<street>263 Clarendon Street</street>"
              + "<city>Boston</city>"
              + "<state>MA</state>"
              + "<zip>02116</zip>"
              + "<country>USA</country>"
              + "</customer>";

      Response response = client.target("http://localhost:8080/services/customers/northamerica-db")
              .request().post(Entity.xml(xml));
      if (response.getStatus() != 201) throw new RuntimeException("Failed to create");
      String location = response.getLocation().toString();
      System.out.println("Location: " + location);
      response.close();

      System.out.println("*** GET Created Customer **");
      String customer = client.target("http://localhost:8080/services/customers/northamerica-db/Bill-Burke").request().get(String.class);
      System.out.println(customer);
   }
}
