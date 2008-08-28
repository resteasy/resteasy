/*
 * JBoss, the OpenSource J2EE webOS Distributable under LGPL license. See terms of license at gnu.org.
 */
package org.jboss.resteasy.test.providers.jaxb;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.test.providers.jaxb.generated.order.Ordertype;

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
   Ordertype getOrderById(@PathParam("orderId") String orderId);

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
   Ordertype updateOrder(Ordertype order, @PathParam("orderId") String orderId);

}
