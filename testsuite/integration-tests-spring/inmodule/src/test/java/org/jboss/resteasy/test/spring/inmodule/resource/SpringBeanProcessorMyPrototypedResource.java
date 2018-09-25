package org.jboss.resteasy.test.spring.inmodule.resource;

import org.jboss.logging.Logger;
import org.junit.Assert;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("/prototyped")
public class SpringBeanProcessorMyPrototypedResource {
   private SpringBeanProcessorCustomer springBeanProcessorCustomer;

   private int counter = 0;
   private static Logger logger = Logger.getLogger(SpringBeanProcessorMyPrototypedResource.class);

   public SpringBeanProcessorMyPrototypedResource() {
      logger.info("here");
   }

   @GET
   @Path("{id}")
   @Produces("text/plain")
   public String callGet(@PathParam("id") String id) {
      Assert.assertEquals("Got unexpected value", "1", id);
      return springBeanProcessorCustomer.getName() + (counter++);
   }

   public SpringBeanProcessorCustomer getSpringBeanProcessorCustomer() {
      return springBeanProcessorCustomer;
   }

   public void setSpringBeanProcessorCustomer(SpringBeanProcessorCustomer springBeanProcessorCustomer) {
      this.springBeanProcessorCustomer = springBeanProcessorCustomer;
   }

}
