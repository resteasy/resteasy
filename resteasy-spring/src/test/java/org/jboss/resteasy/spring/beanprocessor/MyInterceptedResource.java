package org.jboss.resteasy.spring.beanprocessor;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;


@Path("/intercepted")
public class MyInterceptedResource implements MyIntercepted
{
   private Customer customer;

   @GET
   @Produces("foo/bar")
   public Customer callGet()
   {
      return customer;
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
