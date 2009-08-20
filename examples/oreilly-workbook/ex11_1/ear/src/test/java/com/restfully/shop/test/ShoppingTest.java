package com.restfully.shop.test;

import com.restfully.shop.domain.Customer;
import com.restfully.shop.domain.Customers;
import com.restfully.shop.domain.LineItem;
import com.restfully.shop.domain.Link;
import com.restfully.shop.domain.Order;
import com.restfully.shop.domain.Product;
import com.restfully.shop.domain.Products;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ShoppingTest
{
   @BeforeClass
   public static void init()
   {
      RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
   }

   protected Map<String, Link> processLinkHeaders(ClientResponse response)
   {
      List<String> linkHeaders = (List<String>) response.getHeaders().get("Link");
      Map<String, Link> links = new HashMap<String, Link>();
      for (String header : linkHeaders)
      {
         Link link = Link.valueOf(header);
         links.put(link.getRelationship(), link);
      }
      return links;
   }


   @Test
   public void testPopulateDB() throws Exception
   {
      String url = "http://localhost:9095/shop";
      ClientRequest request = new ClientRequest("http://localhost:8080/ex11_1-war/shop");
      ClientResponse response = request.head();
      Map<String, Link> shoppingLinks = processLinkHeaders(response);

      System.out.println("** Populate Products");
      request = new ClientRequest(shoppingLinks.get("products").getHref());

      Product product = new Product();
      product.setName("iPhone");
      product.setCost(199.99);
      request.body("application/xml", product);
      response = request.post();
      Assert.assertEquals(201, response.getStatus());

      product = new Product();
      product.setName("MacBook Pro");
      product.setCost(3299.99);
      request.body("application/xml", product);
      response = request.post();
      Assert.assertEquals(201, response.getStatus());

      product = new Product();
      product.setName("iPod");
      product.setCost(49.99);
      request.body("application/xml", product);
      response = request.post();
      Assert.assertEquals(201, response.getStatus());

   }

   @Test
   public void testCreateOrder() throws Exception
   {
      String url = "http://localhost:9095/shop";
      ClientRequest request = new ClientRequest("http://localhost:8080/ex11_1-war/shop");
      ClientResponse response = request.head();
      Map<String, Link> shoppingLinks = processLinkHeaders(response);

      System.out.println("** Buy an iPhone for Bill Burke");
      System.out.println();
      System.out.println("** First see if Bill Burke exists as a customer");
      request = new ClientRequest(shoppingLinks.get("customers").getHref() + "?firstName=Bill&lastName=Burke");
      Customers customers = request.getTarget(Customers.class);
      Customer customer = null;
      if (customers.getCustomers().size() > 0)
      {
         System.out.println("- Found a Bill Burke in the database, using that");
         customer = customers.getCustomers().iterator().next();
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
         request = new ClientRequest(shoppingLinks.get("customers").getHref());
         request.body("application/xml", customer);
         response = request.post();
         Assert.assertEquals(201, response.getStatus());
         String uri = (String) response.getHeaders().getFirst("Location");

         request = new ClientRequest(uri);
         customer = request.getTarget(Customer.class);
      }

      System.out.println();
      System.out.println("Search for iPhone in the Product database");
      request = new ClientRequest(shoppingLinks.get("products").getHref() + "?name=iPhone");
      Products products = request.getTarget(Products.class);
      Product product = null;
      if (products.getProducts().size() > 0)
      {
         System.out.println("- Found iPhone in the database.");
         product = products.getProducts().iterator().next();
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
      request = new ClientRequest(shoppingLinks.get("orders").getHref());
      request.body("application/xml", order);
      response = request.post();
      Assert.assertEquals(201, response.getStatus());

      System.out.println();
      System.out.println("** Show all orders.");
      request = new ClientRequest(shoppingLinks.get("orders").getHref());
      String xml = request.getTarget(String.class);
      System.out.println(xml);


   }
}
