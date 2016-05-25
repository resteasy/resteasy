package com.restfully.shop.test;

import com.restfully.shop.domain.Customer;
import com.restfully.shop.domain.Customers;
import com.restfully.shop.domain.LineItem;
import com.restfully.shop.domain.Order;
import com.restfully.shop.domain.Product;
import com.restfully.shop.domain.Products;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Date;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ShoppingTest
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

   public void populateDB() throws Exception
   {
      Response response = client.target("http://localhost:8080/services/shop").request().head();
      javax.ws.rs.core.Link products = response.getLink("products");
      response.close();

      System.out.println("** Populate Products");

      Product product = new Product();
      product.setName("iPhone");
      product.setCost(199.99);
      response = client.target(products).request().post(Entity.xml(product));
      Assert.assertEquals(201, response.getStatus());
      response.close();

      product = new Product();
      product.setName("MacBook Pro");
      product.setCost(3299.99);
      response = client.target(products).request().post(Entity.xml(product));
      Assert.assertEquals(201, response.getStatus());
      response.close();

      product = new Product();
      product.setName("iPod");
      product.setCost(49.99);
      response = client.target(products).request().post(Entity.xml(product));
      Assert.assertEquals(201, response.getStatus());
      response.close();
   }

   @Test
   public void testCreateOrder() throws Exception
   {
      populateDB();

      Response response = client.target("http://localhost:8080/services/shop").request().head();
      javax.ws.rs.core.Link customers = response.getLink("customers");
      javax.ws.rs.core.Link products = response.getLink("products");
      javax.ws.rs.core.Link orders = response.getLink("orders");
      response.close();

      System.out.println("** Buy an iPhone for Bill Burke");
      System.out.println();
      System.out.println("** First see if Bill Burke exists as a customer");
      Customers custs = client.target(customers)
              .queryParam("firstName", "Bill")
              .queryParam("lastName", "Burke")
              .request().get(Customers.class);
      Customer customer = null;
      if (custs.getCustomers().size() > 0)
      {
         System.out.println("- Found a Bill Burke in the database, using that");
         customer = custs.getCustomers().iterator().next();
      }
      else
      {
         System.out.println("- Cound not find a Bill Burke in the database, creating one.");
         customer = new Customer();
         customer.setFirstName("Bill");
         customer.setLastName("Burke");
         customer.setStreet("222 Dartmouth Street");
         customer.setCity("Boston");
         customer.setState("MA");
         customer.setZip("02115");
         customer.setCountry("USA");
         response = client.target(customers).request().post(Entity.xml(customer));
         Assert.assertEquals(201, response.getStatus());
         URI uri = response.getLocation();
         response.close();

         customer = client.target(uri).request().get(Customer.class);
      }

      System.out.println();
      System.out.println("Search for iPhone in the Product database");
      Products prods = client.target(products)
              .queryParam("name", "iPhone")
              .request()
              .get(Products.class);
      Product product = null;
      if (prods.getProducts().size() > 0)
      {
         System.out.println("- Found iPhone in the database.");
         product = prods.getProducts().iterator().next();
      }
      else
      {
         throw new RuntimeException("Failed to find an iPhone in the database!");
      }

      System.out.println();
      System.out.println("** Create Order for iPhone");
      LineItem item = new LineItem();
      item.setProduct(product);
      item.setQuantity(1);
      Order order = new Order();
      order.setTotal(product.getCost());
      order.setCustomer(customer);
      order.setDate(new Date().toString());
      order.getLineItems().add(item);
      response = client.target(orders).request().post(Entity.xml(order));
      Assert.assertEquals(201, response.getStatus());
      response.close();

      System.out.println();
      System.out.println("** Show all orders.");
      String xml = client.target(orders).request().get(String.class);
      System.out.println(xml);
   }
}
