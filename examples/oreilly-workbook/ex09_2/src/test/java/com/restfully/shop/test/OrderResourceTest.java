package com.restfully.shop.test;

import com.restfully.shop.domain.Customer;
import com.restfully.shop.domain.LineItem;
import com.restfully.shop.domain.Link;
import com.restfully.shop.domain.Order;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class OrderResourceTest
{
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
   public void testCreateCancelPurge() throws Exception
   {
      //RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
      String url = "http://localhost:9095/shop";
      ClientRequest request = new ClientRequest(url);
      ClientResponse response = request.head();
      Map<String, Link> shoppingLinks = processLinkHeaders(response);

      Link customers = shoppingLinks.get("customers");
      System.out.println("** Create a customer through this URL: " + customers.getHref());

      Customer customer = new Customer();
      customer.setFirstName("Bill");
      customer.setLastName("Burke");
      customer.setStreet("10 Somewhere Street");
      customer.setCity("Westford");
      customer.setState("MA");
      customer.setZip("01711");
      customer.setCountry("USA");

      request = new ClientRequest(customers.getHref());
      request.body("application/xml", customer);
      response = request.post();
      Assert.assertEquals(201, response.getStatus());

      Link orders = shoppingLinks.get("orders");

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
      System.out.println("** Create an order through this URL: " + orders.getHref());
      request = new ClientRequest(orders.getHref());
      request.body("application/xml", order);
      response = request.post();
      Assert.assertEquals(201, response.getStatus());
      String createdOrderUrl = (String) response.getHeaders().getFirst("Location");

      System.out.println();
      System.out.println("** New list of orders");
      request = new ClientRequest(orders.getHref());
      response = request.get();
      System.out.println(response.getEntity(String.class));
      Map<String, Link> ordersLinks = processLinkHeaders(response);

      request = new ClientRequest(createdOrderUrl);
      response = request.head();
      Map<String, Link> orderLinks = processLinkHeaders(response);

      Link cancel = orderLinks.get("cancel");
      if (cancel != null)
      {
         System.out.println("** Canceling the order at URL: " + cancel.getHref());
         request = new ClientRequest(cancel.getHref());
         response = request.post();
         Assert.assertEquals(204, response.getStatus());
      }

      System.out.println();
      System.out.println("** New list of orders after cancel: ");
      request = new ClientRequest(orders.getHref());
      response = request.get();
      System.out.println(response.getEntity(String.class));

      System.out.println();
      Link purge = ordersLinks.get("purge");
      System.out.println("** Purge cancelled orders at URL: " + purge.getHref());
      request = new ClientRequest(purge.getHref());
      response = request.post();
      Assert.assertEquals(204, response.getStatus());

      System.out.println();
      System.out.println("** New list of orders after purge: ");
      request = new ClientRequest(orders.getHref());
      response = request.get();
      System.out.println(response.getEntity(String.class));
   }
}
