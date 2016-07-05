package org.jboss.resteasy.test.providers.jaxb.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

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
