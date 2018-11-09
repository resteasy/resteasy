package org.jboss.resteasy.test.spring.deployment.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;


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
