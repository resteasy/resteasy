package org.jboss.resteasy.spring.scanned;

import org.jboss.resteasy.spring.beanprocessor.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerService
{
   public Customer convert(String name)
   {
      return new Customer(name);
   }

   public String convert(Customer customer)
   {
      return customer.getName();
   }
}
