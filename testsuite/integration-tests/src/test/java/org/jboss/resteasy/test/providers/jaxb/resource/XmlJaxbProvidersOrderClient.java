package org.jboss.resteasy.test.providers.jaxb.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

@Path("/jaxb/orders")
@Consumes({"application/xml"})
@Produces({"application/xml"})
public interface XmlJaxbProvidersOrderClient {

   @GET
   @Path("/{orderId}")
   Order getOrderById(@PathParam("orderId") String orderId);

   @POST
   Response createOrder(Ordertype order);

   @PUT
   @Path("/{orderId}")
   Order updateOrder(Order order, @PathParam("orderId") String orderId);

}
