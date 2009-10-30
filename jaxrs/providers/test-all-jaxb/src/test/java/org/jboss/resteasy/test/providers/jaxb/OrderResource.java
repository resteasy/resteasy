/*
 * JBoss, the OpenSource J2EE webOS Distributable under LGPL license. See terms of license at gnu.org.
 */
package org.jboss.resteasy.test.providers.jaxb;

import org.jboss.resteasy.test.providers.jaxb.data.Item;
import org.jboss.resteasy.test.providers.jaxb.data.Order;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.io.InputStream;

/**
 * A OrderResource.
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
@Path("/jaxb/orders")
@Consumes({"application/xml", "application/fastinfoset", "application/json"})
@Produces({"application/xml", "application/fastinfoset", "application/json"})
public class OrderResource
{

   @GET
   @Path("/{orderId}")
   public Order getOrderById(@PathParam("orderId") String orderId)
   {
      Order order = getOrderFromFileSystem(orderId);
      return order;
   }

   /**
    * FIXME Comment this
    *
    * @param order
    * @return
    */
   @POST
   public Response createOrder(Order order)
   {
      return null;
   }

   @PUT
   @Path("/{orderId}")
   public Order updateOrder(Order order, @PathParam("orderId") String orderId)
   {
      Item updatedItem = order.getItem(0);
      updatedItem.setQuantity(updatedItem.getQuantity() + 1);
      assert updatedItem.getOrder().equals(order);
      Item item = new Item();
      item.setNote("New Item");
      item.setPrice(21.99d);
      item.setQuantity(1);
      item.setTitle("New Thing");
      order.addItem(item);
      return order;
   }

   private Order getOrderFromFileSystem(String orderId)
   {
      StringBuilder order = new StringBuilder("orders/");
      order.append(orderId).append(".xml");
      InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(order.toString());
      return JAXBHelper.unmarshall(Order.class, in).getValue();
   }
}
