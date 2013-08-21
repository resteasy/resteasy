package com.restfully.shop.services;

import com.restfully.shop.domain.Order;
import org.jboss.resteasy.annotations.providers.jaxb.Formatted;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("/orders")
public interface OrderResource
{
   @POST
   @Consumes("application/xml")
   Response createOrder(Order order, @Context UriInfo uriInfo);

   @POST
   @Path("purge")
   void purgeOrders();

   @HEAD
   @Produces("application/xml")
   Response getOrdersHeaders(@Context UriInfo uriInfo);

   @GET
   @Produces("application/xml")
   @Formatted
   Response getOrders(@QueryParam("start") int start,
                      @QueryParam("size") @DefaultValue("2") int size,
                      @Context UriInfo uriInfo);

   @POST
   @Path("{id}/cancel")
   void cancelOrder(@PathParam("id") int id);

   @GET
   @Path("{id}")
   @Produces("application/xml")
   Response getOrder(@PathParam("id") int id, @Context UriInfo uriInfo);

   @HEAD
   @Path("{id}")
   @Produces("application/xml")
   Response getOrderHeaders(@PathParam("id") int id, @Context UriInfo uriInfo);
}
