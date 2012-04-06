package org.jboss.resteasy.spring.beanprocessor;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import junit.framework.Assert;

@Path("/prototyped")
public class MyPrototypedResource
{
   private Customer customer;

   private int counter = 0;

   @PathParam("id")
   private String id;

   public MyPrototypedResource()
   {
      System.out.println("here");
   }

   @GET
   @Path("{id}")
   @Produces("text/plain")
   public String callGet()
   {
      Assert.assertEquals(id, "1");
      return customer.getName() + (counter++);
   }

   public Customer getCustomer()
   {
      return customer;
   }

   public void setCustomer(Customer customer)
   {
      this.customer = customer;
   }

}