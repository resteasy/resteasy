package org.jboss.resteasy.test.spring.deployment.resource;

public class SpringBeanProcessorCustomer {
   private String name;

   public SpringBeanProcessorCustomer() {
   }

   public SpringBeanProcessorCustomer(final String name) {
      this.name = name;
   }


   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }
}
