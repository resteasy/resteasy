package org.jboss.resteasy.test.providers.jaxb;

import org.jboss.resteasy.test.providers.jaxb.data.Order;
import org.jboss.resteasy.test.providers.jaxb.generated.order.Ordertype;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * A XmlOrderClient.
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
@Consumes({"application/xml"})
@Produces({"application/xml"})
public interface XmlOrderClient
{

   @GET
   @Path("/{orderId}")
   Order getOrderById(@PathParam("orderId") String orderId);

   /**
    * FIXME Comment this
    *
    * @param order
    * @return
    */
   @POST
   Response createOrder(Ordertype order);

   @PUT
   @Path("/{orderId}")
   Order updateOrder(Order order, @PathParam("orderId") String orderId);

}
