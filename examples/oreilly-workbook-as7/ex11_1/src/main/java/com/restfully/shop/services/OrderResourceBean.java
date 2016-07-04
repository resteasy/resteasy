package com.restfully.shop.services;

import com.restfully.shop.domain.Customer;
import com.restfully.shop.domain.LineItem;
import com.restfully.shop.domain.Link;
import com.restfully.shop.domain.Order;
import com.restfully.shop.domain.Orders;
import com.restfully.shop.domain.Product;
import com.restfully.shop.persistence.CustomerEntity;
import com.restfully.shop.persistence.LineItemEntity;
import com.restfully.shop.persistence.OrderEntity;
import com.restfully.shop.persistence.ProductEntity;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class OrderResourceBean implements OrderResource
{
   @PersistenceContext
   private EntityManager em;

   protected static void domain2entity(OrderEntity entity, Order order)
   {
      entity.setId(order.getId());
      entity.setCancelled(order.isCancelled());
      entity.setDate(order.getDate());
      entity.setTotal(order.getTotal());
      CustomerEntity customerEntity = new CustomerEntity();
      CustomerResourceBean.domain2entity(customerEntity, order.getCustomer());
      entity.setCustomer(customerEntity);
      for (LineItem item : order.getLineItems())
      {
         LineItemEntity lineItem = new LineItemEntity();
         domain2entity(lineItem, item);
         entity.getLineItems().add(lineItem);
      }

   }

   public static void domain2entity(LineItemEntity entity, LineItem item)
   {
      entity.setId(item.getId());
      ProductEntity product = new ProductEntity();
      ProductResourceBean.domain2entity(product, item.getProduct());
      entity.setProduct(product);
      entity.setQuantity(item.getQuantity());
   }

   public static Order entity2domain(OrderEntity entity)
   {
      Order order = new Order();
      order.setId(entity.getId());
      order.setCancelled(entity.isCancelled());
      order.setDate(entity.getDate());
      order.setTotal(entity.getTotal());
      CustomerEntity customerEntity = entity.getCustomer();
      Customer customer = CustomerResourceBean.entity2domain(customerEntity);
      order.setCustomer(customer);
      for (LineItemEntity item : entity.getLineItems())
      {
         LineItem lineItem = entity2domain(item);
         order.getLineItems().add(lineItem);
      }
      return order;
   }

   public static LineItem entity2domain(LineItemEntity entity)
   {
      LineItem item = new LineItem();
      item.setId(entity.getId());
      Product product = ProductResourceBean.entity2domain(entity.getProduct());
      item.setProduct(product);
      item.setQuantity(entity.getQuantity());
      return item;
   }

   public static void addPurgeLinkHeader(UriInfo uriInfo, Response.ResponseBuilder builder)
   {
      UriBuilder absolute = uriInfo.getAbsolutePathBuilder();
      String purgeUrl = absolute.clone().path("purge").build().toString();
      builder.header("Link", new Link("purge", purgeUrl, null));
   }

   public Response createOrder(Order order, UriInfo uriInfo)
   {
      OrderEntity entity = new OrderEntity();
      domain2entity(entity, order);
      em.persist(entity);
      em.flush();
      System.out.println("Created order " + entity.getId());
      UriBuilder builder = uriInfo.getAbsolutePathBuilder();
      builder.path(Integer.toString(entity.getId()));
      return Response.created(builder.build()).build();

   }

   public void purgeOrders()
   {
      int updated = em.createQuery("delete from PurchaseOrder o where o.cancelled = true").executeUpdate();
   }

   public Response getOrdersHeaders(UriInfo uriInfo)
   {
      Response.ResponseBuilder builder = Response.ok();
      builder.type("application/xml");
      addPurgeLinkHeader(uriInfo, builder);
      return builder.build();
   }

   public Response getOrders(int start,
                             int size,
                             UriInfo uriInfo)
   {
      UriBuilder builder = uriInfo.getAbsolutePathBuilder();
      builder.queryParam("start", "{start}");
      builder.queryParam("size", "{size}");

      ArrayList<Order> list = new ArrayList<Order>();
      ArrayList<Link> links = new ArrayList<Link>();

      List orderEntities = em.createQuery("select p from PurchaseOrder p")
              .setFirstResult(start)
              .setMaxResults(size)
              .getResultList();

      for (Object obj : orderEntities)
      {
         OrderEntity entity = (OrderEntity) obj;
         Order order = entity2domain(entity);
         String self = uriInfo.getAbsolutePathBuilder().path(Integer.toString(order.getId())).build().toString();
         order.addLink(new Link("self", self, "application/xml"));
         if (!order.isCancelled())
         {
            String cancel = uriInfo.getAbsolutePathBuilder().path(Integer.toString(order.getId())).path("cancel").build().toString();
            order.addLink(new Link("cancel", cancel, "application/xml"));
         }
         list.add(order);
      }
      // next link
      // If the size returned is equal then assume there is a next
      if (orderEntities.size() == size)
      {
         int next = start + size;
         URI nextUri = builder.clone().build(next, size);
         Link nextLink = new Link("next", nextUri.toString(), "application/xml");
         links.add(nextLink);
      }
      // previous link
      if (start > 0)
      {
         int previous = start - size;
         if (previous < 0) previous = 0;
         URI previousUri = builder.clone().build(previous, size);
         Link previousLink = new Link("previous", previousUri.toString(), "application/xml");
         links.add(previousLink);
      }
      Orders orders = new Orders();
      orders.setOrders(list);
      orders.setLinks(links);
      Response.ResponseBuilder responseBuilder = Response.ok(orders);
      addPurgeLinkHeader(uriInfo, responseBuilder);
      return responseBuilder.build();
   }

   public static void addCancelHeader(UriInfo uriInfo, Response.ResponseBuilder builder)
   {
      UriBuilder absolute = uriInfo.getAbsolutePathBuilder();
      String cancelUrl = absolute.clone().path("cancel").build().toString();
      builder.header("Link", new Link("cancel", cancelUrl, null));
   }

   public void cancelOrder(int id)
   {
      OrderEntity order = em.getReference(OrderEntity.class, id);
      order.setCancelled(true);
   }


   public Response getOrder(int id, UriInfo uriInfo)
   {
      OrderEntity entity = em.getReference(OrderEntity.class, id);
      Order order = entity2domain(entity);
      String self = uriInfo.getAbsolutePathBuilder().build().toString();
      order.addLink(new Link("self", self, "application/xml"));
      if (!order.isCancelled())
      {
         String cancel = uriInfo.getAbsolutePathBuilder().path("cancel").build().toString();
         order.addLink(new Link("cancel", cancel, "application/xml"));
      }

      Response.ResponseBuilder builder = Response.ok(order);
      if (!order.isCancelled()) addCancelHeader(uriInfo, builder);
      return builder.build();
   }

   public Response getOrderHeaders(int id, UriInfo uriInfo)
   {
      OrderEntity order = em.getReference(OrderEntity.class, id);
      Response.ResponseBuilder builder = Response.ok();
      builder.type("application/xml");
      if (!order.isCancelled()) addCancelHeader(uriInfo, builder);
      return builder.build();
   }
}