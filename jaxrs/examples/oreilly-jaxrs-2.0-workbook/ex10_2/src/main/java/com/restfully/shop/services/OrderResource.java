package com.restfully.shop.services;

import com.restfully.shop.domain.Order;
import com.restfully.shop.domain.Orders;
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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Path("/orders")
public class OrderResource
{
   private Map<Integer, Order> orderDB = new Hashtable<Integer, Order>();
   private AtomicInteger idCounter = new AtomicInteger();

   @POST
   @Consumes("application/xml")
   public Response createOrder(Order order, @Context UriInfo uriInfo)
   {
      order.setId(idCounter.incrementAndGet());
      orderDB.put(order.getId(), order);
      System.out.println("Created order " + order.getId());
      UriBuilder builder = uriInfo.getAbsolutePathBuilder();
      builder.path(Integer.toString(order.getId()));
      return Response.created(builder.build()).build();

   }

   @GET
   @Path("{id}")
   @Produces("application/xml")
   public Response getOrder(@PathParam("id") int id, @Context UriInfo uriInfo)
   {
      Order order = orderDB.get(id);
      if (order == null)
      {
         throw new WebApplicationException(Response.Status.NOT_FOUND);
      }
      Response.ResponseBuilder builder = Response.ok(order);
      if (!order.isCancelled()) addCancelHeader(uriInfo, builder);
      return builder.build();
   }

   protected void addCancelHeader(UriInfo uriInfo, Response.ResponseBuilder builder)
   {
      UriBuilder absolute = uriInfo.getAbsolutePathBuilder();
      URI cancelUrl = absolute.clone().path("cancel").build();
      builder.links(Link.fromUri(cancelUrl).rel("cancel").build());
   }

   @POST
   @Path("{id}/cancel")
   public void cancelOrder(@PathParam("id") int id)
   {
      Order order = orderDB.get(id);
      if (order == null)
      {
         throw new WebApplicationException(Response.Status.NOT_FOUND);
      }
      order.setCancelled(true);
   }


   @HEAD
   @Path("{id}")
   @Produces("application/xml")
   public Response getOrderHeaders(@PathParam("id") int id, @Context UriInfo uriInfo)
   {
      Order order = orderDB.get(id);
      if (order == null)
      {
         throw new WebApplicationException(Response.Status.NOT_FOUND);
      }
      Response.ResponseBuilder builder = Response.ok();
      builder.type("application/xml");
      if (!order.isCancelled()) addCancelHeader(uriInfo, builder);
      return builder.build();
   }

   @GET
   @Produces("application/xml")
   @Formatted
   public Response getOrders(@QueryParam("start") int start,
                             @QueryParam("size") @DefaultValue("2") int size,
                             @Context UriInfo uriInfo)
   {
      UriBuilder builder = uriInfo.getAbsolutePathBuilder();
      builder.queryParam("start", "{start}");
      builder.queryParam("size", "{size}");

      ArrayList<Order> list = new ArrayList<Order>();
      ArrayList<Link> links = new ArrayList<Link>();
      synchronized (orderDB)
      {
         int i = 0;
         for (Order order : orderDB.values())
         {
            if (i >= start && i < start + size) list.add(order);
            i++;
         }
         // next link
         if (start + size < orderDB.size())
         {
            int next = start + size;
            URI nextUri = builder.clone().build(next, size);
            Link nextLink = Link.fromUri(nextUri).rel("next").type("application/xml").build();
            links.add(nextLink);
         }
         // previous link
         if (start > 0)
         {
            int previous = start - size;
            if (previous < 0) previous = 0;
            URI previousUri = builder.clone().build(previous, size);
            Link previousLink = Link.fromUri(previousUri).rel("previous").type("application/xml").build();
            links.add(previousLink);
         }
      }
      Orders orders = new Orders();
      orders.setOrders(list);
      orders.setLinks(links);
      Response.ResponseBuilder responseBuilder = Response.ok(orders);
      addPurgeLinkHeader(uriInfo, responseBuilder);
      return responseBuilder.build();
   }

   protected void addPurgeLinkHeader(UriInfo uriInfo, Response.ResponseBuilder builder)
   {
      UriBuilder absolute = uriInfo.getAbsolutePathBuilder();
      URI purgeUri = absolute.clone().path("purge").build();
      builder.links(Link.fromUri(purgeUri).rel("purge").build());
   }

   @POST
   @Path("purge")
   public void purgeOrders()
   {
      synchronized (orderDB)
      {
         List<Order> orders = new ArrayList<Order>();
         orders.addAll(orderDB.values());
         for (Order order : orders)
         {
            if (order.isCancelled())
            {
               orderDB.remove(order.getId());
            }
         }
      }
   }

   @HEAD
   @Produces("application/xml")
   public Response getOrdersHeaders(@QueryParam("start") int start,
                                    @QueryParam("size") @DefaultValue("2") int size,
                                    @Context UriInfo uriInfo)
   {
      Response.ResponseBuilder builder = Response.ok();
      builder.type("application/xml");
      addPurgeLinkHeader(uriInfo, builder);
      return builder.build();
   }

}