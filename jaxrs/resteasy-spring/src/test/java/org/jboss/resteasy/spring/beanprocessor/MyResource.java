package org.jboss.resteasy.spring.beanprocessor;

import org.jboss.resteasy.spring.scanned.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path("/")
public class MyResource
{
   
   @Autowired
   CustomerService customerService;
   
   private Customer customer;

   @GET
   @Produces("foo/bar")
   public Customer callGet()
   {
      return customer;
   }
   
   @Path("customer-name")
   @GET
   @Produces("foo/bar")
   public Customer getCustomer(@QueryParam("name") String name)
   {
      return customerService.convert(name);
   }

   @Path("customer-object")
   @GET
   @Produces("text/String")
   public String getName(@QueryParam("customer") Customer customer)
   {
      return customerService.convert(customer);
   }
   
   public void setCustomer(Customer customer)
   {
      this.customer = customer;
   }
}
