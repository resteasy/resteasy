package com.restfully.shop.services;

import com.restfully.shop.domain.Customer;
import com.restfully.shop.domain.Customers;
import com.restfully.shop.persistence.CustomerEntity;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class CustomerResourceBean implements CustomerResource
{
   @PersistenceContext
   private EntityManager em;

   public Response createCustomer(Customer customer, UriInfo uriInfo)
   {
      CustomerEntity entity = new CustomerEntity();
      domain2entity(entity, customer);
      em.persist(entity);
      em.flush();

      System.out.println("Created customer " + entity.getId());
      UriBuilder builder = uriInfo.getAbsolutePathBuilder();
      builder.path(Integer.toString(entity.getId()));
      return Response.created(builder.build()).build();

   }

   public Customer getCustomer(int id)
   {
      CustomerEntity customer = em.getReference(CustomerEntity.class, id);
      return entity2domain(customer);
   }

   public static void domain2entity(CustomerEntity entity, Customer customer)
   {
      entity.setId(customer.getId());
      entity.setFirstName(customer.getFirstName());
      entity.setLastName(customer.getLastName());
      entity.setStreet(customer.getStreet());
      entity.setCity(customer.getCity());
      entity.setState(customer.getState());
      entity.setZip(customer.getZip());
      entity.setCountry(customer.getCountry());
   }

   public static Customer entity2domain(CustomerEntity entity)
   {
      Customer cust = new Customer();
      cust.setId(entity.getId());
      cust.setFirstName(entity.getFirstName());
      cust.setLastName(entity.getLastName());
      cust.setStreet(entity.getStreet());
      cust.setCity(entity.getCity());
      cust.setState(entity.getState());
      cust.setZip(entity.getZip());
      cust.setCountry(entity.getCountry());
      return cust;
   }

   public Customers getCustomers(int start,
                                 int size,
                                 String firstName,
                                 String lastName,
                                 UriInfo uriInfo)
   {
      UriBuilder builder = uriInfo.getAbsolutePathBuilder();
      builder.queryParam("start", "{start}");
      builder.queryParam("size", "{size}");

      ArrayList<Customer> list = new ArrayList<Customer>();
      ArrayList<Link> links = new ArrayList<Link>();

      Query query = null;
      if (firstName != null && lastName != null)
      {
         query = em.createQuery("select c from Customer c where c.firstName=:first and c.lastName=:last");
         query.setParameter("first", firstName);
         query.setParameter("last", lastName);

      }
      else if (lastName != null)
      {
         query = em.createQuery("select c from Customer c where c.lastName=:last");
         query.setParameter("last", lastName);
      }
      else
      {
         query = em.createQuery("select c from Customer c");
      }

      List customerEntities = query.setFirstResult(start)
              .setMaxResults(size)
              .getResultList();

      for (Object obj : customerEntities)
      {
         CustomerEntity entity = (CustomerEntity) obj;
         list.add(entity2domain(entity));
      }
      // next link
      // If the size returned is equal then assume there is a next
      if (customerEntities.size() == size)
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
      Customers customers = new Customers();
      customers.setCustomers(list);
      customers.setLinks(links);
      return customers;
   }

}
