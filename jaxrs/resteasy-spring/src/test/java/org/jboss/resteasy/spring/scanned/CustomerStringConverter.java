package org.jboss.resteasy.spring.scanned;

import org.jboss.resteasy.spi.StringConverter;
import org.jboss.resteasy.spring.beanprocessor.Customer;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.ext.Provider;

@Provider
public class CustomerStringConverter implements StringConverter<Customer>
{

   // this isn't a complex service, but it provides a test to confirm that
   // RESTEasy doesn't muck up the @Autowired annotation handling in the Spring
   // life-cycle
   @Autowired
   CustomerService service;

   @Override
   public Customer fromString(String name)
   {
      return service.convert(name);
   }

   public String toString(Customer customer)
   {
      return service.convert(customer);
   }

}
