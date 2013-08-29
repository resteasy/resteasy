package com.restfully.shop.test;

import com.restfully.shop.domain.Customer;
import com.restfully.shop.domain.LineItem;
import com.restfully.shop.domain.Order;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;


/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class OrderResourceTest
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
   public void testCreateCancelPurge() throws Exception
   {
      String base = "http://localhost:8080/services/shop";
      Response response = client.target(base).request().head();

      Link customers = response.getLink("customers");
      Link orders = response.getLink("orders");
      response.close();

      System.out.println("** Create a customer through this URL: " + customers.getUri().toString());

      Customer customer = new Customer();
      customer.setFirstName("Bill");
      customer.setLastName("Burke");
      customer.setStreet("10 Somewhere Street");
      customer.setCity("Westford");
      customer.setState("MA");
      customer.setZip("01711");
      customer.setCountry("USA");

      response = client.target(customers).request().post(Entity.xml(customer));
      Assert.assertEquals(201, response.getStatus());
      response.close();


      Order order = new Order();
      order.setTotal("$199.99");
      order.setCustomer(customer);
      order.setDate(new Date().toString());
      LineItem item = new LineItem();
      item.setCost("$199.99");
      item.setProduct("iPhone");
      order.setLineItems(new ArrayList<LineItem>());
      order.getLineItems().add(item);

      System.out.println();
      System.out.println("** Create an order through this URL: " + orders.getUri().toString());
      response = client.target(orders).request().post(Entity.xml(order));
      Assert.assertEquals(201, response.getStatus());
      URI createdOrderUrl = response.getLocation();
      response.close();

      System.out.println();
      System.out.println("** New list of orders");
      response = client.target(orders).request().get();
      String orderList = response.readEntity(String.class);
      System.out.println(orderList);
      Link purge = response.getLink("purge");
      response.close();

      response = client.target(createdOrderUrl).request().head();
      Link cancel = response.getLink("cancel");
      response.close();
      if (cancel != null)
      {
         System.out.println("** Canceling the order at URL: " + cancel.getUri().toString());
         response = client.target(cancel).request().post(null);
         Assert.assertEquals(204, response.getStatus());
         response.close();
      }

      System.out.println();
      System.out.println("** New list of orders after cancel: ");
      orderList = client.target(orders).request().get(String.class);
      System.out.println(orderList);

      System.out.println();
      System.out.println("** Purge cancelled orders at URL: " + purge.getUri().toString());
      response = client.target(purge).request().post(null);
      Assert.assertEquals(204, response.getStatus());
      response.close();

      System.out.println();
      System.out.println("** New list of orders after purge: ");
      orderList = client.target(orders).request().get(String.class);
      System.out.println(orderList);
   }
}
