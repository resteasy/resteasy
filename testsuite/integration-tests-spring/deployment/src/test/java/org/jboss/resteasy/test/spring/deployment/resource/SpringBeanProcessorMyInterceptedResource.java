package org.jboss.resteasy.test.spring.deployment.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;


@Path("/intercepted")
public class SpringBeanProcessorMyInterceptedResource implements SpringBeanProcessorMyIntercepted {
   private SpringBeanProcessorCustomer springBeanProcessorCustomer;

   @GET
   @Produces("foo/bar")
   public SpringBeanProcessorCustomer callGet() {
      return springBeanProcessorCustomer;
   }

   public SpringBeanProcessorCustomer getSpringBeanProcessorCustomer() {
      return springBeanProcessorCustomer;
   }

   public void setSpringBeanProcessorCustomer(SpringBeanProcessorCustomer springBeanProcessorCustomer) {
      this.springBeanProcessorCustomer = springBeanProcessorCustomer;
   }
}
