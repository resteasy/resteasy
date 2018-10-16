package org.jboss.resteasy.test.spring.deployment.resource;

import org.springframework.stereotype.Component;

@Component
public class SpringBeanProcessorCustomerService {
   public SpringBeanProcessorCustomer convert(String name) {
      return new SpringBeanProcessorCustomer(name);
   }

   public String convert(SpringBeanProcessorCustomer springBeanProcessorCustomer) {
      return springBeanProcessorCustomer.getName();
   }
}
