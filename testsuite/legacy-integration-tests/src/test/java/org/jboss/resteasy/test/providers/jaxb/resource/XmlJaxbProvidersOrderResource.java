package org.jboss.resteasy.test.providers.jaxb.resource;

import org.junit.Assert;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.io.InputStream;

@Path("/jaxb/orders")
@Consumes({"application/xml", "application/fastinfoset", "application/json"})
@Produces({"application/xml", "application/fastinfoset", "application/json"})
public class XmlJaxbProvidersOrderResource {

    @GET
    @Path("/{orderId}")
    public Order getOrderById(@PathParam("orderId") String orderId) {
        Order order = getOrderFromFileSystem(orderId);
        return order;
    }

    @POST
    public Response createOrder(Order order) {
        return null;
    }

    @PUT
    @Path("/{orderId}")
    public Order updateOrder(Order order, @PathParam("orderId") String orderId) {
        Item updatedItem = order.getItem(0);
        updatedItem.setQuantity(updatedItem.getQuantity() + 1);
        Item item = new Item();
        item.setNote("New Item");
        item.setPrice(21.99d);
        item.setQuantity(1);
        item.setTitle("New Thing");
        order.addItem(item);
        return order;
    }

    private Order getOrderFromFileSystem(String orderId) {
        StringBuilder order = new StringBuilder("orders/");
        order.append(orderId).append(".xml");
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("org/jboss/resteasy/test/providers/jaxb/" + order.toString());
        Assert.assertNotEquals(null, in);
        return XmlJaxbProvidersHelper.unmarshall(Order.class, in).getValue();
    }
}
